package org.fiteagle.adapters.sshService.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.sshService.SshServiceAdapter;
import org.fiteagle.adapters.sshService.SshServiceAdapterControl;

@Path("/")
public class SshServiceAdapterREST extends AbstractAdapterREST {
  
  @EJB
  SshServiceAdapterControl sshServiceAdapterControl;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return sshServiceAdapterControl.getAdapterInstances();
  }
  
  
  
  
  
}
