package org.fiteagle.adapters.openstack.dm;

import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openstack.OpenstackAdapter;

@Singleton
@Startup
public class OpenstackAdapterMDBSender extends AbstractAdapterMDBSender{
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return OpenstackAdapter.adapterInstances;
  }

}
