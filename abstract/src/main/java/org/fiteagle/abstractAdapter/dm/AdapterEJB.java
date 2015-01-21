package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;

import com.hp.hpl.jena.rdf.model.Model;

public interface AdapterEJB {
  
  Model createInstance(Model createModel) throws AdapterException;
  
  Model deleteInstances(Model deleteModel) throws AdapterException;
  
  Model configureInstances(Model configureModel) throws AdapterException;
  
}
