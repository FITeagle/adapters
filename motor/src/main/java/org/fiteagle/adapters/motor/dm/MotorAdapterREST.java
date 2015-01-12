package org.fiteagle.adapters.motor.dm;

import java.util.Map;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.motor.MotorAdapter;

@Path("/")
public class MotorAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return MotorAdapter.adapterInstances;
  }
}
