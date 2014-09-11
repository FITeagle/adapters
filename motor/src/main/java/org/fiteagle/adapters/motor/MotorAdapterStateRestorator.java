package org.fiteagle.adapters.motor;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapterStateRestorator;

@Singleton
@Startup
public class MotorAdapterStateRestorator extends AbstractAdapterStateRestorator {

    @PostConstruct
    protected void startup() {  
       super.adapter = MotorAdapter.getInstance();
       super.adapterRDFHandler = MotorAdapterRDFHandler.getInstance();
       super.startup();       
   }

}
