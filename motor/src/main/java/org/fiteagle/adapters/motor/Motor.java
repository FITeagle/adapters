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
    
    public Motor(MotorAdapter owningAdapter, String instanceName, Model createModel) {
        super();
        this.manufacturer = "Fraunhofer FOKUS";
        this.rpm = 0;
        this.maxRpm = 3000;
        this.throttle = 0;
        
        StmtIterator iter = createModel.listStatements();
        while(iter.hasNext()){
          updateProperty(iter.next());
        }
        
        this.owningAdapter = owningAdapter;
        this.instanceName = instanceName;
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
    }
  }

}
