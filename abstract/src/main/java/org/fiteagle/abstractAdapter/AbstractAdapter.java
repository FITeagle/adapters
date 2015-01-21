package org.fiteagle.abstractAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapter {
  
  private final Logger LOGGER = Logger.getLogger(this.getClass().toString());
  
  private List<AdapterEventListener> listeners = new ArrayList<AdapterEventListener>();
  
  public boolean isRecipient(Model messageModel) {
    return messageModel.containsResource(getAdapterInstance());
  }
  
  public Model createInstances(Model model) throws AdapterException {
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    if(!resourceInstanceIterator.hasNext()){
      LOGGER.log(Level.INFO, "Could not find any instances to create");
      throw new AdapterException(Response.Status.BAD_REQUEST.name());
    }
    
    Model createdInstancesModel = ModelFactory.createDefaultModel();    
    while (resourceInstanceIterator.hasNext()) {
      String instanceURI = resourceInstanceIterator.next().getSubject().getURI();
      LOGGER.log(Level.INFO, "Creating instance: " + instanceURI);
      Model createdInstance = createInstance(instanceURI, model);
      createdInstancesModel.add(createdInstance);
    }
    if (createdInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any new instances to create");
      throw new AdapterException(Response.Status.CONFLICT.name());
    }
    return createdInstancesModel;
  }
  
  public Model deleteInstances(Model model) throws AdapterException {
	Model deletedInstancesModel = ModelFactory.createDefaultModel();    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    while (resourceInstanceIterator.hasNext()) {
      String instanceURI = resourceInstanceIterator.next().getSubject().getURI();
      LOGGER.log(Level.INFO, "Deleting instance: " + instanceURI);
      try {
        deleteInstance(instanceURI);
      } catch (InstanceNotFoundException e) {
        LOGGER.log(Level.INFO, "Instance: " + instanceURI + " not found");
        throw new AdapterException(Response.Status.NOT_FOUND.name());
      }    
      Resource deletedInstance = deletedInstancesModel.createResource(instanceURI);
      deletedInstance.addProperty(MessageBusOntologyModel.hasState, "deleted");
    }
    return deletedInstancesModel;
  }
  
  public Model configureInstances(Model model) throws AdapterException {
    Model configuredInstancesModel = ModelFactory.createDefaultModel();    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceInstance = resourceInstanceIterator.next().getSubject();
      LOGGER.log(Level.INFO, "Configuring instance: " + resourceInstance);
      
      StmtIterator propertiesIterator = model.listStatements(resourceInstance, null, (RDFNode) null);
      Model configureModel = ModelFactory.createDefaultModel();
      while (propertiesIterator.hasNext()) {
        configureModel.add(propertiesIterator.next());
      }
      Model updatedModel = configureInstance(resourceInstance.getURI(), configureModel);
      configuredInstancesModel.add(updatedModel);
    }
    if (configuredInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to configure");
      throw new AdapterException(Response.Status.NOT_FOUND.name());
    }
    return configuredInstancesModel;
  }
  
  protected StmtIterator getResourceInstanceIterator(Model model) {
    return model.listStatements(null, RDF.type, getAdapterManagedResource());
  }
  
  public void notifyListeners(Model eventRDF, String requestID, String methodType, String methodTarget) {
    for (AdapterEventListener listener : listeners) {
      listener.publishModelUpdate(eventRDF, requestID, methodType, methodTarget);
    }
  }
  
  public void addListener(AdapterEventListener newListener) {
    listeners.add(newListener);
  }
  
  public abstract Resource getAdapterManagedResource();
  
  public abstract Resource getAdapterInstance();
  
  public abstract Resource getAdapterType();
  
  public abstract Model getAdapterDescriptionModel();
  
  public abstract void updateAdapterDescription();
  
  public abstract Model configureInstance(String instanceURI, Model configureModel);
  
  public abstract Model createInstance(String instanceURI, Model newInstanceModel);
  
  public abstract void deleteInstance(String instanceURI) throws InstanceNotFoundException;
  
  public abstract Model getInstance(String instanceURI) throws InstanceNotFoundException;
  
  public abstract Model getAllInstances() throws InstanceNotFoundException;
  
  public static class AdapterException extends Exception {
    
    private static final long serialVersionUID = -1664977530188161479L;
    
    public AdapterException(String message) {
      super(message);
    }
  }
  
  public static class InstanceNotFoundException extends Exception {

    private static final long serialVersionUID = 2310151290668732710L;

    public InstanceNotFoundException(){
      super();
    }
    
    public InstanceNotFoundException(String message) {
      super(message);
    }
  }
  
}
