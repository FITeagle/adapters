package org.fiteagle.adapters.openstack.dm;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

@Singleton
@Startup
public class OpenstackAdapterMDBSender implements AdapterEventListener{

  private static Logger LOGGER = Logger.getLogger(OpenstackAdapterMDBSender.class.toString());
  protected Map<String, OpenstackAdapter> getAdapterInstances() {
    return OpenstackAdapter.adapterInstances;
  }

  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;


  @PostConstruct
  public void initializeAdapters() {
    for(OpenstackAdapter adapter : getAdapterInstances().values()){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Adding MDB-Sender for " + adapter.getAdapterInstance().getURI());
      adapter.setListener(this);
      register(adapter, 1000);
    }
  }

  public void register(AbstractAdapter adapter, long delay){
    if(delay < 3600000){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Registering " + adapter.getAdapterInstance().getURI());
      try {
        adapter.updateAdapterDescription();
        adapter.notifyListeners(adapter.getAdapterDescriptionModel(), null, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
      } catch (AbstractAdapter.ProcessingException e) {
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
    //final Message message = MessageUtil.createRDFMessage(eventRDF, methodType, methodTarget, IMessageBus.SERIALIZATION_DEFAULT, requestID, context);
    final Message message = context.createTextMessage(MessageUtil.serializeModel(eventRDF, IMessageBus.SERIALIZATION_DEFAULT));
    try {
      message.setStringProperty(IMessageBus.METHOD_TYPE, methodType);
      message.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
      if(requestID != null){
        message.setJMSCorrelationID(requestID);
      }
      else{
        message.setJMSCorrelationID(UUID.randomUUID().toString());
      }
      if(methodTarget != null){
        message.setStringProperty(IMessageBus.METHOD_TARGET, methodTarget);
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    context.createProducer().send(topic, message);
  }

  @PreDestroy
  public void contextDestroyed() {
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Deregistering " + adapter.getAdapterInstance().getURI());
      Model messageModel = ModelFactory.createDefaultModel();
      messageModel.add(adapter.getAdapterInstance(), RDF.type, adapter.getAdapterABox());
      String fileName = adapter.getAdapterInstance().getLocalName();
      adapter.notifyListeners(messageModel, null, IMessageBus.TYPE_DELETE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
    }
  }

  public JMSContext getContext(){
    return this.context;
  }


}
