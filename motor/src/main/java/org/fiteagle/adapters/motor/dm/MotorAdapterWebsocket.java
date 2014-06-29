package org.fiteagle.adapters.motor.dm;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.adapters.AbstractAdapter;
import org.fiteagle.adapters.abstractdm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.motor.MotorAdapter;

@ServerEndpoint("/websocket")
public class MotorAdapterWebsocket extends AbstractAdapterWebsocket{

	@Override
    public AbstractAdapter handleSetup(){
      return MotorAdapter.getInstance();
    }

}
