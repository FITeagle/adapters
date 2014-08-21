package org.fiteagle.abstractAdapter.dm;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.jena.riot.RiotException;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vju on 8/20/14.
 */

/**
 * MDB to listen for incoming Messages
 * Do not forget to annotate as MDB
 */
@MessageDriven(name = "AbstractAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public abstract class AbstractMDBListener implements MessageListener {


    public abstract AbstractAdapter getAdapter();

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public String responseConfigure(Message requestMessage) throws JMSException {
        Logger LOGGER = getLogger();
        LOGGER.log(Level.INFO, "Received a configure message");

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
                    LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a create message, so do something with it
                if (currentStatement != null) {
                    modelConfigure.remove(currentStatement);

                    LOGGER.log(Level.INFO, "Configuring instance");
                    return getAdapter().controlInstance(modelConfigure);
                }
            } catch (RiotException e) {
                System.err.println("Invalid RDF");
            }
        }

        return "No instance configured \n\n";
    }
    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public String responseDiscover(Message requestMessage) throws JMSException {

        String serialization = getSerialization(requestMessage);
        Logger LOGGER = getLogger();
        LOGGER.log(Level.INFO, "Received a discover message");

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
                    LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a discover message, so do something with it
                if (currentStatement != null) {
                    StmtIterator iteratorClassResource = modelInform.listStatements(new SimpleSelector(null, RDF.type, getAdapter().getInstanceClassResource()));

                    LOGGER.log(Level.INFO, "Searching for instance resources to describe...");

                    Statement currentInstanceStatement = null;
                    while (iteratorClassResource.hasNext()) {
                        currentInstanceStatement = iteratorClassResource.nextStatement();
                        LOGGER.log(Level.INFO, "Found : " + currentInstanceStatement.toString());

                        String instanceName = currentInstanceStatement.getSubject().getLocalName();
                        LOGGER.log(Level.INFO, "Creating instance: " + instanceName);

                        return getAdapter().monitorInstance(instanceName, serialization);

                    }
                }
            } catch (RiotException e) {
                System.err.println("Invalid RDF");
            }
        }

        // No specific instance requested, show all

        Model modelDiscover = getAdapter().getAdapterDescriptionModel(serialization);
        modelDiscover.add(getAdapter().getAllInstancesModel(serialization));

        StringWriter writer = new StringWriter();

        modelDiscover.write(writer, serialization);

        return writer.toString();
    }

    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public String responseCreate(Message requestMessage) throws JMSException {
        Logger LOGGER = getLogger();
        LOGGER.log(Level.INFO, "Received a create message");

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
                    LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a create message, so do something with it
                if (currentStatement != null) {
                    StmtIterator iteratorClassResource = modelInform.listStatements(new SimpleSelector(null, RDF.type, getAdapter().getInstanceClassResource()));

                   LOGGER.log(Level.INFO, "Searching for instance resources to create...");

                    String output = "";
                    Statement currentInstanceStatement = null;
                    while (iteratorClassResource.hasNext()) {
                        currentInstanceStatement = iteratorClassResource.nextStatement();
                        LOGGER.log(Level.INFO, "Found : " + currentInstanceStatement.toString());

                        String instanceName = currentInstanceStatement.getSubject().getLocalName();
                        LOGGER.log(Level.INFO, "Creating instance: " + instanceName);

                        if (getAdapter().createInstance(instanceName)) {
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
    /**
     * create a response String as response for this request message
     * @param requestMessage
     * @return
     */
    public String responseRelease(Message requestMessage) throws JMSException {
        Logger LOGGER = getLogger();
        LOGGER.log(Level.INFO, "Received a release message");

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
                    LOGGER.log(Level.INFO, "Found : " + currentStatement.toString());
                }

                // This is a create message, so do something with it
                if (currentStatement != null) {
                    StmtIterator iteratorClassResource = modelRelease.listStatements(new SimpleSelector(null, RDF.type, getAdapter().getInstanceClassResource()));

                    LOGGER.log(Level.INFO, "Searching for instance resources to release...");

                    Statement currentInstanceStatement = null;
                    while (iteratorClassResource.hasNext()) {
                        currentInstanceStatement = iteratorClassResource.nextStatement();
                        LOGGER.log(Level.INFO, "Found : " + currentInstanceStatement.toString());

                        String instanceName = currentInstanceStatement.getSubject().getLocalName();
                        LOGGER.log(Level.INFO, "Releasing instance: " + instanceName);

                        if (getAdapter().terminateInstance(instanceName)) {
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

    /**
     * Method for checking if this message belongs to this adapter
     * @param requestMessage
     * @return true if message belongs to adapter
     */
    public boolean messageBelongsToAdapter(Message requestMessage) {
        boolean isForAdapter = false;
        try {
            if (requestMessage.getStringProperty(IMessageBus.RDF) != null) {
                String inputRDF = requestMessage.getStringProperty(IMessageBus.RDF);
                Model inputModel = ModelFactory.createDefaultModel();
                InputStream is = new ByteArrayInputStream(inputRDF.getBytes());
                inputModel.read(is, null, requestMessage.getStringProperty(IMessageBus.SERIALIZATION));

                StmtIterator mightyRobotIterator = inputModel.listStatements(new SimpleSelector(null, RDF.type, getAdapter().getInstanceClassResource()));
                while(mightyRobotIterator.hasNext()){
                    isForAdapter = true;
                    break;
                }
            }
        } catch (Exception e) {

        }
        return isForAdapter;
    }

    /**
     * Implementation of the onMessage method to handle the request
     * @param requestMessage
     */
    public void onMessage(final Message requestMessage) {
        messageBelongsToAdapter(requestMessage);
        try {

            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
                String result = null;
                if(!messageBelongsToAdapter(requestMessage)){
                    return;
                };
                if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                    result = responseDiscover(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CREATE)) {

                    result = responseCreate(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CONFIGURE)) {

                    result = responseConfigure(requestMessage);

                } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_RELEASE)) {

                    result = responseRelease(requestMessage);
                }

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
    public Message generateResponseMessage(Message requestMessage, String result) throws JMSException {
        final Message responseMessage = this.context.createMessage();

        responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, requestMessage.getStringProperty(IMessageBus.METHOD_TYPE));
        responseMessage.setStringProperty(IMessageBus.RDF, result);

        return responseMessage;
    }

    public String getSerialization(Message message) throws JMSException {
        return message.getStringProperty(IMessageBus.SERIALIZATION);
    }

    public abstract Logger getLogger();
}
