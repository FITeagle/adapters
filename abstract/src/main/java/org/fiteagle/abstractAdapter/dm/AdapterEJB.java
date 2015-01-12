package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;

import com.hp.hpl.jena.rdf.model.Model;

public interface AdapterEJB {
  
     String getAdapterDescription(String serializationFormat);

     Model createInstance(String instanceURI) throws AdapterException;

     void terminateInstance(String instanceURI);

     Model monitorInstance(String instanceURI);

     String getAllInstances(String serializationFormat);

     Model configureInstances(Model configureModel) throws AdapterException;

     void addChangeListener(AdapterEventListener newListener);
}
