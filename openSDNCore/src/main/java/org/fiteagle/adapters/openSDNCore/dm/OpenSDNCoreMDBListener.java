package org.fiteagle.adapters.openSDNCore.dm;

import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.openSDNCore.OpenSDNCoreAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageFilters;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = MessageFilters.FILTER_ADAPTER),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OpenSDNCoreMDBListener extends AbstractAdapterMDBListener {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return OpenSDNCoreAdapter.adapterInstances;
  }
  
}
