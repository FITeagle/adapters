package org.fiteagle.adapters.epc.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.epc.EpcAdapterControl;

@Singleton
public class EpcAdapterMDBSender extends AbstractAdapterMDBSender {

    @EJB
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient EpcAdapterControl controller;

}
