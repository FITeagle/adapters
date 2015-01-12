package org.fiteagle.adapters.openMTC.dm;

import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.openMTC.OpenMTCAdapter;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "OpenMTCAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OpenMTCAdapterMDBListener extends AbstractAdapterMDBListener {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return OpenMTCAdapter.adapterInstances;
  }

}