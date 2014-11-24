package org.fiteagle.adapters.motor;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class DynamicMotor extends Motor {
  
  public DynamicMotor(MotorAdapter owningAdapter, String instanceName, Model createModel) {
    super(owningAdapter, instanceName, createModel);
    this.isDynamicThreadRunning = false;
    this.isDynamic = false;
  }
  
  public DynamicMotor() {
    super();
  }
  
  private boolean isDynamic;
  private boolean isDynamicThreadRunning;
  
  public void setIsDynamic(boolean state) {
    this.isDynamic = state;
    
    try {
      if (this.isDynamic && !isDynamicThreadRunning) {
        makeMotorDynamic();
      } else if (!this.isDynamic && isDynamicThreadRunning) {
        makeMotorStatic();
      }
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }
  
  private void makeMotorDynamic() throws NamingException {
    IMotorAdapterDynamic dynamicThread;
    dynamicThread = (IMotorAdapterDynamic) new InitialContext().lookup("java:module/MotorAdapterDynamic");
    
    dynamicThread.startThread(getInstanceName());
    
    isDynamicThreadRunning = true;
  }
  
  private void makeMotorStatic() throws NamingException {
    IMotorAdapterDynamic dynamicThread = (IMotorAdapterDynamic) new InitialContext().lookup("java:module/MotorAdapterDynamic");
    dynamicThread.endThread(getInstanceName());
    
    isDynamicThreadRunning = false;
  }
  
  public boolean isDynamic() {
    return this.isDynamic;
  }
  
  @Override
  public void updateProperty(Statement configureStatement) {
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
