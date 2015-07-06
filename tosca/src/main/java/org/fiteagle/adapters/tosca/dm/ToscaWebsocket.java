package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.tosca.ToscaAdapter;
import org.fiteagle.adapters.tosca.ToscaAdapterControl;

@ServerEndpoint("/")
public class ToscaWebsocket extends AbstractAdapterWebsocket {

  @EJB
  ToscaAdapterControl adapterControl;
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return adapterControl.getAdapterInstances();
  }
}
