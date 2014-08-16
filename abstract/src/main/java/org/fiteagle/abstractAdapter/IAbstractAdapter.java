package org.fiteagle.abstractAdapter;

import java.beans.PropertyChangeListener;
import java.io.InputStream;

public interface IAbstractAdapter {

    public String getAdapterDescription(String serializationFormat);

    public boolean createInstance(String instanceName);

    public boolean terminateInstance(String instanceName);

    public String monitorInstance(String instanceName, String serializationFormat);

    public String getAllInstances(String serializationFormat);

    public String controlInstance(String in, String serializationFormat);

    public boolean addChangeListener(AdapterEventListener newListener);

}
