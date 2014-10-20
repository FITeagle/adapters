package org.fiteagle.adapters.openstack.dm;

import java.util.Iterator;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openstack.OpenstackAdapter;

@Singleton
@Startup
public class OpenstackAdapterMDBSender extends AbstractAdapterMDBSender{
  
  private static OpenstackAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if(adapter == null){
      Iterator<String> iter = OpenstackAdapter.openstackAdapterInstances.keySet().iterator();
      if(iter.hasNext()){
          adapter = OpenstackAdapter.getInstance(iter.next());
      }
    }
    return adapter;
  }

}
