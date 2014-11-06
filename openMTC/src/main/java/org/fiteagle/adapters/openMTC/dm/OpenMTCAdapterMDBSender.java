package org.fiteagle.adapters.openMTC.dm;

import java.util.Iterator;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openMTC.OpenMTCAdapter;

@Singleton
@Startup
public class OpenMTCAdapterMDBSender extends AbstractAdapterMDBSender{
  
  private static OpenMTCAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if(adapter == null){
      Iterator<String> iter = OpenMTCAdapter.adapterInstances.keySet().iterator();
      if(iter.hasNext()){
          adapter = OpenMTCAdapter.getInstance(iter.next());
      }
    }
    return adapter;
  }

}
