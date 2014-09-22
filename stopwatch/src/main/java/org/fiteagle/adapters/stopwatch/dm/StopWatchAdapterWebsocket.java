package org.fiteagle.adapters.stopwatch.dm;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;

@ServerEndpoint("/websocket")
public class StopWatchAdapterWebsocket extends AbstractAdapterWebsocket {

    @Override
    public AbstractAdapter handleSetup() {
        return StopWatchAdapter.getInstance();
    }

}
