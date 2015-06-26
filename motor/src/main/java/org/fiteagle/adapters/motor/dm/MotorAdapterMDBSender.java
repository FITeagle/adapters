package org.fiteagle.adapters.motor.dm;


import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.motor.MotorAdapterControl;

@Singleton
public class MotorAdapterMDBSender extends AbstractAdapterMDBSender {

  @EJB
  MotorAdapterControl adapterControl;

  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return adapterControl.getAdapterInstances();
  }
}
