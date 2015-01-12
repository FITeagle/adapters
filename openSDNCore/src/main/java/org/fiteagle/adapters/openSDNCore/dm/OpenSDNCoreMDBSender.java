package org.fiteagle.adapters.openSDNCore.dm;

import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openSDNCore.OpenSDNCoreAdapter;

@Singleton
@Startup
public class OpenSDNCoreMDBSender extends AbstractAdapterMDBSender {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return OpenSDNCoreAdapter.adapterInstances;
  }
}
