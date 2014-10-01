package org.fiteagle.adapters.openstack.dm;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.naming.NamingException;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.OpenstackAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(name = "OpenstackAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OpenstackAdapterMDBListener extends AbstractAdapterMDBListener {
  
  @PostConstruct
  public void setup() throws NamingException {
      this.adapterRDFHandler = OpenstackAdapterRDFHandler.getInstance();
      this.adapter = OpenstackAdapter.getInstance();
  }
}