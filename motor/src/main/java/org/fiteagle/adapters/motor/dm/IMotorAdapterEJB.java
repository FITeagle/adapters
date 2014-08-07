package org.fiteagle.adapters.motor.dm;

import java.beans.PropertyChangeListener;

public interface IMotorAdapterEJB {

    public String getAdapterDescription(String serializationFormat);

    public boolean createInstance(int instanceID);

    public boolean terminateInstance(int instanceID);

    public String monitorInstance(int instanceID, String serializationFormat);

    public String getAllInstances(String serializationFormat);

    public String controlInstance(String controlInput, String serializationFormat);

    public boolean addChangeListener(PropertyChangeListener newListener);

}
