package org.fiteagle.abstractAdapter.dm;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.abstractAdapter.AdapterControl;
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
  
  @EJB
  private AdapterControl adapterControl;
  
  @PostConstruct
  public void initializeAdapters() {
    for(AbstractAdapter adapter : adapterControl.getAdapterInstances()){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Adding MDB-Sender for " + adapter.getAdapterABox().getURI());
      adapter.addListener(this);
      register(adapter, 1000);
    }
  }
  
  public void register(AbstractAdapter adapter, long delay){
    if(delay < 3600000){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Registering " + adapter.getAdapterABox().getURI());
      try {
        adapter.updateAdapterDescription();
        adapter.notifyListeners(adapter.getAdapterDescriptionModel(), null, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
      } catch (ProcessingException e) {
        LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Error while registering: "+e.getMessage());
        delay = delay*2;
        LOGGER.log(Level.WARNING, "Retry in "+delay+"ms");
        try {
          Thread.sleep(delay);
          register(adapter, delay);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
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
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Deregistering " + adapter.getAdapterABox().getURI());
      Model messageModel = ModelFactory.createDefaultModel();
      messageModel.add(adapter.getAdapterABox(), RDF.type, adapter.getAdapterABox());
      String fileName = adapter.getAdapterABox().getLocalName();
      adapter.notifyListeners(messageModel, null, IMessageBus.TYPE_DELETE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
    }
  }
  
  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
  
}
