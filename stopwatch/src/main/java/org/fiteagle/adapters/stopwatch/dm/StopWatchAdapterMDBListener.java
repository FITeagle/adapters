package org.fiteagle.adapters.stopwatch.dm;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;
import org.fiteagle.adapters.stopwatch.StopWatchAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "StopWatchAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class StopWatchAdapterMDBListener extends AbstractAdapterMDBListener {

    @Override
    protected AbstractAdapter getAdapter() {
      return StopWatchAdapter.getInstance();
    }

    @Override
    protected AbstractAdapterRDFHandler getAdapterRDFHandler() {
      return  StopWatchAdapterRDFHandler.getInstance();
    }
}
