package org.fiteagle.adapters.emulatedDevices.dm;

import java.util.Collection;

import javax.ejb.EJB;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.adapters.emulatedDevices.EmulatedDevicesAdapterControl;

public class EmulatedDevicesAdapterEJB extends AbstractAdapterEJB {

    @EJB
    private transient EmulatedDevicesAdapterControl controller;
	
	@Override
	protected Collection<AbstractAdapter> getAdapterInstances() {
		// TODO Auto-generated method stub
		return null;
	}

}
