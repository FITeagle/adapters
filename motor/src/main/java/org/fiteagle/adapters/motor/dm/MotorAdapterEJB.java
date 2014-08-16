package org.fiteagle.adapters.motor.dm;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton(name = "MotorAdapter")
@Remote(IMotorAdapterEJB.class)
public class MotorAdapterEJB implements IMotorAdapterEJB {
    private final MotorAdapter adapter;

    public MotorAdapterEJB() {
        this.adapter = MotorAdapter.getInstance();
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
    public String controlInstance(String controlInput, String serializationFormat) {
        return this.adapter.controlInstance(controlInput, serializationFormat);
    }
    
    @Override
    public boolean addChangeListener(AdapterEventListener newListener){
        return this.adapter.addChangeListener(newListener);
    }

}
