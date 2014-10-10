package org.fiteagle.adapters.mightyrobot.dm;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Singleton
@Startup
public class MightyRobotAdapterMDBSender extends AbstractAdapterMDBSender {

  @Override
  protected AbstractAdapter getAdapter() {
    return MightyRobotAdapter.getInstance();
  }
}

