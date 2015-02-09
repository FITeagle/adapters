package org.fiteagle.adapters.openstack.dm;

import java.util.Map;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.openstack.OpenstackAdapter;

@Path("/")
public class OpenstackAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return OpenstackAdapter.adapterInstances;
  }
  
}
