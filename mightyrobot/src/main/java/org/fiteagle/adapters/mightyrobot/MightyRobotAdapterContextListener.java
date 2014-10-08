package org.fiteagle.adapters.mightyrobot;


import javax.servlet.annotation.WebListener;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterContextListener;

@WebListener
public class MightyRobotAdapterContextListener extends AbstractAdapterContextListener {

  @Override
  protected AbstractAdapter getAdapterInstance() {
    return MightyRobotAdapter.getInstance();
  }
    
}
