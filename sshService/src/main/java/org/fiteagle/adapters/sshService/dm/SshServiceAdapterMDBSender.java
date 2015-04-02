package org.fiteagle.adapters.sshService.dm;

import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.sshService.*;
import org.fiteagle.adapters.sshService.SshService;

@Singleton
@Startup
public class SshServiceAdapterMDBSender extends AbstractAdapterMDBSender {

  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return SshServiceAdapter.adapterInstances;
  }
}
