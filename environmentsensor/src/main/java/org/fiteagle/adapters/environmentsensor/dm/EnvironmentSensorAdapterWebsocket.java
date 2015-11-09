package org.fiteagle.adapters.environmentsensor.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.environmentsensor.EnvironmentSensorAdapterControl;

@ServerEndpoint("/websocket")
class EnvironmentSensorAdapterWebsocket extends AbstractAdapterWebsocket {

    @EJB
    private transient EnvironmentSensorAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }
}
