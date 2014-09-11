package org.fiteagle.adapters.motor;


import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


@WebListener
public class MotorAdapterContextListener implements ServletContextListener {
    
  public void contextInitialized(ServletContextEvent event) {
      // see StateRestorator Class
  }

  public void contextDestroyed(ServletContextEvent event) {
      MotorAdapter.getInstance().deregisterAdapter();
  }
}