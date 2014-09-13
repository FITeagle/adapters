package org.fiteagle.abstractAdapter;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
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
            restoreState();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

    public void restoreState() throws JMSException {

        Model messageModel = ModelFactory.createDefaultModel();
        messageModel.add(adapter.getAdapterInstance(), RDF.type, adapter.getAdapterType());

        String filterCorrelationID = sendRequestMessage(messageModel);
        AbstractAdapterStateRestorator.LOGGER.log(Level.INFO, this.toString() + " : Sent request Message");
        filterCorrelationID = "JMSCorrelationID='" + filterCorrelationID + "'";
        AbstractAdapterStateRestorator.LOGGER.log(Level.INFO, this.toString() + " : waiting for repository response");
        Message responseMessage = this.context.createConsumer(this.topic, filterCorrelationID).receive(10000);

        if (responseMessage != null) {
            AbstractAdapterStateRestorator.LOGGER.log(Level.INFO, this.toString() + " : Got response");
            Model modelCreate = MessageBusMsgFactory.getMessageRDFModel(responseMessage);

            // In this case the inform message is used to create instances internally in the adapter
            if (MessageBusMsgFactory.isMessageType(modelCreate, MessageBusOntologyModel.propertyFiteagleInform)) {
                System.err.println("is inform");
                adapterRDFHandler.parseCreateModel(modelCreate, filterCorrelationID);
                System.err.println("parsed create model");
            }
        }
        AbstractAdapterStateRestorator.LOGGER.log(Level.INFO, this.toString() + " : Got no response (null)");

    }

    private String sendRequestMessage(Model eventRDF) {
        String correlationID = UUID.randomUUID().toString();

        try {

            Model messageModel = MessageBusMsgFactory.createMsgRequest(eventRDF);
            String serializedRDF = MessageBusMsgFactory.serializeModel(messageModel);

            final Message requestMessage = this.context.createMessage();
            requestMessage.setJMSCorrelationID(correlationID);
            requestMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_REQUEST);
            requestMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            requestMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);

            this.context.createProducer().send(this.topic, requestMessage);
        } catch (JMSException e) {
            System.err.println("JMSException in AbstractAdapterMDBSender");
        }

        return correlationID;
    }


}
