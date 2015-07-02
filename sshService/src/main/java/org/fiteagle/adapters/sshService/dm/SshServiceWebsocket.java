package org.fiteagle.adapters.sshService.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.sshService.SshServiceAdapterControl;


@ServerEndpoint("/websocket")
public class SshServiceWebsocket extends AbstractAdapterWebsocket{
  
  @EJB
  SshServiceAdapterControl sshServiceControl;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return sshServiceControl.getAdapterInstances();
  }
}
