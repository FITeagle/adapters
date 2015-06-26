package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.tosca.ToscaAdapter;

@Path("/")
public class ToscaAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return ToscaAdapter.adapterInstances;
  }
  
}
