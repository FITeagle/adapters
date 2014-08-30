package org.fiteagle.abstractAdapter.dm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.apache.jena.riot.RiotException;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterMDBListener implements MessageListener {

    private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBListener.class.toString());

    protected AbstractAdapterRDFHandler adapterRDFHandler;

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;
    
    public void onMessage(final Message requestMessage) {
        try {

            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
                String result = "";

                if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                    AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a discover message");
                    result = responseDiscover(requestMessage);
                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CREATE)) {
                    AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a create message");
                    responseCreate(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CONFIGURE)) {
                    AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a configure message");
                    responseConfigure(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_RELEASE)) {
                    AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a release message");
                    responseRelease(requestMessage);
                }

                if (!result.isEmpty()) {
                    Message responseMessage = generateResponseMessage(requestMessage, result);

                    if (null != requestMessage.getJMSCorrelationID()) {
                        responseMessage.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
                    }

                    this.context.createProducer().send(topic, responseMessage);
                }
            }

        } catch (JMSException e) {
            System.err.println(this.toString() + "JMSException");
        }
    }

    public String responseDiscover(Message requestMessage) throws JMSException {

        Model modelDiscover = getMessageModel(requestMessage);

        // This is a create message, so do something with it
        if (isMessageType(modelDiscover, MessageBusOntologyModel.propertyFiteagleDiscover)) {
            
            return adapterRDFHandler.parseDiscoverModel(modelDiscover);
        }

        return "Not a valid fiteagle:discover message \n\n";
    }

    public String responseCreate(Message requestMessage) throws JMSException {

        Model modelCreate = getMessageModel(requestMessage);

        // This is a create message, so do something with it
        if (isMessageType(modelCreate, MessageBusOntologyModel.propertyFiteagleCreate)) {            
            return adapterRDFHandler.parseCreateModel(modelCreate, requestMessage.getJMSCorrelationID());
        }

        return "Not a valid fiteagle:create message \n\n";
    }

    private Model getMessageModel(Message jmsMessage) throws JMSException {
        // create an empty model
        Model messageModel = ModelFactory.createDefaultModel();

        if (jmsMessage.getStringProperty(IMessageBus.RDF) != null) {

            String inputRDF = jmsMessage.getStringProperty(IMessageBus.RDF);

            InputStream is = new ByteArrayInputStream(inputRDF.getBytes());

            try {
                // read the RDF/XML file
                messageModel.read(is, null, jmsMessage.getStringProperty(IMessageBus.SERIALIZATION));
            } catch (RiotException e) {
                System.err.println("MDB Listener: Received invalid RDF");
            }
        }

        return messageModel;
    }

    private boolean isMessageType(Model messageModel, Property messageTypePropety) {

        return messageModel.contains(null, RDF.type, messageTypePropety);
    }

    public String responseConfigure(Message requestMessage) throws JMSException {

        Model modelConfigure = getMessageModel(requestMessage);

        // This is a configure message, so do something with it
        if (isMessageType(modelConfigure, MessageBusOntologyModel.propertyFiteagleConfigure)) {
            return adapterRDFHandler.parseConfigureModel(modelConfigure, requestMessage.getJMSCorrelationID());
        }
        return "Not a valid fiteagle:configure message \n\n";
    }

    public String responseRelease(Message requestMessage) throws JMSException {
        Model modelRelease = getMessageModel(requestMessage);

        // This is a release message, so do something with it
        if (isMessageType(modelRelease, MessageBusOntologyModel.propertyFiteagleRelease)) {
            return adapterRDFHandler.parseReleaseModel(modelRelease, requestMessage.getJMSCorrelationID());
        }

        return "Not a valid fiteagle:release message \n\n";
    }

    public Message generateResponseMessage(Message requestMessage, String result) throws JMSException {
        final Message responseMessage = this.context.createMessage();

        responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, IMessageBus.TYPE_INFORM);
        responseMessage.setStringProperty(IMessageBus.RDF, result);

        return responseMessage;
    }

}
