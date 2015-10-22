package org.fiteagle.adapters.motor.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.motor.MotorAdapterControl;

@ServerEndpoint("/websocket")
class MotorAdapterWebsocket extends AbstractAdapterWebsocket {

    @EJB
    private transient MotorAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }
}
