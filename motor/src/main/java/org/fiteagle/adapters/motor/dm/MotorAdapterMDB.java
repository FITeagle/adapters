package org.fiteagle.adapters.motor.dm;

import javax.annotation.PostConstruct;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDB implements MessageListener {

    // private final static Logger LOGGER = Logger
    // .getLogger(MotorAdapterMDB.class.toString());
    private MotorAdapter adapter;

    @PostConstruct
    public void setup() {
        this.adapter = MotorAdapter.getInstance();
    }

    public void onMessage(final Message message) {
        try {
            String result = messageToString(message);
            System.err.println(result);
        } catch (JMSException e) {
            // LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private String messageToString(Message message) throws JMSException {
        String result = "";

        result += "Message ID: " + message.getJMSMessageID() + "\n";
        result += "  * JMSCorrelationID: " + message.getJMSCorrelationID() + "\n";
        result += "  * Request: " + message.getStringProperty(IMessageBus.TYPE_REQUEST) + "\n";
        result += "  * Result: " + message.getStringProperty(IMessageBus.TYPE_RESULT) + "\n";

        return result;
    }
}
