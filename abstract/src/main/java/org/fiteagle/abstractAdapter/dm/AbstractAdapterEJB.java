package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public abstract class AbstractAdapterEJB implements IAbstractAdapterEJB {

	// Subclasses need to set this to the appropriate Adapter to work!
    protected AbstractAdapter adapter = null;

    @Override
    public String getAdapterDescription(String serializationFormat) {
        return this.adapter.getAdapterDescription(serializationFormat);
    }

    @Override
    public boolean createInstance(String instanceName) {
      Model modelCreate = ModelFactory.createDefaultModel();
      return this.adapter.createInstance(instanceName, modelCreate);
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
    public Model configureInstance(Statement configureStatement) {
        return this.adapter.configureInstance(configureStatement);
    }

    @Override
    public boolean addChangeListener(AdapterEventListener newListener) {
        return this.adapter.addChangeListener(newListener);
    }
}
