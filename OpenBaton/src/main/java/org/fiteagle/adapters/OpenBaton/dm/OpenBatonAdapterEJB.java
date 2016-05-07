package org.fiteagle.adapters.OpenBaton.dm;

import java.util.Collection;

import javax.ejb.EJB;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.adapters.OpenBaton.OpenBatonAdapterControl;

public class OpenBatonAdapterEJB extends AbstractAdapterEJB {

    @EJB
    private transient OpenBatonAdapterControl controller;
	
	@Override
	protected Collection<AbstractAdapter> getAdapterInstances() {
		// TODO Auto-generated method stub
		return null;
	}

}
