package org.fiteagle.adapters.mightyrobot;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapterStateRestorator;

@Startup
@Singleton
public class MightyRobotAdapterStateRestorator extends AbstractAdapterStateRestorator {

    @PostConstruct
    protected void startup() {  
       super.adapter = MightyRobotAdapter.getInstance();
       super.adapterRDFHandler = MightyRobotAdapterRDFHandler.getInstance();
       super.startup();   
   }

}
