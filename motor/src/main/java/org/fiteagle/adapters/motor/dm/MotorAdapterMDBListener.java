package org.fiteagle.adapters.motor.dm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.jena.riot.RiotException;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractMDBListener;
import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.IResourceRepository;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO: Welches topic? Wie nur einen Adapter ansprechen?

@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDBListener extends AbstractMDBListener {

    private static Logger LOGGER = Logger.getLogger(MotorAdapterMDBListener.class.toString());

    private MotorAdapter adapter;

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    @PostConstruct
    public void setup() throws NamingException {
        this.adapter = MotorAdapter.getInstance();
    }

    public AbstractAdapter getAdapter(){
        return this.adapter;
    }
    public String responseDiscover(Message requestMessage) throws JMSException {
        
        String serialization = getSerialization(requestMessage);
        
        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Received a discover message");

        if (requestMessage.getStringProperty(IMessageBus.RDF) != null) {

            String inputRDF = requestMessage.getStringProperty(IMessageBus.RDF);

            // create an empty model
            Model modelInform = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(inputRDF.getBytes());

            try {
                // read the RDF/XML file
                modelInform.read(is, null, requestMessage.getStringProperty(IMessageBus.SERIALIZATION));

                StmtIterator iteratorCreateMessage = modelInform.listStatements(new SimpleSelector(null, RDF.type, MessageBusOntologyModel.propertyFiteagleDiscover));
                Statement currentStatement = null;
                while (iteratorCreateMessage.hasNext()) {
                    currentStatement = iteratorCreateMessage.nextStatement();
                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a discover message, so do something with it
                if (currentStatement != null) {
                    StmtIterator iteratorMotorResource = modelInform.listStatements(new SimpleSelector(null, RDF.type, adapter.getMotorResource()));

                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Searching for motor resources to describe...");

                    Statement currentMotorStatement = null;
                    while (iteratorMotorResource.hasNext()) {
                        currentMotorStatement = iteratorMotorResource.nextStatement();
                        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentMotorStatement.toString());

                        String instanceName = currentMotorStatement.getSubject().getLocalName();
                        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Creating instance: " + instanceName);

                        return adapter.monitorInstance(instanceName, serialization);

                    }
                }
            } catch (RiotException e) {
                System.err.println("Invalid RDF");
            }
        }

        // No specific instance requested, show all

        Model modelDiscover = this.adapter.getAdapterDescriptionModel(serialization);
        modelDiscover.add(this.adapter.getAllInstancesModel(serialization));

        StringWriter writer = new StringWriter();

        modelDiscover.write(writer, serialization);

        return writer.toString();
    }

    // public String responseInstances(Message requestMessage) throws JMSException {
    // return this.adapter.getAllInstances(getSerialization(requestMessage));
    // }

    // public String responseMonitor(Message requestMessage) throws JMSException{
    // int instanceID = getInstanceID(requestMessage);
    // return this.adapter.monitorInstance(instanceName, getSerialization(requestMessage));
    // }

    public String responseCreate(Message requestMessage) throws JMSException {

        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Received a create message");

        if (requestMessage.getStringProperty(IMessageBus.RDF) != null) {

            String inputRDF = requestMessage.getStringProperty(IMessageBus.RDF);

            // create an empty model
            Model modelInform = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(inputRDF.getBytes());

            try {
                // read the RDF/XML file
                modelInform.read(is, null, requestMessage.getStringProperty(IMessageBus.SERIALIZATION));

                StmtIterator iteratorCreateMessage = modelInform.listStatements(new SimpleSelector(null, RDF.type, MessageBusOntologyModel.propertyFiteagleCreate));
                Statement currentStatement = null;
                while (iteratorCreateMessage.hasNext()) {
                    currentStatement = iteratorCreateMessage.nextStatement();
                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a create message, so do something with it
                if (currentStatement != null) {
                    StmtIterator iteratorMotorResource = modelInform.listStatements(new SimpleSelector(null, RDF.type, adapter.getMotorResource()));

                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Searching for motor resources to create...");

                    String output = "";
                    Statement currentMotorStatement = null;
                    while (iteratorMotorResource.hasNext()) {
                        currentMotorStatement = iteratorMotorResource.nextStatement();
                        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentMotorStatement.toString());

                        String instanceName = currentMotorStatement.getSubject().getLocalName();
                        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Creating instance: " + instanceName);

                        if (adapter.createInstance(instanceName)) {
                            output += "Created new instance " + instanceName + "\n";
                        }

                    }
                    if (!output.isEmpty()) {
                        return output;
                    }
                }
            } catch (RiotException e) {
                System.err.println("Invalid RDF");
            }
        }

        return "No instance created \n\n";
    }

    public String responseConfigure(Message requestMessage) throws JMSException {
        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Received a configure message");

        if (requestMessage.getStringProperty(IMessageBus.RDF) != null) {

            String inputRDF = requestMessage.getStringProperty(IMessageBus.RDF);
            String serialization = requestMessage.getStringProperty(IMessageBus.SERIALIZATION);

            // create an empty model
            Model modelConfigure = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(inputRDF.getBytes());

            try {
                // read the RDF/XML file
                modelConfigure.read(is, null, serialization);

                StmtIterator iteratorConfigureMessage = modelConfigure.listStatements(new SimpleSelector(null, RDF.type, MessageBusOntologyModel.propertyFiteagleConfigure));
                Statement currentStatement = null;
                while (iteratorConfigureMessage.hasNext()) {
                    currentStatement = iteratorConfigureMessage.nextStatement();
                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a create message, so do something with it
                if (currentStatement != null) {
                    modelConfigure.remove(currentStatement);

                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Configuring instance");
                    return adapter.controlInstance(modelConfigure);
                }
            } catch (RiotException e) {
                System.err.println("Invalid RDF");
            }
        }

        return "No instance configured \n\n";
    }

    public String responseRelease(Message requestMessage) throws JMSException {
        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Received a release message");

        if (requestMessage.getStringProperty(IMessageBus.RDF) != null) {

            String inputRDF = requestMessage.getStringProperty(IMessageBus.RDF);

            // create an empty model
            Model modelRelease = ModelFactory.createDefaultModel();
            InputStream is = new ByteArrayInputStream(inputRDF.getBytes());

            try {
                // read the RDF/XML file
                modelRelease.read(is, null, requestMessage.getStringProperty(IMessageBus.SERIALIZATION));

                StmtIterator iteratorReleaseMessage = modelRelease.listStatements(new SimpleSelector(null, RDF.type, MessageBusOntologyModel.propertyFiteagleRelease));
                Statement currentStatement = null;
                while (iteratorReleaseMessage.hasNext()) {
                    currentStatement = iteratorReleaseMessage.nextStatement();
                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a create message, so do something with it
                if (currentStatement != null) {
                    StmtIterator iteratorMotorResource = modelRelease.listStatements(new SimpleSelector(null, RDF.type, adapter.getMotorResource()));

                    MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Searching for motor resources to release...");

                    Statement currentMotorStatement = null;
                    while (iteratorMotorResource.hasNext()) {
                        currentMotorStatement = iteratorMotorResource.nextStatement();
                        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Found : " + currentMotorStatement.toString());

                        String instanceName = currentMotorStatement.getSubject().getLocalName();
                        MotorAdapterMDBListener.LOGGER.log(Level.INFO, "Releasing instance: " + instanceName);

                        if (adapter.terminateInstance(instanceName)) {
                            return "Released instance " + instanceName + "\n\n";
                        }

                    }
                }
            } catch (RiotException e) {
                System.err.println("Invalid RDF");
            }
        }
        return "No instance released \n\n";

    }

    public String getSerialization(Message message) throws JMSException {
        return message.getStringProperty(IMessageBus.SERIALIZATION);
    }

    public int getInstanceID(Message message) throws JMSException {
        // return Integer.parseInt(message.getStringProperty(IResourceRepository.PROP_INSTANCE_ID));
        return 10;
    }

    public Message generateResponseMessage(Message requestMessage, String result) throws JMSException {
        final Message responseMessage = this.context.createMessage();

        // TODO: What kind of response types will be available?
        responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, requestMessage.getStringProperty(IMessageBus.METHOD_TYPE));
        responseMessage.setStringProperty(IMessageBus.RDF, result);

        return responseMessage;
    }

    public void onMessage(final Message requestMessage) {
        try {

            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
                String result = null;

                if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {

                    result = responseDiscover(requestMessage);

                    // } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                    // result = responseInstances(requestMessage);

                    // } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                    //
                    // result = responseMonitor(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CREATE)) {

                    result = responseCreate(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CONFIGURE)) {

                    result = responseConfigure(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_RELEASE)) {

                    result = responseRelease(requestMessage);
                }
                //
                // } else {
                // result = "Unknown request";
                // }

                if (result != null) {
                    Message responseMessage = generateResponseMessage(requestMessage, result);

                    if (null != requestMessage.getJMSCorrelationID()) {
                        responseMessage.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
                    }

                    this.context.createProducer().send(topic, responseMessage);
                }
            }

        } catch (JMSException e) {
            System.err.println("JMSException");
        }
    }

}
