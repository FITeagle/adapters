package org.fiteagle.adapters.mightyrobot.dm;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "MightyRobotAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MightyRobotAdapterMDBListener extends AbstractAdapterMDBListener {

    @Override
    protected AbstractAdapter getAdapter() {
      return MightyRobotAdapter.getInstance();
    }

    @Override
    protected AbstractAdapterRDFHandler getAdapterRDFHandler() {
      return MightyRobotAdapterRDFHandler.getInstance();
    }
}

