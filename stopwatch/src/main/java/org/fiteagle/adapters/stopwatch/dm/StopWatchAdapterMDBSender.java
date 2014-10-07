package org.fiteagle.adapters.stopwatch.dm;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;

@Singleton
@Startup
public class StopWatchAdapterMDBSender extends AbstractAdapterMDBSender {

    @PostConstruct
     protected void startup() {  
        super.adapter = StopWatchAdapter.getInstance();
        super.startup();       
    }
}
