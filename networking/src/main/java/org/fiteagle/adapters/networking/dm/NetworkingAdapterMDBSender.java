package org.fiteagle.adapters.networking.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.networking.NetworkingAdapterControl;

@Singleton
public class NetworkingAdapterMDBSender extends AbstractAdapterMDBSender {
  
  @EJB
  NetworkingAdapterControl adapterControl;
  
}
