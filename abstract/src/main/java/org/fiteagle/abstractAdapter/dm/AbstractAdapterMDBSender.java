package org.fiteagle.abstractAdapter.dm;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterMDBSender implements AdapterEventListener {
  
  private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBSender.class.toString());
  
  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  @PostConstruct
  public void initializeAdapter() {
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Adding MDB-Sender for " + adapter.getAdapterInstance().getURI());
      adapter.addListener(this);
      
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Registering " + adapter.getAdapterInstance().getURI());
      adapter.updateAdapterDescription();
      adapter.notifyListeners(adapter.getAdapterDescriptionModel(), null, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
    }
   }
  
  @Override
  public void publishModelUpdate(Model eventRDF, String requestID, String methodType, String methodTarget) {
    final Message message = MessageUtil.createRDFMessage(eventRDF, methodType, methodTarget, IMessageBus.SERIALIZATION_DEFAULT, requestID, context);
    context.createProducer().send(topic, message);
  }
  
  @PreDestroy
  public void contextDestroyed() {
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Deregistering " + adapter.getAdapterInstance().getURI());
      Model messageModel = ModelFactory.createDefaultModel();
      messageModel.add(adapter.getAdapterInstance(), RDF.type, adapter.getAdapterType());
      adapter.notifyListeners(messageModel, null, IMessageBus.TYPE_DELETE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
    }
  }
  
  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
  
}
