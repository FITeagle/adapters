package org.fiteagle.adapters.emulatedDevices.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.emulatedDevices.EmulatedDevicesAdapterControl;

@Singleton
public class EmulatedDevicesAdapterMDBSender extends AbstractAdapterMDBSender {

    @EJB
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient EmulatedDevicesAdapterControl controller;

}
