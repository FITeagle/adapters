package org.fiteagle.adapters.mightyrobot;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by vju on 7/31/14.
 */
//This class is meant to do some startup tasks.
@Startup
@Singleton
public class EagerInitializer {
    @Inject
    MightyRobotAdapter mra;
    @PostConstruct
    private void init(){
        //as mra cannot be is lazily initialized we need to force a startup
        mra.init();
    }

}
