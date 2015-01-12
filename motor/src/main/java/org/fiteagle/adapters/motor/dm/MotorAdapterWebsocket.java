package org.fiteagle.adapters.motor.dm;

import java.util.Map;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.motor.MotorAdapter;

@ServerEndpoint("/websocket")
public class MotorAdapterWebsocket extends AbstractAdapterWebsocket {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return MotorAdapter.adapterInstances;
  }
}
