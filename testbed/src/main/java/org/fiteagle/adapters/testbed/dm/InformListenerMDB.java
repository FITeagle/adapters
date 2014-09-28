package org.fiteagle.adapters.testbed.dm;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.fiteagle.adapters.testbed.OntologyReader;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;

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
public class InformListenerMDB implements MessageListener{

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
                if(modelMessage != null && isAdapterMessage(modelMessage)){
                    LOGGER.log(Level.INFO, "GOT INFORM MESSAGE IN TESTBEDADAPTER + XXXXXX");

                    if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_INFORM)) {
                       Model adapterModel =  OntologyReader.getTestbedModel();
                       String name = getAdapterName(modelMessage);
                        if(name == null){
                            LOGGER.log(Level.INFO, "http://fiteagle.org/ontology#Adapter could not be detected" );
                            return;
                        }
                       // LOGGER.log(Level.INFO, "GOT INFORM MESSAGE IN TESTBEDADAPTER");
                       com.hp.hpl.jena.rdf.model.Resource testbedResource = adapterModel.getResource("http://fiteagle.org/ontology#FITEAGLE_Testbed");
                       testbedResource.addProperty(adapterModel.createProperty("http://fiteagle.org/ontology#containsAdapter"), adapterModel.createResource(name));
                       sendUpdateModel(adapterModel);
                    }
                }

            }

        } catch (JMSException e) {
            System.err.println(this.toString() + "JMSException");
        }
    }

    private boolean isAdapterMessage(Model messageModel){

boolean retval = false;
       StmtIterator iter = messageModel.listStatements();

      //  LOGGER.log(Level.INFO, "Iterator has next: " + iter.hasNext() );
        while(iter.hasNext()){
            //LOGGER.log(Level.INFO, iter.nextStatement().getObject().toString());
            Statement s = iter.nextStatement();
            if(s.getObject().toString().equals("http://fiteagle.org/ontology#Adapter")){


            retval = true;
            }
        }


        return retval;

    }
    private String getAdapterName(Model messageModel){
        StmtIterator iter = messageModel.listStatements();
        String retstring = null;
        while(iter.hasNext()){
            //LOGGER.log(Level.INFO, iter.nextStatement().getObject().toString());
            Statement s = iter.nextStatement();
            if(s.getObject().toString().equals("http://fiteagle.org/ontology#Adapter")){
                retstring = s.getSubject().toString();
            }
        }

       return retstring;


    }
    private void sendUpdateModel(Model updateModel) {
        try{
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
