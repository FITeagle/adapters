package org.fiteagle.adapters.networking.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.networking.NetworkingAdapterControl;

@ServerEndpoint("/websocket")
public class NetworkingAdapterWebsocket extends AbstractAdapterWebsocket{
  
  @EJB
  NetworkingAdapterControl networkingAdapterControl;
  
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return networkingAdapterControl.getAdapterInstances();
  }
}
