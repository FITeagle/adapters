package org.fiteagle.abstractAdapter.dm;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by vju on 8/20/14.
 */

/**
 * MDB to listen for incoming Messages
 * Do not forget to annotate as MDB
 */
@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public abstract class AbstractMDBListener implements MessageListener {


    public abstract AbstractAdapter getAdapter();

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public abstract String responseConfigure(Message requestMessage);
    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public abstract String responseDiscover(Message requestMessage);
    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public abstract String responseCreate(Message requestMessage);
    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public abstract String responseRelease(Message requestMessage);

    /**
     * Method for checking if this message belongs to this adapter
     * @param requestMessage
     * @return true if message belongs to adapter
     */
    public boolean messageBelongsToAdapter(Message requestMessage) {
        boolean isForAdapter = false;
        try {
            if (requestMessage.getStringProperty(IMessageBus.RDF) != null) {
                String inputRDF = requestMessage.getStringProperty(IMessageBus.RDF);
                Model inputModel = ModelFactory.createDefaultModel();
                InputStream is = new ByteArrayInputStream(inputRDF.getBytes());
                inputModel.read(is, null, requestMessage.getStringProperty(IMessageBus.SERIALIZATION));

                StmtIterator mightyRobotIterator = inputModel.listStatements(new SimpleSelector(null, RDF.type, getAdapter().getInstanceClassResource()));
                while(mightyRobotIterator.hasNext()){
                    isForAdapter = true;
                    break;
                }


            }
        } catch (Exception e) {

        }
        return isForAdapter;
    }

    /**
     * Implementation of the onMessage method to handle the request
     * @param requestMessage
     */
    public void onMessage(final Message requestMessage) {
        messageBelongsToAdapter(requestMessage);
        try {

            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
                String result = null;
                if(!messageBelongsToAdapter(requestMessage)){
                    return;
                };
                if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                    result = responseDiscover(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CREATE)) {

                    result = responseCreate(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CONFIGURE)) {

                    result = responseConfigure(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_RELEASE)) {

                    result = responseRelease(requestMessage);
                }

                if (result != null) {
                    Message responseMessage = generateResponseMessage(requestMessage, result);

                    if (null != requestMessage.getJMSCorrelationID()) {
                        responseMessage.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
                    }

                    this.context.createProducer().send(topic, responseMessage);
                }
            }

        } catch (JMSException e) {
            System.err.println("JMSException");
        }
    }
    public Message generateResponseMessage(Message requestMessage, String result) throws JMSException {
        final Message responseMessage = this.context.createMessage();

        responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, requestMessage.getStringProperty(IMessageBus.METHOD_TYPE));
        responseMessage.setStringProperty(IMessageBus.RDF, result);

        return responseMessage;
    }
}
