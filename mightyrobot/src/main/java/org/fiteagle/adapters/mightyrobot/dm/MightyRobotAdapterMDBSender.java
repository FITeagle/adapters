package org.fiteagle.adapters.mightyrobot.dm;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Singleton
@Startup
public class MightyRobotAdapterMDBSender extends AbstractAdapterMDBSender {

    @PostConstruct
     protected void startup() {  
        super.adapter = MightyRobotAdapter.getInstance();
        super.startup();       
    }
}

