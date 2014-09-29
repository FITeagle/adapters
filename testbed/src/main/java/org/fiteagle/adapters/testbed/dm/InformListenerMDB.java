package org.fiteagle.adapters.testbed.dm;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.fiteagle.adapters.testbed.OntologyReader;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vju on 9/28/14.
 */
@MessageDriven(name = "TestbedInformListener", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class InformListenerMDB implements MessageListener {

    private static Logger LOGGER = Logger.getLogger(InformListenerMDB.class.toString());

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    public void onMessage(final Message requestMessage) {
        try {

            LOGGER.log(Level.INFO, "GOT INFORM MESSAGE IN TESTBEDADAPTER");
            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {

                Model modelMessage = MessageBusMsgFactory.getMessageRDFModel(requestMessage);
                if (modelMessage != null && isAdapterMessage(modelMessage)) {
                    LOGGER.log(Level.INFO, "GOT INFORM MESSAGE IN TESTBEDADAPTER + XXXXXX");

                    if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_INFORM)) {
                        Model adapterModel = OntologyReader.getTestbedModel();
                        com.hp.hpl.jena.rdf.model.Resource adapterInstance = getAdapterInstance(modelMessage);
                        if (adapterInstance == null) {
                            LOGGER.log(Level.INFO, "http://fiteagle.org/ontology#Adapter could not be detected");
                        } else {                            
                            // LOGGER.log(Level.INFO, "GOT INFORM MESSAGE IN TESTBEDADAPTER");
                            com.hp.hpl.jena.rdf.model.Resource testbedResource = adapterModel.getResource("http://fiteagleinternal#FITEAGLE_Testbed");
                            testbedResource.addProperty(MessageBusOntologyModel.propertyFiteagleContainsAdapter, adapterInstance);
                            sendUpdateModel(adapterModel);
                        }
                    }
                }
            }

        } catch (JMSException e) {
            System.err.println(this.toString() + "JMSException");
        }
    }

    private boolean isAdapterMessage(Model messageModel) {
        if (messageModel.contains(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter)) {
            return true;
        }
        return false;
    }

    private com.hp.hpl.jena.rdf.model.Resource getAdapterInstance(Model messageModel) {
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
            Model messageModel = MessageBusMsgFactory.createMsgInform(updateModel);
            String serializedRDF = MessageBusMsgFactory.serializeModel(messageModel);
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
