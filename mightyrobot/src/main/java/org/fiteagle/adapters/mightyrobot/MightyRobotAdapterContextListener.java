package org.fiteagle.adapters.mightyrobot;


import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


@WebListener
public class MightyRobotAdapterContextListener implements ServletContextListener {
    
  public void contextInitialized(ServletContextEvent event) {
      // see StateRestorator Class
  }

  public void contextDestroyed(ServletContextEvent event) {
      MightyRobotAdapter.getInstance().deregisterAdapter();
  }
}
