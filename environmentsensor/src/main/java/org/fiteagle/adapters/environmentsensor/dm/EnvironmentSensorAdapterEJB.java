package org.fiteagle.adapters.environmentsensor.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.environmentsensor.EnvironmentSensorAdapterControl;

@Singleton
@Remote(AdapterEJB.class)
class EnvironmentSensorAdapterEJB extends AbstractAdapterEJB {

    @EJB
    private transient EnvironmentSensorAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }
}
