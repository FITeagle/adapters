package org.fiteagle.abstractAdapter.dm;

import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;

import com.hp.hpl.jena.rdf.model.Model;

public interface AdapterEJB {
  
  Model createInstance(Model createModel) throws ProcessingException, InvalidRequestException;
  
  Model deleteInstances(Model deleteModel) throws ProcessingException, InvalidRequestException;
  
  Model configureInstances(Model configureModel) throws ProcessingException, InvalidRequestException;
  
}
