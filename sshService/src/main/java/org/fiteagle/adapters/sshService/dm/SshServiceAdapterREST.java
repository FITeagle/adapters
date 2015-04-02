package org.fiteagle.adapters.sshService.dm;

import java.util.Map;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.sshService.SshServiceAdapter;

@Path("/")
public class SshServiceAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return SshServiceAdapter.adapterInstances;
  }
  
}
