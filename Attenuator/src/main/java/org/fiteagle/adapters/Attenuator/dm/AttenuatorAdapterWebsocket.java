package org.fiteagle.adapters.Attenuator.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapterControl;

@ServerEndpoint("/websocket")
public class AttenuatorAdapterWebsocket extends AbstractAdapterWebsocket{
  
  @EJB
  private transient AttenuatorAdapterControl controller;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return this.controller.getAdapterInstances();
  }
  
}
