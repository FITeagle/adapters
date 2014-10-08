package org.fiteagle.adapters.motor.dm;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton
@Startup
public class MotorAdapterMDBSender extends AbstractAdapterMDBSender {

    @Override
    @PostConstruct
    protected void startup() {  
      startup(MotorAdapter.getInstance());       
    }
}
