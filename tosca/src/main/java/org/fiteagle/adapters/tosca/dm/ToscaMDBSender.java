package org.fiteagle.adapters.tosca.dm;

import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.tosca.ToscaAdapter;

@Singleton
@Startup
public class ToscaMDBSender extends AbstractAdapterMDBSender {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return ToscaAdapter.adapterInstances;
  }
}
