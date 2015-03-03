package org.fiteagle.abstractAdapter.dm;

import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class AbstractAdapterEJB implements AdapterEJB {
  
  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
  
  @Override
  public Model createInstance(Model createModel) throws ProcessingException, InvalidRequestException {
    Model resultModel = ModelFactory.createDefaultModel();
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      if(adapter.isRecipient(createModel)){
        resultModel.add(adapter.createInstances(createModel));
      }
    }
    return resultModel;
  }
  
  @Override
  public Model deleteInstances(Model deleteModel) throws InvalidRequestException, ProcessingException  {
	  Model resultModel = ModelFactory.createDefaultModel();
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      if(adapter.isRecipient(deleteModel)){
        resultModel.add(adapter.deleteInstances(deleteModel));
      }
    }
    return resultModel;
  }
  
  @Override
  public Model configureInstances(Model configureModel) throws InvalidRequestException, ProcessingException, InstanceNotFoundException  {
    Model resultModel = ModelFactory.createDefaultModel();
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      if(adapter.isRecipient(configureModel)){
        resultModel.add(adapter.updateInstances(configureModel));
      }
    }
    return resultModel;
  }
  
}
