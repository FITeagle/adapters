package org.fiteagle.adapters.motor.dm;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@WebListener
public class MotorAdapterContextListener implements ServletContextListener {
  public void contextInitialized(ServletContextEvent event) {
  
      IMotorAdapterMDBSender sender;
      try {
        sender = (IMotorAdapterMDBSender) new InitialContext().lookup("java:module/MotorAdapterMDBSender");
        sender.registerAdapter();
    } catch (NamingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }

  public void contextDestroyed(ServletContextEvent event) {
      
      IMotorAdapterMDBSender sender;
      try {
        sender = (IMotorAdapterMDBSender) new InitialContext().lookup("java:module/MotorAdapterMDBSender");
        sender.unregisterAdapter();
    } catch (NamingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
}