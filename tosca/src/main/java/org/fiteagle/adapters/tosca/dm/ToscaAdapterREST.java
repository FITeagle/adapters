package org.fiteagle.adapters.tosca.dm;

import java.util.Map;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.tosca.ToscaAdapter;

@Path("/")
public class ToscaAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return ToscaAdapter.adapterInstances;
  }
  
}
