package org.fiteagle.adapters.stopwatch;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapterStateRestorator;

@Startup
@Singleton
public class StopWatchAdapterStateRestorator extends AbstractAdapterStateRestorator {

    @PostConstruct
    protected void startup() {  
       super.adapter = StopWatchAdapter.getInstance();
       super.adapterRDFHandler = StopWatchAdapterRDFHandler.getInstance();
       super.startup();   
   }

}
