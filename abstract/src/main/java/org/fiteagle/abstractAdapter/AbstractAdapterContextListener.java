package org.fiteagle.abstractAdapter;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterContextListener implements ServletContextListener {
  
  private static Logger LOGGER = Logger.getLogger(AbstractAdapterContextListener.class.toString());
  
  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  @Override
  public void contextInitialized(ServletContextEvent event) {
    LOGGER.log(Level.INFO, this.getClass().getSimpleName() + ": Registering adapter " + getAdapter().adapterName);
    
    getAdapter().registerAdapter();
    
    try {
      restoreState();
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
      System.err.println("JMSException in AbstractAdapterMDBSender");
    }
    
  }
  
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    getAdapter().deregisterAdapter();
  }
  
  protected abstract AbstractAdapter getAdapter();
}
