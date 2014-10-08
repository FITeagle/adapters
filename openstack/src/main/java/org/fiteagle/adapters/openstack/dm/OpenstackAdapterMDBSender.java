package org.fiteagle.adapters.openstack.dm;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.openstack.OpenstackAdapter;

@Singleton
@Startup
public class OpenstackAdapterMDBSender extends AbstractAdapterMDBSender{
  
  @Override
  @PostConstruct
  protected void startup() {  
     startup(OpenstackAdapter.getInstance());   
  }
}
