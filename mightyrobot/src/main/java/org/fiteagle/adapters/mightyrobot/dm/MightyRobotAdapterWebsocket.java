package org.fiteagle.adapters.mightyrobot.dm;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.abstractdm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@ServerEndpoint("/websocket")
public class MightyRobotAdapterWebsocket extends AbstractAdapterWebsocket{

	@Override
    public AbstractAdapter handleSetup(){
      return MightyRobotAdapter.getInstance();
    }

}
