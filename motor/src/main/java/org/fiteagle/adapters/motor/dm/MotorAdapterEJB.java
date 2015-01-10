package org.fiteagle.adapters.motor.dm;

import java.util.Iterator;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapterEJB;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton
@Remote(IAbstractAdapterEJB.class)
public class MotorAdapterEJB extends AbstractAdapterEJB {
  
  private static MotorAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if (adapter == null) {
      Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
      if (iterator.hasNext()) {
        adapter = MotorAdapter.getInstance(iterator.next());
      }
    }
    return adapter;
  }
}
