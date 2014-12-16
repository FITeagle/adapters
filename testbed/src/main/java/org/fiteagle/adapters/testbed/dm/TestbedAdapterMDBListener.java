package org.fiteagle.adapters.testbed.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.fiteagle.adapters.testbed.TestbedAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@MessageDriven(name = "TestbedInformListener", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class TestbedAdapterMDBListener implements MessageListener {

    private static Logger LOGGER = Logger.getLogger(TestbedAdapterMDBListener.class.toString());

    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;
    
    @Inject
    private TestbedAdapterMDBSender mib;

    public void onMessage(final Message requestMessage) {
        try {
            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
              String rdfString = MessageUtil.getRDFResult(requestMessage);
              if(rdfString == null ){
                return;
              }
              Model messageModel = MessageUtil.parseSerializedModel(rdfString);
                if(messageModel != null){
                
                  if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                          LOGGER.log(Level.INFO, "Received a discover message");
                          mib.sendInformMessage(TestbedAdapter.getTestbedModel(), requestMessage.getJMSCorrelationID());
                  }
                  
                  else if (isAdapterMessage(messageModel)) {
                      if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_INFORM)) {
                          Model adapterModel = TestbedAdapter.getTestbedModel();
                          Resource adapterInstance = getAdapterInstance(messageModel);
                          if (adapterInstance == null) {
                              LOGGER.log(Level.INFO, "no adapter could be detected in the inform message");
                          } else {                            
                              LOGGER.log(Level.INFO, "Received an inform message");
                              Resource testbedResource = adapterModel.getResource(TestbedAdapter.getInstance().getAdapterInstance().getURI());
                              testbedResource.addProperty(MessageBusOntologyModel.propertyFiteagleContainsAdapter, adapterInstance);
                              sendUpdateModel(adapterModel);
                          }
                      }
                  }
                }
            }

        } catch (JMSException e) {
          LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private boolean isAdapterMessage(Model messageModel) {
        if (messageModel.contains(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter)) {
            return true;
        }
        return false;
    }

    private Resource getAdapterInstance(Model messageModel) {
        StmtIterator adapterIterator = messageModel.listStatements(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
        while (adapterIterator.hasNext()) {
            Statement currentStatement = adapterIterator.next();
            StmtIterator adapterInstanceIterator = messageModel.listStatements(null, RDF.type, currentStatement.getSubject());
            while (adapterInstanceIterator.hasNext()) {
                return adapterInstanceIterator.next().getSubject();
            }
        }
        return null;
    }

    private void sendUpdateModel(Model updateModel) {
        try {
            Model messageModel = MessageUtil.createMsgInform(updateModel);
            String serializedRDF = MessageUtil.serializeModel(messageModel);
            final Message eventMessage = this.context.createMessage();

            eventMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_INFORM);
            eventMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            eventMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
            LOGGER.log(Level.INFO, "Sending Testbed Update Model as Inform Message");
            this.context.createProducer().send(topic, eventMessage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

}
