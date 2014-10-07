package org.fiteagle.adapters.openstack;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapterStateRestorator;

@Startup
@Singleton
public class OpenstackAdapterStateRestorator extends AbstractAdapterStateRestorator {

    @PostConstruct
    protected void startup() {  
       super.adapter = OpenstackAdapter.getInstance();
       super.adapterRDFHandler = OpenstackAdapterRDFHandler.getInstance();
       super.startup();   
   }

}
