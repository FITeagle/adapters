package org.fiteagle.abstractAdapter;

import java.util.UUID;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.apache.jena.riot.RiotException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterStateRestorator {

    protected AbstractAdapterRDFHandler adapterRDFHandler;
    protected AbstractAdapter adapter;
    
    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    protected void startup() {
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
        filterCorrelationID = "JMSCorrelationID='" + filterCorrelationID + "'";
        Message responseMessage = this.context.createConsumer(this.topic, filterCorrelationID).receive(IMessageBus.TIMEOUT);

        if (responseMessage != null) {
            Model modelCreate = MessageBusMsgFactory.getMessageRDFModel(responseMessage);

            // In this case the inform message is used to create instances internally in the adapter
            if (MessageBusMsgFactory.isMessageType(modelCreate, MessageBusOntologyModel.propertyFiteagleInform)) {
                adapterRDFHandler.parseCreateModel(modelCreate, filterCorrelationID);
            }
        }

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

            this.context.createProducer().send(topic, requestMessage);
        } catch (JMSException e) {
            System.err.println("JMSException in AbstractAdapterMDBSender");
        }

        return correlationID;
    }


}
