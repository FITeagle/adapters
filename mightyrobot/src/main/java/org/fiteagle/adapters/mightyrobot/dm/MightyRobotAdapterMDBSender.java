package org.fiteagle.adapters.mightyrobot.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractMDBSender;
import org.fiteagle.abstractAdapter.dm.IAbstractMDBSender;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created by vju on 8/22/14.
 */

@Singleton(name = "MightyRobotAdapterMDBSender")
@Startup
@Remote(IAbstractMDBSender.class)
public class MightyRobotAdapterMDBSender extends AbstractMDBSender{

    private static Logger LOGGER = Logger.getLogger(MightyRobotAdapterMDBSender.class.toString());

    @Inject
    MightyRobotAdapter mra;

    @Override
    public AbstractAdapter getAdapter() {
        return mra;
    }

    @Override
    public Logger getLogger(){return LOGGER;};

}
