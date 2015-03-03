package org.fiteagle.abstractAdapter;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.api.core.Config;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapter {
  
  private final Logger LOGGER = Logger.getLogger(this.getClass().toString());
  
  private List<AdapterEventListener> listeners = new ArrayList<AdapterEventListener>();
  
  public AbstractAdapter(String adapterInstanceName){
    Config.getInstance(adapterInstanceName);
  }
  
  public boolean isRecipient(Model messageModel) {
    return messageModel.containsResource(getAdapterInstance());
  }
  
  public Model getInstances(Model model) throws ProcessingException, InvalidRequestException, InstanceNotFoundException {
    Model instancesModel = ModelFactory.createDefaultModel();
    for (Resource resource : getAdapterManagedResources()) {
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        String instanceURI = resourceInstanceIterator.next().getURI();
        try {
          Model createdInstance = getInstance(instanceURI);
          instancesModel.add(createdInstance);
        } catch (InstanceNotFoundException e) {
          LOGGER.log(Level.WARNING, "Could not find instance: " + instanceURI);
        }
      }
    }
    if(instancesModel.isEmpty()){
      throw new InstanceNotFoundException("None of the requested instances could be found");
    }
    return instancesModel;
  }
  
  public Model createInstances(Model model) throws ProcessingException, InvalidRequestException {
    Model createdInstancesModel = ModelFactory.createDefaultModel();
    for (Resource resource : getAdapterManagedResources()) {
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        String instanceURI = resourceInstanceIterator.next().getURI();
        LOGGER.log(Level.INFO, "Creating instance: " + instanceURI);
        Model createdInstance = createInstance(instanceURI, model);
        createdInstancesModel.add(createdInstance);
      }
      if (createdInstancesModel.isEmpty()) {
        LOGGER.log(Level.INFO, "Could not find any new instances to create");
        throw new ProcessingException(Response.Status.CONFLICT.name());
      }
    }
    return createdInstancesModel;
  }
  
  public Model deleteInstances(Model model) throws InvalidRequestException, ProcessingException {
    Model deletedInstancesModel = ModelFactory.createDefaultModel();
    for(Resource resource : getAdapterManagedResources()){
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        String instanceURI = resourceInstanceIterator.next().getURI();
        LOGGER.log(Level.INFO, "Deleting instance: " + instanceURI);
        try {
          deleteInstance(instanceURI);
        } catch (InstanceNotFoundException e) {
          LOGGER.log(Level.INFO, "Instance: " + instanceURI + " not found");
          throw new ProcessingException(e);
        }    
        Resource deletedInstance = deletedInstancesModel.createResource(instanceURI);
        deletedInstance.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Removing);
      }
    }
    return deletedInstancesModel;
  }
  
  public Model updateInstances(Model model) throws InvalidRequestException, ProcessingException {
    Model updatedInstancesModel = ModelFactory.createDefaultModel();  
    for(Resource resource : getAdapterManagedResources()){
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        Resource resourceInstance = resourceInstanceIterator.next();
        LOGGER.log(Level.INFO, "Configuring instance: " + resourceInstance);
        
        StmtIterator propertiesIterator = model.listStatements(resourceInstance, null, (RDFNode) null);
        Model updateModel = ModelFactory.createDefaultModel();
        while (propertiesIterator.hasNext()) {
          updateModel.add(propertiesIterator.next());
        }
        Model updatedModel = updateInstance(resourceInstance.getURI(), updateModel);
        updatedInstancesModel.add(updatedModel);
      }
      if (updatedInstancesModel.isEmpty()) {
        LOGGER.log(Level.INFO, "Could not find any instances to configure");
        throw new ProcessingException(Response.Status.NOT_FOUND.name());
      }
    }
    return updatedInstancesModel;
  }
  
  public List<Resource> getAdapterManagedResources(){
    List<Resource> managedResources = new ArrayList<>();
    StmtIterator iter = getAdapterInstance().listProperties(Omn_lifecycle.parentTo);
    while(iter.hasNext()){
      managedResources.add(iter.next().getResource());
    }
    return managedResources;
  }
  
  public void notifyListeners(Model eventRDF, String requestID, String methodType, String methodTarget) {
    for (AdapterEventListener listener : listeners) {
      listener.publishModelUpdate(eventRDF, requestID, methodType, methodTarget);
    }
  }
  
  public void addListener(AdapterEventListener newListener) {
    listeners.add(newListener);
  }
  
  public abstract Resource getAdapterInstance();
  
  public abstract Resource getAdapterType();
  
  public abstract Model getAdapterDescriptionModel();
  
  public abstract void updateAdapterDescription() throws ProcessingException;
  
  public abstract Model updateInstance(String instanceURI, Model configureModel) throws InvalidRequestException, ProcessingException;
  
  public abstract Model createInstance(String instanceURI, Model newInstanceModel) throws ProcessingException, InvalidRequestException;
  
  public abstract void deleteInstance(String instanceURI) throws InstanceNotFoundException, InvalidRequestException, ProcessingException;
  
  public abstract Model getInstance(String instanceURI) throws InstanceNotFoundException, ProcessingException, InvalidRequestException;
  
  public abstract Model getAllInstances() throws InstanceNotFoundException, ProcessingException;
  
  public static class InstanceNotFoundException extends Exception {

    private static final long serialVersionUID = 2310151290668732710L;

    public InstanceNotFoundException(String message) {
      super(message);
    }
  }
  
  public static class InvalidRequestException extends Exception {

    private static final long serialVersionUID = -217391164873287337L;

    public InvalidRequestException(String message) {
      super(message);
    }
    
    public InvalidRequestException(Throwable cause) {
      super(cause);
    }
  }
  
  public static class ProcessingException extends Exception {

    private static final long serialVersionUID = 7943720534259771304L;

    public ProcessingException(String message) {
      super(message);
    }
    
    public ProcessingException(Throwable cause) {
      super(cause);
    }
  }
  
}
