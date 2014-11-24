package org.fiteagle.adapters.motor;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


public class Motor {
    

    private String manufacturer;
    private int rpm;
    private int maxRpm;
    private int throttle;
    protected MotorAdapter owningAdapter;   
    private String instanceName;
    
    public Motor(){
        super();
    }
    
    public Motor(MotorAdapter owningAdapter, String instanceName) {
        super();
        this.manufacturer = "Fraunhofer FOKUS";
        this.rpm = 0;
        this.maxRpm = 3000;
        this.throttle = 0;
        
        this.owningAdapter = owningAdapter;
        this.instanceName = instanceName;
    }
    
    
    public String toString() {
        
        return "Motor";
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
    
    public void setRpmWithNotify(int rpm) {
        this.rpm = rpm;
        Model changedInstanceValues = owningAdapter.getSingleInstanceModel(instanceName); 
        owningAdapter.setModelPrefixes(changedInstanceValues);   
        owningAdapter.notifyListeners(changedInstanceValues, "");
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

  public void updateProperties(Statement configureStatement) {
    StmtIterator iter = configureStatement.getSubject().listProperties();
    
    while (iter.hasNext()) {
      Statement statement = iter.next();
      System.out.println();
      switch(statement.getPredicate().getLocalName()){
        case "rpm":
          this.setRpm(statement.getInt());
          break;
        case "maxRpm":
          this.setMaxRpm(statement.getInt());
          break;
        case "throttle":
          this.setRpm(statement.getInt());
          break;
        case "manufacturer":
          this.setManufacturer(statement.getString());
          break;
      }
    }
  }

}
