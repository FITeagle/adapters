package org.fiteagle.adapters.Open5GCore.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.Open5GCore.Open5GCoreAdapterControl;

@Singleton
public class Open5GCoreAdapterMDBSender extends AbstractAdapterMDBSender {

    @EJB
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient Open5GCoreAdapterControl controller;

}
