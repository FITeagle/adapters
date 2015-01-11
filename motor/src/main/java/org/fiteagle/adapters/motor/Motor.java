package org.fiteagle.adapters.motor;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class Motor {
  
  private String manufacturer;
  private int rpm;
  private int maxRpm;
  private int throttle;
  protected MotorAdapter owningAdapter;
  private String instanceName;
  private boolean isDynamic;
  
  private static Logger LOGGER = Logger.getLogger(Motor.class.toString());  
  
  public Motor(MotorAdapter owningAdapter, String instanceName) {
    this.isDynamic = false;
    this.manufacturer = "Fraunhofer FOKUS";
    this.rpm = 0;
    this.maxRpm = 3000;
    this.throttle = 0;
    
    this.owningAdapter = owningAdapter;
    this.instanceName = instanceName;
  }
  
  public void setIsDynamic(boolean state) {
    isDynamic = state;
    
    if (isDynamic && !threadIsRunning()) {
      startThread();
    } else if (!isDynamic && threadIsRunning()) {
      terminate();
    }
  }
  
  public void setRpmWithNotify(int rpm) {
    this.rpm = rpm;
    Model resourceModel = owningAdapter.parseToModel(this);
    owningAdapter.notifyListeners(resourceModel, null, IMessageBus.TYPE_INFORM, null);
  }
  
  public String getManufacturer() {
    return manufacturer;
  }
  
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }
  
  public int getRpm() {
    return rpm;
  }
  
  public void setRpm(int rpm) {
    this.rpm = rpm;
  }
  
  public int getMaxRpm() {
    return maxRpm;
  }
  
  public void setMaxRpm(int maxRpm) {
    this.maxRpm = maxRpm;
  }
  
  public int getThrottle() {
    return throttle;
  }
  
  public void setThrottle(int throttle) {
    this.throttle = throttle;
  }
  
  public String getInstanceName() {
    return instanceName;
  }
  
  public boolean isDynamic() {
    return this.isDynamic;
  }
  
  public void updateProperty(Statement configureStatement) {
    if(configureStatement.getSubject().getLocalName().equals(instanceName)){
      switch (configureStatement.getPredicate().getLocalName()) {
        case "rpm":
          this.setRpm(configureStatement.getInt());
          break;
        case "maxRpm":
          this.setMaxRpm(configureStatement.getInt());
          break;
        case "throttle":
          this.setRpm(configureStatement.getInt());
          break;
        case "manufacturer":
          this.setManufacturer(configureStatement.getString());
          break;
        case "isDynamic":
          this.setIsDynamic(configureStatement.getBoolean());
          break;
      }
    }
  }
  
  private Thread thread;
  
  private final static String threadFactoryURI = "java:jboss/ee/concurrency/factory/default";
  
  private ManagedThreadFactory threadFactory;
  
  private void startThread() {
    RPMCreator creator = new RPMCreator();
    if(threadFactory == null){
      Context context;
      try {
        context = new InitialContext();
        threadFactory = (ManagedThreadFactory) context.lookup(threadFactoryURI);
      } catch (NamingException e) {
        LOGGER.log(Level.SEVERE, "Could not create managed thread factory: "+threadFactoryURI);
      }
    }
    thread = threadFactory.newThread(creator);
    thread.start();
  }
  
  public void terminate() {
    if(thread != null){
      thread.interrupt();
    }
  }
  
  private boolean threadIsRunning() {
    if (thread != null && thread.isAlive()) {
      return true;
    }
    return false;
  }
  
  public class RPMCreator implements Runnable {
    
    private static final int SLEEP_TIME = 5000;
    
    private Random randomRPMGenerator = new Random();
    
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
          return;
        }
        setRpmWithNotify(randomRPMGenerator.nextInt(maxRpm));
      }
    }
  }
  
}
