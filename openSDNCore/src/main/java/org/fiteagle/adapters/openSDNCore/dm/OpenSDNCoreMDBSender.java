package org.fiteagle.adapters.openSDNCore.dm;

import java.util.Iterator;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openSDNCore.OpenSDNCoreAdapter;

@Singleton
@Startup
public class OpenSDNCoreMDBSender extends AbstractAdapterMDBSender {
  
  private static OpenSDNCoreAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if (adapter == null) {
      Iterator<String> iterator = OpenSDNCoreAdapter.adapterInstances.keySet().iterator();
      if (iterator.hasNext()) {
        adapter = OpenSDNCoreAdapter.getInstance(iterator.next());
      }
    }
    return adapter;
  }
}
