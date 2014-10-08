package org.fiteagle.adapters.motor;


import javax.servlet.annotation.WebListener;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterContextListener;


@WebListener
public class MotorAdapterContextListener extends AbstractAdapterContextListener {

  @Override
  protected AbstractAdapter getAdapterInstance() {
    return MotorAdapter.getInstance();
  }
    
}