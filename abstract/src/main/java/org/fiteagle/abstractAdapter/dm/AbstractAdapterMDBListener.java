package org.fiteagle.abstractAdapter.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractAdapterMDBListener implements MessageListener {
  
  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBListener.class.toString());
  
  protected abstract AbstractAdapter getAdapter();
  
  public void onMessage(final Message message) {
    String messageType = MessageUtil.getMessageType(message);
    String serialization = MessageUtil.getMessageSerialization(message);
    String rdfString = MessageUtil.getStringBody(message);
    
    if (messageType != null && rdfString != null) {
      Model messageModel = MessageUtil.parseSerializedModel(rdfString, serialization);
      
      if (adapterIsRecipient(messageModel)) {
        LOGGER.log(Level.INFO, "Received a " + messageType + " message");
        try{
          if (messageType.equals(IMessageBus.TYPE_CREATE)) {
            Model resultModel = getAdapter().createInstances(messageModel);
            getAdapter().notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM,  IMessageBus.TARGET_ORCHESTRATOR);
            
          } else if (messageType.equals(IMessageBus.TYPE_CONFIGURE)) {
            Model resultModel = getAdapter().configureInstances(messageModel);
            getAdapter().notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);
            
          } else if (messageType.equals(IMessageBus.TYPE_DELETE)) {
            getAdapter().deleteInstances(messageModel);
          }
        } catch(AdapterException e){
          Message errorMessage = MessageUtil.createErrorMessage(e.getMessage(), MessageUtil.getJMSCorrelationID(message), context);
          context.createProducer().send(topic, errorMessage);
        }
      }
    }
  }
  
  private boolean adapterIsRecipient(Model messageModel) {
    return messageModel.containsResource(getAdapter().getAdapterInstance());
  }
  
}
