package org.fiteagle.adapters.sshService.dm;

import java.util.Collection;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.sshService.SshServiceAdapter;

@Path("/")
public class SshServiceAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return SshServiceAdapter.adapterInstances;
  }
  
  
  
}
