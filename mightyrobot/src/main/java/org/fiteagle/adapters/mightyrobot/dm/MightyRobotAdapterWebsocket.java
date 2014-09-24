package org.fiteagle.adapters.mightyrobot.dm;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@ServerEndpoint("/websocket")
public class MightyRobotAdapterWebsocket extends AbstractAdapterWebsocket{

    @Inject
    MightyRobotAdapter mightyRobotAdapter;

	@Override
    public AbstractAdapter handleSetup(){
      return mightyRobotAdapter;
    }

}
