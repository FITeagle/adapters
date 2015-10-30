package org.fiteagle.adapters.epc.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.epc.EpcAdapterControl;

@ServerEndpoint("/websocket")
class EpcAdapterWebsocket extends AbstractAdapterWebsocket {

    @EJB
    private transient EpcAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }
}
