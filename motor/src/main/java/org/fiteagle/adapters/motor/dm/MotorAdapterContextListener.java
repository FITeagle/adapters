package org.fiteagle.adapters.motor.dm;


import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.fiteagle.adapters.motor.MotorAdapter;

@WebListener
public class MotorAdapterContextListener implements ServletContextListener {
    
  public void contextInitialized(ServletContextEvent event) {
      MotorAdapter.getInstance().registerAdapter();
  }

  public void contextDestroyed(ServletContextEvent event) {
      MotorAdapter.getInstance().deregisterAdapter();
  }
}