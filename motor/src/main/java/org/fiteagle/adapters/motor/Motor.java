package org.fiteagle.adapters.motor;

public class Motor {

    public String getManufacturer() {
        return manufacturer;
    } 

    public void setManufacturer(String manufacturer) {
        owningAdapter.notifyListeners(this, "manufacturer", this.manufacturer, this.manufacturer = manufacturer);
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        owningAdapter.notifyListeners(this, "rpm", "" + this.rpm, "" + rpm);
        this.rpm = rpm;
    }

    public int getMaxRpm() {
        return maxRpm;
    }

    public void setMaxRpm(int maxRpm) {
        owningAdapter.notifyListeners(this, "maxRpm", "" + this.maxRpm, "" + maxRpm);
        this.maxRpm = maxRpm;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        owningAdapter.notifyListeners(this, "throttle", "" + this.throttle, "" + throttle);
        this.throttle = throttle;
    }

    public Motor(MotorAdapter owningAdapter) {
        super();
        this.manufacturer = "Fraunhofer FOKUS";
        this.rpm = 0;
        this.maxRpm = 3000;
        this.throttle = 0;

        this.owningAdapter = owningAdapter;
    }

    public String toString() {

        return "Motor";
    }

    private String manufacturer;
    private int rpm;
    private int maxRpm;
    private int throttle;
    private MotorAdapter owningAdapter;

}
