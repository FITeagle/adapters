package org.fiteagle.adapters.Open5GCore.dm;

import java.util.Collection;

import javax.ejb.EJB;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.adapters.Open5GCore.Open5GCoreAdapterControl;

public class Open5GCoreAdapterEJB extends AbstractAdapterEJB {

    @EJB
    private transient Open5GCoreAdapterControl controller;
	
	@Override
	protected Collection<AbstractAdapter> getAdapterInstances() {
		// TODO Auto-generated method stub
		return null;
	}

}
