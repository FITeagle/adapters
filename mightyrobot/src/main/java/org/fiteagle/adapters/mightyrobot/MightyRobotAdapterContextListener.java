package org.fiteagle.adapters.mightyrobot;


import javax.jms.JMSException;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterContextListener;

@WebListener
public class MightyRobotAdapterContextListener extends AbstractAdapterContextListener {

  @Override
  public void contextInitialized(ServletContextEvent event) {
    System.out.println(this.getClass().getSimpleName() + ": Registering adapter " + getAdapter());
    
    getAdapter().registerAdapter();
    
    // At this point maybe some parameters of the adapter itself should be restored as well?!
    // getAdapter().restoreAdapterParameters();
    
    try {
      restoreState();
    } catch (JMSException e) {
//      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }
  
  
  
  @Override
  protected AbstractAdapter getAdapter() {
    return MightyRobotAdapter.getInstance();
  }
    
}
