package org.fiteagle.adapters.mightyrobot.dm;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.abstractAdapter.IAbstractAdapter;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Singleton(name = "MightyRobotAdapter")
@Remote(IAbstractAdapter.class)
public class MightyRobotAdapterEJB implements IAbstractAdapter {
    private final MightyRobotAdapter adapter;

    public MightyRobotAdapterEJB() {
        this.adapter = MightyRobotAdapter.getInstance();
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
    public String controlInstance(String in, String serializationFormat) {
        return this.adapter.controlInstance(in, serializationFormat);
    }
    
    @Override
    public boolean addChangeListener(AdapterEventListener newListener){
        return this.adapter.addChangeListener(newListener);
    }

}
