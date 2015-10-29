package org.fiteagle.adapters.ACSclient.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.ACSclient.ACSclientAdapterControl;

@ServerEndpoint("/websocket")
public class ACSclientAdapterWebsocket extends AbstractAdapterWebsocket{
  
  @EJB
  private transient ACSclientAdapterControl controller;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return this.controller.getAdapterInstances();
  }
}
