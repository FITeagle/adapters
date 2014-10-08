package org.fiteagle.adapters.openstack;


import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


@WebListener
public class OpenstackAdapterContextListener implements ServletContextListener {
    
  @Override
  public void contextInitialized(ServletContextEvent event) {
      // see StateRestorator Class
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
      OpenstackAdapter.getInstance().deregisterAdapter();
  }
}
