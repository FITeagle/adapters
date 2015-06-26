package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.tosca.ToscaAdapter;

@Singleton
@Startup
public class ToscaMDBSender extends AbstractAdapterMDBSender {
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return ToscaAdapter.adapterInstances;
  }
}
