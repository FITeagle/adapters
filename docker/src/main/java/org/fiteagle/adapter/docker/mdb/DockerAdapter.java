package org.fiteagle.adapter.docker.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.fiteagle.api.Adapter;

/**
 * A simple Message Driven Bean that asynchronously receives and processes the messages that are sent to the topic.
 * 
 * @author Alexander Willner (alexander.willner@tu-berlin.de)
 * 
 */
@MessageDriven(name = "DockerAdapterMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/adapters"),
        //@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = Adapter.PROPERTY_TYPE + "=DockerAdapter"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class DockerAdapter implements MessageListener {
    private final static Logger LOGGER = Logger.getLogger(DockerAdapter.class.toString());

    public void onMessage(Message rcvMessage) {
        TextMessage msg = null;
        try {
            if (rcvMessage instanceof TextMessage) {
                msg = (TextMessage) rcvMessage;
                LOGGER.info("The Docker adapter received a message from adapter topic: " + Adapter.toDebugString(msg));
            } else {
                LOGGER.warning("The Docker adapter received a message of wrong type: " + rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
