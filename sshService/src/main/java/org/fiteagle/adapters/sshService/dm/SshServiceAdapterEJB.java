package org.fiteagle.adapters.sshService.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.sshService.SshServiceAdapterControl;


@Singleton
@Remote(AdapterEJB.class)
public class SshServiceAdapterEJB extends AbstractAdapterEJB{
  
  @EJB
  SshServiceAdapterControl sshServiceAdapterControl;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return sshServiceAdapterControl.getAdapterInstances();
  }
  
}
