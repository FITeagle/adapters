package org.fiteagle.adapters.openstack.dm;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.OpenstackAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "OpenstackAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OpenstackAdapterMDBListener extends AbstractAdapterMDBListener {
  
  @Override
  protected AbstractAdapter getAdapter() {
    return OpenstackAdapter.getInstance();
  }

  @Override
  protected AbstractAdapterRDFHandler getAdapterRDFHandler() {
    return OpenstackAdapterRDFHandler.getInstance();
  }
}