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

    private MotorAdapter adapter;

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    @PostConstruct
    public void setup() throws NamingException {
        this.adapter = MotorAdapter.getInstance();
    }
    
    public String responseDescribe(Message requestMessage) throws JMSException{
        return this.adapter.getAdapterDescription(getSerialization(requestMessage));
    }
    
    public String responseInstances(Message requestMessage) throws JMSException{
        return this.adapter.getAllInstances(getSerialization(requestMessage));
    }
    
    public String responseMonitor(Message requestMessage) throws JMSException{
        int instanceID = getInstanceID(requestMessage);
        return this.adapter.monitorInstance(instanceID, getSerialization(requestMessage));
    }

    public String responseProvision(Message requestMessage) throws JMSException{
        int instanceID = getInstanceID(requestMessage);
        if (this.adapter.createInstance(instanceID)) {
            return "Created instance " + instanceID;
        } else {
            return "Cannot create instance " + instanceID;
        }
    }
    
    public String responseControl(Message requestMessage) throws JMSException{
        String controlString = requestMessage.getStringProperty(IResourceRepository.PROP_CONTROL);
       
        // Need to convert String to input stream for Apache Jena
        InputStream in = new ByteArrayInputStream(controlString.getBytes());
        return this.adapter.controlInstance(in, getSerialization(requestMessage));
    }
    
    public String responseTerminate(Message requestMessage) throws JMSException{
        int instanceID = getInstanceID(requestMessage);
        if (this.adapter.terminateInstance(instanceID)) {
            return "Terminated instance " + instanceID;
        } else {
            return "Cannot terminate instance " + instanceID;
        }
        
    }

    public String getSerialization(Message message) throws JMSException{
        return message.getStringProperty(IResourceRepository.PROP_SERIALIZATION);
    }
    
    public int getInstanceID(Message message) throws JMSException{
        return Integer.parseInt(message.getStringProperty(IResourceRepository.PROP_INSTANCE_ID));
    }
    
    public Message generateResponseMessage(Message requestMessage, String result) throws JMSException{
        final Message responseMessage = this.context.createMessage();
        
        // TODO: What kind of response types will be available?
        responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST));
        responseMessage.setStringProperty(IMessageBus.TYPE_RESULT, result);
        
        return responseMessage;
    }

    public void onMessage(final Message requestMessage) {
        try {

            if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST) != null) {
                String result = "";

                try {

                    if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_DESCRIBE)) {
                        
                        result = responseDescribe(requestMessage);

                    } else if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_LIST_RESOURCES)) {
                        
                        result = responseInstances(requestMessage);

                    } else if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_MONITOR)) {
                        
                        result = responseMonitor(requestMessage);

                    } else if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_PROVISION)) {
                        
                        result = responseProvision(requestMessage);

                    } else if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_CONTROL)) {
                       
                        result = responseControl(requestMessage);

                    } else if (requestMessage.getStringProperty(IMessageBus.TYPE_REQUEST).equals(IMessageBus.REQUEST_TERMINATE)) {
                        
                        result = responseTerminate(requestMessage);

                    } else {
                        result = "Unknown request";
                    }

                } catch (java.lang.NumberFormatException e) {
                    result = "Number parsing error";

                }
                
                Message responseMessage = generateResponseMessage(requestMessage, result);

                if (null != requestMessage.getJMSCorrelationID()) {
                    responseMessage.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
                }

                this.context.createProducer().send(topic, responseMessage);
            }

        } catch (JMSException e) {
            System.err.println("JMSException");
        }
    }

}
