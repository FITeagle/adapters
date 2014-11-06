package org.fiteagle.adapters.openMTC.dm;

import java.util.Iterator;

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
  
  private static OpenMTCAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if(adapter == null){
      Iterator<String> iter = OpenMTCAdapter.adapterInstances.keySet().iterator();
      if(iter.hasNext()){
          adapter = OpenMTCAdapter.getInstance(iter.next());
      }
    }
    return adapter;
  }

}