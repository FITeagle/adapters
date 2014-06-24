package org.fiteagle.adapters.motor.dm;

import java.beans.PropertyChangeListener;
import java.io.InputStream;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.adapters.motor.IAdapterListener;
import org.fiteagle.adapters.motor.IMotorAdapter;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton(name = "MotorAdapter")
@Remote(IMotorAdapter.class)
public class MotorAdapterEJB implements IMotorAdapter {
    private final MotorAdapter adapter;

    public MotorAdapterEJB() {
        this.adapter = MotorAdapter.getInstance();
    }

    // @Override
    // public void registerForEvents(IAdapterListener adapterDM) {
    // this.adapter.registerForEvents(adapterDM);
    // }

    @Override
    public String getAdapterDescription(String serializationFormat) {
        return this.adapter.getAdapterDescription(serializationFormat);
    }

    @Override
    public boolean createMotorInstance(int motorInstanceID) {
        return this.adapter.createMotorInstance(motorInstanceID);
    }

    @Override
    public boolean terminateMotorInstance(int motorInstanceID) {
        return this.adapter.terminateMotorInstance(motorInstanceID);
    }

    @Override
    public String monitorMotorInstance(int motorInstanceID, String serializationFormat) {
        return this.adapter.monitorMotorInstance(motorInstanceID, serializationFormat);
    }

    @Override
    public String getAllMotorInstances(String serializationFormat) {
        return this.adapter.getAllMotorInstances(serializationFormat);
    }

    @Override
    public String controlMotorInstance(InputStream in, String serializationFormat) {
        return this.adapter.controlMotorInstance(in, serializationFormat);
    }
    
    @Override
    public void addChangeListener(PropertyChangeListener newListener){
        this.adapter.addChangeListener(newListener);
    }

}
