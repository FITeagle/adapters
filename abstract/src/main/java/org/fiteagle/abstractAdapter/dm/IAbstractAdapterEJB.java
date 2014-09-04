package org.fiteagle.abstractAdapter.dm;

import java.util.List;

import org.fiteagle.abstractAdapter.AdapterEventListener;
import com.hp.hpl.jena.rdf.model.Statement;

public interface IAbstractAdapterEJB {
    public String getAdapterDescription(String serializationFormat);

    public boolean createInstance(String instanceName);

    public boolean terminateInstance(String instanceName);

    public String monitorInstance(String instanceName, String serializationFormat);

    public String getAllInstances(String serializationFormat);

    public List<String> configureInstance(Statement configureStatement);

    public boolean addChangeListener(AdapterEventListener newListener);
}
