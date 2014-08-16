package org.fiteagle.adapters.motor.dm;

import com.hp.hpl.jena.rdf.model.Model;

public interface IMotorAdapterMDBSender {
    
    public void registerAdapter();
    
    public void unregisterAdapter();

    public void sendInformMessage(Model eventRDF);

}
