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
import org.fiteagle.api.core.MessageUtil;
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
    public void initializeAdapter() {
      LOGGER.log(Level.INFO, this.getClass().getSimpleName() + ": Adding change listener for " + getAdapter().getAdapterInstance().getURI());
      getAdapter().addChangeListener(new AdapterEventListener() {

        @Override
        public void publishModelUpdate(Model eventRDF, String requestID) {
            sendInformMessage(eventRDF, requestID);                
        }
      });
      
      LOGGER.log(Level.INFO, this.getClass().getSimpleName() + ": Registering " + getAdapter().getAdapterInstance().getURI());
      getAdapter().registerAdapter();
      
      LOGGER.log(Level.INFO, this.getClass().getSimpleName() + ": Restoring previous state of " + getAdapter().getAdapterInstance().getURI());
      try {
        restoreState();
      } catch (JMSException e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }
    }
    
    private void sendInformMessage(Model eventRDF, String requestID) {
        try {
            Model messageModel = MessageUtil.createMsgInform(eventRDF);

            String correlationID = "";
            if(requestID == null || requestID.isEmpty()){
                correlationID = UUID.randomUUID().toString();
            } else {
                correlationID = requestID;
            }
            
            Message eventMessage = MessageUtil.createRDFMessage(messageModel, IMessageBus.TYPE_INFORM, IMessageBus.SERIALIZATION_DEFAULT, context);
            eventMessage.setJMSCorrelationID(correlationID);
            
            context.createProducer().send(topic, eventMessage);
        } catch (JMSException e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
    
    public void restoreState() throws JMSException {
      Model messageModel = ModelFactory.createDefaultModel();
      messageModel.add(getAdapter().getAdapterInstance(), RDF.type, getAdapter().getAdapterType());
      messageModel.add(MessageBusOntologyModel.internalMessage, MessageBusOntologyModel.methodRestores, getAdapter().getAdapterInstance());
      
      sendRequestMessage(messageModel);
    }
    
    private void sendRequestMessage(Model eventRDF) {
        Model messageModel = MessageUtil.createMsgRequest(eventRDF);
        final Message requestMessage = MessageUtil.createRDFMessage(messageModel, IMessageBus.TYPE_REQUEST, IMessageBus.SERIALIZATION_DEFAULT, context);
        
        this.context.createProducer().send(this.topic, requestMessage);
    }
    
    @PreDestroy
    public void contextDestroyed() {
      getAdapter().deregisterAdapter();
    }
    
    protected abstract AbstractAdapter getAdapter();
}
