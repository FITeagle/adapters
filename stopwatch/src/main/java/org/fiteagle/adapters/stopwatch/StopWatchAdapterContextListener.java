package org.fiteagle.adapters.stopwatch;


import javax.servlet.annotation.WebListener;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterContextListener;


@WebListener
public class StopWatchAdapterContextListener extends AbstractAdapterContextListener {

  @Override
  protected AbstractAdapter getAdapter() {
    return StopWatchAdapter.getInstance();
  }
    
}