package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.tosca.ToscaAdapter;

@ServerEndpoint("/")
public class ToscaWebsocket extends AbstractAdapterWebsocket {
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return ToscaAdapter.adapterInstances;
  }
}
