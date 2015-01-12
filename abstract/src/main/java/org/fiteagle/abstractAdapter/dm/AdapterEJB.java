package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;

import com.hp.hpl.jena.rdf.model.Model;

public interface AdapterEJB {
  
  Model createInstance(Model createModel) throws AdapterException;
  
  void deleteInstances(Model deleteModel);
  
  Model configureInstances(Model configureModel) throws AdapterException;
  
}
