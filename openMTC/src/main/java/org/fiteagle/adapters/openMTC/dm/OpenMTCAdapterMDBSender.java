package org.fiteagle.adapters.openMTC.dm;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openMTC.OpenMTCAdapter;

@Singleton
@Startup
public class OpenMTCAdapterMDBSender extends AbstractAdapterMDBSender{
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return OpenMTCAdapter.adapterInstances;
  }

}
