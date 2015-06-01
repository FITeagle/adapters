package org.fiteagle.adapters.motor.dm;

import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton
@Remote(AdapterEJB.class)
class MotorAdapterEJB extends AbstractAdapterEJB {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return MotorAdapter.adapterInstances;
  }
}
