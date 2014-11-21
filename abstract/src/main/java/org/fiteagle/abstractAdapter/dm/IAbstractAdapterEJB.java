package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AdapterEventListener;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public interface IAbstractAdapterEJB {
     String getAdapterDescription(String serializationFormat);

     boolean createInstance(String instanceName);

     boolean terminateInstance(String instanceName);

     String monitorInstance(String instanceName, String serializationFormat);

     String getAllInstances(String serializationFormat);

     Model configureInstance(Statement configureStatement);

     boolean addChangeListener(AdapterEventListener newListener);
}
