package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class AbstractAdapterEJB implements AdapterEJB {

    protected abstract AbstractAdapter getAdapter();

    @Override
    public String getAdapterDescription(String serializationFormat) {
        return MessageUtil.serializeModel(getAdapter().getAdapterDescriptionModel(), serializationFormat);
    }

    @Override
    public Model createInstance(String instanceURI) throws AdapterException {
      Model modelCreate = ModelFactory.createDefaultModel();
      return getAdapter().createInstances(modelCreate);
    }

    @Override
    public void terminateInstance(String instanceURI) {
        getAdapter().deleteInstance(instanceURI);
    }

    @Override
    public Model monitorInstance(String instanceURI) {
        return getAdapter().getInstanceModel(instanceURI);
    }

    @Override
    public String getAllInstances(String serialization) {
        return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), serialization);
    }

    @Override
    public Model configureInstances(Model configureModel) throws AdapterException {
        return getAdapter().configureInstances(configureModel);
    }

    @Override
    public void addChangeListener(AdapterEventListener newListener) {
        getAdapter().addListener(newListener);
    }
}
