package org.fiteagle.abstractAdapter.dm;

import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;

import com.hp.hpl.jena.rdf.model.Statement;

public abstract class AbstractAdapterEJB implements IAbstractAdapterEJB {

    protected AbstractAdapter adapter;

    @Override
    public String getAdapterDescription(String serializationFormat) {
        return this.adapter.getAdapterDescription(serializationFormat);
    }

    @Override
    public boolean createInstance(String instanceName) {
        return this.adapter.createInstance(instanceName);
    }

    @Override
    public boolean terminateInstance(String instanceName) {
        return this.adapter.terminateInstance(instanceName);
    }

    @Override
    public String monitorInstance(String instanceName, String serializationFormat) {
        return this.adapter.monitorInstance(instanceName, serializationFormat);
    }

    @Override
    public String getAllInstances(String serializationFormat) {
        return this.adapter.getAllInstances(serializationFormat);
    }

    @Override
    public List<String> configureInstance(Statement configureStatement) {
        return this.adapter.configureInstance(configureStatement);
    }

    @Override
    public boolean addChangeListener(AdapterEventListener newListener) {
        return this.adapter.addChangeListener(newListener);
    }
}
