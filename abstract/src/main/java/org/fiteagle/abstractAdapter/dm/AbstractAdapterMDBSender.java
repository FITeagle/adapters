package org.fiteagle.abstractAdapter.dm;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterMDBSender {
    
    private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBSender.class.toString());
    
    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    @PostConstruct
    public void contextInitialized() {
      getAdapter().addChangeListener(new AdapterEventListener() {

        @Override
        public void rdfChange(Model eventRDF, String requestID) {
            sendInformMessage(eventRDF, requestID);                
        }
      });
      
      LOGGER.log(Level.INFO, this.getClass().getSimpleName() + ": Registering adapter " + getAdapter().getAdapterName());
      
      getAdapter().registerAdapter();
      
      try {
        restoreState();
      } catch (JMSException e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    
    public void sendInformMessage(Model eventRDF, String requestID) {
        try {
            Model messageModel = MessageBusMsgFactory.createMsgInform(eventRDF);
            messageModel.add(getAdapter().getAdapterInstance(), RDF.type, getAdapter().getAdapterType());

            String serializedRDF = MessageBusMsgFactory.serializeModel(messageModel);

            String correlationID = "";
            if(requestID == null || requestID.isEmpty()){
                correlationID = UUID.randomUUID().toString();
            } else {
                correlationID = requestID;
            }
            
            final Message eventMessage = this.context.createMessage();
            eventMessage.setJMSCorrelationID(correlationID);
            eventMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_INFORM);
            eventMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            eventMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
            
            this.context.createProducer().send(topic, eventMessage);
        } catch (JMSException e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
    
    public void restoreState() throws JMSException {
      
      Model messageModel = ModelFactory.createDefaultModel();
      messageModel.add(getAdapter().getAdapterInstance(), RDF.type, getAdapter().getAdapterType());
      messageModel.add(MessageBusOntologyModel.internalMessage, MessageBusOntologyModel.methodRestores, getAdapter().getAdapterInstance());
      
      sendRequestMessage(messageModel);
      LOGGER.log(Level.INFO, this.getClass().getSimpleName() + ": Sent request restore message");
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
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    
    @PreDestroy
    public void contextDestroyed() {
      getAdapter().deregisterAdapter();
    }
    
    protected abstract AbstractAdapter getAdapter();

}




