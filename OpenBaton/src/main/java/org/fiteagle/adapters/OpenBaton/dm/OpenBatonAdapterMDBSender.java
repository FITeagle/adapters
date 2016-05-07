package org.fiteagle.adapters.OpenBaton.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.OpenBaton.OpenBatonAdapterControl;

@Singleton
public class OpenBatonAdapterMDBSender extends AbstractAdapterMDBSender {

    @EJB
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient OpenBatonAdapterControl controller;

}
