package org.fiteagle.adapters.motor.dm;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.abstractdm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.motor.MotorAdapter;

@ServerEndpoint("/websocket")
public class MotorAdapterWebsocket extends AbstractAdapterWebsocket{

	@Override
    public AbstractAdapter handleSetup(){
      return MotorAdapter.getInstance();
    }

}
