package org.fiteagle.adapters.stopwatch.dm;

import javax.annotation.PostConstruct;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.naming.NamingException;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;
import org.fiteagle.adapters.stopwatch.StopWatchAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "StopWatchAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class StopWatchAdapterMDBListener extends AbstractAdapterMDBListener {

    @PostConstruct
    public void setup() throws NamingException {
        this.adapterRDFHandler = StopWatchAdapterRDFHandler.getInstance();
        this.adapter = StopWatchAdapter.getInstance();
    }
}
