package org.fiteagle.adapters.stopwatch.dm;

import java.beans.PropertyChangeListener;
import java.io.InputStream;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.adapters.stopwatch.IStopwatchAdapter;
import org.fiteagle.adapters.stopwatch.StopwatchAdapter;

@Singleton(name = "StopwatchAdapter")
@Remote(IStopwatchAdapter.class)
public class StopwatchAdapterEJB implements IStopwatchAdapter {
    private final StopwatchAdapter adapter;

    public StopwatchAdapterEJB() {
        this.adapter = StopwatchAdapter.getInstance();
    }

    // @Override
    // public void registerForEvents(IAdapterListener adapterDM) {
    // this.adapter.registerForEvents(adapterDM);
    // }

    @Override
    public String getAdapterDescription(String serializationFormat) {
        return this.adapter.getAdapterDescription(serializationFormat);
    }

    @Override
    public boolean createInstance(int instanceID) {
        return this.adapter.createInstance(instanceID);
    }

    @Override
    public boolean terminateInstance(int instanceID) {
        return this.adapter.terminateInstance(instanceID);
    }

    @Override
    public String monitorInstance(int instanceID, String serializationFormat) {
        return this.adapter.monitorInstance(instanceID, serializationFormat);
    }

    @Override
    public String getAllInstances(String serializationFormat) {
        return this.adapter.getAllInstances(serializationFormat);
    }

    @Override
    public String controlInstance(InputStream in, String serializationFormat) {
        return this.adapter.controlInstance(in, serializationFormat);
    }
    
    @Override
    public boolean addChangeListener(PropertyChangeListener newListener){
        return this.adapter.addChangeListener(newListener);
    }

}
