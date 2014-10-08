package org.fiteagle.abstractAdapter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class AbstractAdapterContextListener implements ServletContextListener {
  
  @Override
  public void contextInitialized(ServletContextEvent event) {
      
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
      getAdapterInstance().deregisterAdapter();
  }
  
  protected abstract AbstractAdapter getAdapterInstance();
}
