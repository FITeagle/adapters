package org.fiteagle.adapters.motor;


public class Motor {

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
        owningAdapter.notifyListeners(owningAdapter.createInformRDF(instanceName));
    }

    public int getMaxRpm() {
        return maxRpm;
    }

    public void setMaxRpm(int maxRpm) {
        this.maxRpm = maxRpm;
        owningAdapter.notifyListeners(owningAdapter.createInformRDF(instanceName));
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
        owningAdapter.notifyListeners(owningAdapter.createInformRDF(instanceName));
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
    
    public Motor(){
        super();
        
    }

    public String toString() {

        return "Motor";
    }

    private String manufacturer;
    private int rpm;
    private int maxRpm;
    private int throttle;
    protected MotorAdapter owningAdapter;
    
    public String getInstanceName() {
        return instanceName;
    }

    private String instanceName;

}
