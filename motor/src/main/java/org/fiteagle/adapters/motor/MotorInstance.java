package org.fiteagle.adapters.motor;

public class MotorInstance {
    
    private String name;
    private String location;
    private String type;
    private String manufacturer;
    
    private boolean status;
    private int currentRotationalSpeed;
    private int maxRotationalSpeed;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public int getCurrentRotationalSpeed() {
        return currentRotationalSpeed;
    }
    public void setCurrentRotationalSpeed(int currentRotationalSpeed) {
        this.currentRotationalSpeed = currentRotationalSpeed;
    }
    public int getMaxRotationalSpeed() {
        return maxRotationalSpeed;
    }
    public void setMaxRotationalSpeed(int maxRotationalSpeed) {
        this.maxRotationalSpeed = maxRotationalSpeed;
    }
    
}
