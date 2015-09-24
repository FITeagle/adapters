package org.fiteagle.adapters.networking.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.networking.NetworkingAdapterControl;

@Singleton
@Remote(AdapterEJB.class)
public class NetworkingAdapterEJB extends AbstractAdapterEJB {
  
  @EJB
  NetworkingAdapterControl networkingAdapterControl;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return networkingAdapterControl.getAdapterInstances();
  }
  
  
}
