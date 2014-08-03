package org.fiteagle.adapters.motor.dm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.adapters.motor.dm.IMotorAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;

@Singleton(name = "MotorAdapterMDBSender")
@Startup
@Remote(IMotorAdapterMDBSender.class)
public class MotorAdapterMDBSender implements IMotorAdapterMDBSender {

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    private MotorAdapter adapter;

    @SuppressWarnings("unused")
    @PostConstruct
    private void startup() {
        adapter = MotorAdapter.getInstance();
        adapter.addChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String message = "Event Notification: " + event.getSource().toString() + " " + event.getPropertyName() + ":" + event.getNewValue() + " [old -> " + event.getOldValue() + "] | [new -> "
                        + event.getNewValue() + "]";
                sendMessage(message);
            }
        });
    }

    public void registerAdapter() {
        sendMessage("Register Motor Adapter");
    }

    public void unregisterAdapter() {
        sendMessage("Unregister Motor Adapter");
    }

    public void sendMessage(String message) {
        try {

            final Message eventMessage = this.context.createMessage();

            eventMessage.setJMSCorrelationID(UUID.randomUUID().toString());
            eventMessage.setStringProperty(IMessageBus.TYPE_NOTIFICATION, IMessageBus.EVENT_NOTIFICATION);
            eventMessage.setStringProperty(IMessageBus.TYPE_RESULT, message);

            this.context.createProducer().send(topic, eventMessage);
        } catch (JMSException e) {
            System.err.println("JMSException");
        }
    }

}
