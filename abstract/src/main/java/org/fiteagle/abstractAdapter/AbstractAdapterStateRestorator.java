package org.fiteagle.abstractAdapter;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterStateRestorator {

    private static Logger LOGGER = Logger.getLogger(AbstractAdapterStateRestorator.class.toString());

    protected AbstractAdapterRDFHandler adapterRDFHandler;
    protected AbstractAdapter adapter;

    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    protected void startup() {
        AbstractAdapterStateRestorator.LOGGER.log(Level.INFO, this.toString() + " : Registering adapter " + adapter.adapterName);

        adapter.registerAdapter();

        // At this point maybe some parameters of the adapter itself should be restored as well?!
        // adapter.restoreAdapterParameters();

        try {
            while(!adapter.isFinishedRegistering()){
                
            }
            restoreState();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void restoreState() throws JMSException {

        Model messageModel = ModelFactory.createDefaultModel();
        messageModel.add(adapter.getAdapterInstance(), RDF.type, adapter.getAdapterType());
        messageModel.add(MessageBusOntologyModel.internalMessage, MessageBusOntologyModel.methodRestores, adapter.getAdapterInstance());

        sendRequestMessage(messageModel);
        AbstractAdapterStateRestorator.LOGGER.log(Level.INFO, this.toString() + " : Sent request restore Message");
    }

    private void sendRequestMessage(Model eventRDF) {

        try {

            Model messageModel = MessageBusMsgFactory.createMsgRequest(eventRDF);
            String serializedRDF = MessageBusMsgFactory.serializeModel(messageModel);

            final Message requestMessage = this.context.createMessage();
            requestMessage.setJMSCorrelationID(UUID.randomUUID().toString());
            requestMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_REQUEST);
            requestMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            requestMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);

            this.context.createProducer().send(this.topic, requestMessage);
        } catch (JMSException e) {
            System.err.println("JMSException in AbstractAdapterMDBSender");
        }

    }

}
