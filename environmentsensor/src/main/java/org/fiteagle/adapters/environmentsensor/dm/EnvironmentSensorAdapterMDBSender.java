package org.fiteagle.adapters.environmentsensor.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.environmentsensor.EnvironmentSensorAdapterControl;

@Singleton
public class EnvironmentSensorAdapterMDBSender extends AbstractAdapterMDBSender {

    @EJB
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient EnvironmentSensorAdapterControl controller;

}
