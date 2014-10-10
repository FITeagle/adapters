package org.fiteagle.adapters.motor.dm;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton
@Startup
public class MotorAdapterMDBSender extends AbstractAdapterMDBSender {

  @Override
  protected AbstractAdapter getAdapter() {
    return MotorAdapter.getInstance();
  }
}
