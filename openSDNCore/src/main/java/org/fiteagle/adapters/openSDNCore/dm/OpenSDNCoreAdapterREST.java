package org.fiteagle.adapters.openSDNCore.dm;

import java.util.Map;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.openSDNCore.OpenSDNCoreAdapter;

@Path("/")
public class OpenSDNCoreAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return OpenSDNCoreAdapter.adapterInstances;
  }
  
}
