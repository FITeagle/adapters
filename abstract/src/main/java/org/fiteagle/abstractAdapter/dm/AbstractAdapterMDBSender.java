package org.fiteagle.abstractAdapter.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractAdapterMDBSender implements AdapterEventListener {
  
  private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBSender.class.toString());
  
  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  @PostConstruct
  public void initializeAdapter() {
    LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Adding MDB-Sender for " + getAdapter().getAdapterInstance().getURI());
    getAdapter().addListener(this);
    
    LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Registering " + getAdapter().getAdapterInstance().getURI());
    getAdapter().registerAdapter();
  }
  
  @Override
  public void publishModelUpdate(Model eventRDF, String requestID, String methodType, String methodTarget) {
    final Message message = MessageUtil.createRDFMessage(eventRDF, methodType, methodTarget, IMessageBus.SERIALIZATION_DEFAULT, requestID, context);
    context.createProducer().send(topic, message);
  }
  
  @PreDestroy
  public void contextDestroyed() {
    getAdapter().deregisterAdapter();
  }
  
  protected abstract AbstractAdapter getAdapter();
}
