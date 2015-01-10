package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class AbstractAdapterEJB implements IAbstractAdapterEJB {

    protected abstract AbstractAdapter getAdapter();

    @Override
    public String getAdapterDescription(String serializationFormat) {
        return getAdapter().getAdapterDescription(serializationFormat);
    }

    @Override
    public boolean createInstance(String instanceName) {
      Model modelCreate = ModelFactory.createDefaultModel();
      return getAdapter().createInstance(instanceName, modelCreate);
    }

    @Override
    public boolean terminateInstance(String instanceName) {
        return getAdapter().terminateInstance(instanceName);
    }

    @Override
    public String monitorInstance(String instanceName, String serializationFormat) {
        return getAdapter().monitorInstance(instanceName, serializationFormat);
    }

    @Override
    public String getAllInstances(String serializationFormat) {
        return getAdapter().getAllInstances(serializationFormat);
    }

    @Override
    public Model configureInstance(String instanceName, Model configureModel) {
        return getAdapter().configureInstance(instanceName, configureModel);
    }

    @Override
    public void addChangeListener(AdapterEventListener newListener) {
        getAdapter().addListener(newListener);
    }
}
