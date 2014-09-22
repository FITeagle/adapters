package org.fiteagle.adapters.stopwatch;


import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


@WebListener
public class StopWatchAdapterContextListener implements ServletContextListener {
    
  public void contextInitialized(ServletContextEvent event) {
      // see StateRestorator Class
  }

  public void contextDestroyed(ServletContextEvent event) {
      StopWatchAdapter.getInstance().deregisterAdapter();
  }
}