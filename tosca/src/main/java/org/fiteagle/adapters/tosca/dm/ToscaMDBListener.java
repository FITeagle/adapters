package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.tosca.ToscaAdapter;
import org.fiteagle.adapters.tosca.ToscaAdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageFilters;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = MessageFilters.FILTER_ADAPTER),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ToscaMDBListener extends AbstractAdapterMDBListener {

    @EJB
    ToscaAdapterControl toscaAdapterControl;
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return toscaAdapterControl.getAdapterInstances();
  }
  
}
