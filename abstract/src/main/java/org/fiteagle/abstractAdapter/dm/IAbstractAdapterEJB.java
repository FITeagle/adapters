package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AdapterEventListener;

public interface IAbstractAdapterEJB {
    public String getAdapterDescription(String serializationFormat);

    public boolean createInstance(String instanceName);

    public boolean terminateInstance(String instanceName);

    public String monitorInstance(String instanceName, String serializationFormat);

    public String getAllInstances(String serializationFormat);

    public String configureInstance(String configureInput, String serializationFormat);

    public boolean addChangeListener(AdapterEventListener newListener);
}

