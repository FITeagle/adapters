package org.fiteagle.adapters.motor.dm;

import java.util.Iterator;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.motor.MotorAdapter;

@Path("/")
public class MotorAdapterREST extends AbstractAdapterREST {

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
