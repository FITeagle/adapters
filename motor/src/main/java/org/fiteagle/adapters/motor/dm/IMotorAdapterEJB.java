package org.fiteagle.adapters.motor.dm;

import org.fiteagle.abstractAdapter.AdapterEventListener;

public interface IMotorAdapterEJB {

    public String getAdapterDescription(String serializationFormat);

    public boolean createInstance(String instanceName);

    public boolean terminateInstance(String instanceName);

    public String monitorInstance(String instanceName, String serializationFormat);

    public String getAllInstances(String serializationFormat);

    public String controlInstance(String controlInput, String serializationFormat);

    public boolean addChangeListener(AdapterEventListener newListener);

}
