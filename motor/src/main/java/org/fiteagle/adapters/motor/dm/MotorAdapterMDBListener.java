package org.fiteagle.adapters.motor.dm;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.adapters.motor.MotorAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDBListener extends AbstractAdapterMDBListener {

    @PostConstruct
    public void setup() {
        this.adapterRDFHandler = MotorAdapterRDFHandler.getInstance();
        this.adapter = MotorAdapter.getInstance();
    }
}
