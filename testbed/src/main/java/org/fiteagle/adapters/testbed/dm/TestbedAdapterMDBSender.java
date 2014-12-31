package org.fiteagle.adapters.testbed.dm;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

import org.fiteagle.adapters.testbed.TestbedAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class TestbedAdapterMDBSender {
  
  @Inject
  private JMSContext context;
  @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  private static Logger LOGGER = Logger.getLogger(TestbedAdapterMDBSender.class.toString());
  
  @PostConstruct
  public void sendModel() {
    TestbedAdapter.getInstance().setMDBSender(this);
    sendInformMessage(TestbedAdapter.getTestbedModel(), null);
  }
  
  @PreDestroy
  public void contextDestroyed() {
    TestbedAdapter.getInstance().deregisterAdapter();
  }
  
  public void sendInformMessage(Model rdfModel, String requestID) {
    try {
      rdfModel.add(MessageBusOntologyModel.internalMessage, RDF.type, MessageBusOntologyModel.propertyFiteagleInform);
      String serializedRDF = MessageUtil.serializeModel(rdfModel);
      
      String correlationID = "";
      if (requestID == null || requestID.isEmpty()) {
        correlationID = UUID.randomUUID().toString();
      } else {
        correlationID = requestID;
      }
      
      final Message eventMessage = this.context.createMessage();
      eventMessage.setJMSCorrelationID(correlationID);
      eventMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_INFORM);
      eventMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
      eventMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
      LOGGER.log(Level.INFO, "Sending Testbed Model as Inform Message");
      this.context.createProducer().send(topic, eventMessage);
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }
  
}
