package org.fiteagle.adapters.openSDNCore.dm;

import java.util.Iterator;

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
  
  private static OpenSDNCoreAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if (adapter == null) {
      Iterator<String> iterator = OpenSDNCoreAdapter.adapterInstances.keySet().iterator();
      if (iterator.hasNext()) {
        adapter = OpenSDNCoreAdapter.getInstance(iterator.next());
      }
    }
    return adapter;
  }
  
}
