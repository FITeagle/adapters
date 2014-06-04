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
    
    
    public Motor() {
        super();
        this.manufacturer = "Fraunhofer FOKUS";
        this.rpm = 0;
        this.maxRpm = 3000;
        this.throttle = 0;
    }


    private String manufacturer;    
    private int rpm;
    private int maxRpm;
    private int throttle;
    
    


    
}
