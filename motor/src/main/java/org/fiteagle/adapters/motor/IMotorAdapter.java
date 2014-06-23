package org.fiteagle.adapters.motor;

import java.beans.PropertyChangeListener;
import java.io.InputStream;

public interface IMotorAdapter {
    //public void registerForEvents(IAdapterListener adapterDM);
        
    public String getAdapterDescription(String serializationFormat);

    public boolean createMotorInstance(int motorInstanceID);

    public boolean terminateMotorInstance(int motorInstanceID);

    public String monitorMotorInstance(int motorInstanceID, String serializationFormat);

    public String getAllMotorInstances(String serializationFormat);

    public String controlMotorInstance(InputStream in, String serializationFormat);
    
    public void addChangeListener(PropertyChangeListener newListener);
}