package org.fiteagle.adapters.motor.dm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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

import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.IResourceRepository;

// TODO: Welches topic? Wie nur einen Adapter ansprechen?

@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDBListener implements MessageListener {

    // private final static Logger LOGGER = Logger
    // .getLogger(MotorAdapterMDB.class.toString());
    private MotorAdapter adapter;

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    @PostConstruct
    public void setup() throws NamingException {
        this.adapter = MotorAdapter.getInstance();
    }

    public void onMessage(final Message message) {
        try {

            if (message.getStringProperty(IMessageBus.TYPE_REQUEST) != null) {
                String result = "";
                final String id = message.getJMSCorrelationID();
                final Message responseMessage = this.context.createMessage();

                try {

                    if (message.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_DESCRIBE)) {
                        String serializationFormat = message.getStringProperty(IResourceRepository.PROP_SERIALIZATION);
                        result = this.adapter.getAdapterDescription(serializationFormat);

                    } else if (message.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_LIST_RESOURCES)) {
                        String serializationFormat = message.getStringProperty(IResourceRepository.PROP_SERIALIZATION);
                        result = this.adapter.getAllInstances(serializationFormat);

                    } else if (message.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_MONITOR)) {
                        String serializationFormat = message.getStringProperty(IResourceRepository.PROP_SERIALIZATION);
                        int instanceID = Integer.parseInt(message.getStringProperty(IResourceRepository.PROP_INSTANCE_ID));
                        result = this.adapter.monitorInstance(instanceID, serializationFormat);

                    } else if (message.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_PROVISION)) {
                        int instanceID = Integer.parseInt(message.getStringProperty(IResourceRepository.PROP_INSTANCE_ID));
                        if (this.adapter.createInstance(instanceID)) {
                            result = "Created instance " + instanceID;
                        } else {
                            result = "Cannot create instance " + instanceID;
                        }

                    } else if (message.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_CONTROL)) {
                        String serializationFormat = message.getStringProperty(IResourceRepository.PROP_SERIALIZATION);
                        String controlString = message.getStringProperty(IResourceRepository.PROP_CONTROL);
                       
                        // Need to convert String to input stream for Apache Jena
                        InputStream in = new ByteArrayInputStream(controlString.getBytes());
                        result = this.adapter.controlInstance(in, serializationFormat);

                    } else if (message.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_TERMINATE)) {
                        int instanceID = Integer.parseInt(message.getStringProperty(IResourceRepository.PROP_INSTANCE_ID));
                        if (this.adapter.terminateInstance(instanceID)) {
                            result = "Terminated instance " + instanceID;
                        } else {
                            result = "Cannot terminate instance " + instanceID;
                        }

                    } else {
                        result = "Unknown request";
                    }

                } catch (java.lang.NumberFormatException e) {
                    result = "Number parsing error";

                }
                
                // TODO: What kind of response types will be available?
                responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, message.getStringProperty(IMessageBus.TYPE_REQUEST));
                responseMessage.setStringProperty(IMessageBus.TYPE_RESULT, result);

                if (null != id) {
                    responseMessage.setJMSCorrelationID(id);
                }

                this.context.createProducer().send(topic, responseMessage);
            }

        } catch (JMSException e) {
            System.err.println("JMSException");
        }
    }

}
