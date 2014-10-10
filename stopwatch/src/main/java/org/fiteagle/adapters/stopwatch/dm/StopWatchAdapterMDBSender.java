package org.fiteagle.adapters.stopwatch.dm;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;

@Singleton
@Startup
public class StopWatchAdapterMDBSender extends AbstractAdapterMDBSender {

  @Override
  protected AbstractAdapter getAdapter() {
    return StopWatchAdapter.getInstance();
  }
}
