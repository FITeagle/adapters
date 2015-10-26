package org.fiteagle.adapters.motor.dm;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.motor.MotorAdapterControl;

@Singleton
public class MotorAdapterMDBSender extends AbstractAdapterMDBSender {

    @EJB
    @SuppressWarnings("PMD.UnusedPrivateField")
    private transient MotorAdapterControl controller;

}
