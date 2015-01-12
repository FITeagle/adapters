package org.fiteagle.abstractAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
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
      if(!containsInstance(instanceURI)) {
        Model createdInstance = handleCreateInstance(instanceURI, model);
        LOGGER.log(Level.INFO, "Created instance: " + instanceURI);
        createdInstancesModel.add(createdInstance);
      }
      else{
        LOGGER.log(Level.INFO, "Instance: " + instanceURI+" already exists");
      }
    }
    if (createdInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any new instances to create");
      throw new AdapterException(Response.Status.CONFLICT.name());
    }
    updateAdapterModel(createdInstancesModel);
    return createdInstancesModel;
  }
  
  public void deleteInstances(Model model) {
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    while (resourceInstanceIterator.hasNext()) {
      String instanceURI = resourceInstanceIterator.next().getSubject().getURI();
      deleteInstance(instanceURI);     
    }
  }
  
  public void deleteInstance(String instanceURI){
    LOGGER.log(Level.INFO, "Deleting instance: " + instanceURI);
    handleDeleteInstance(instanceURI);
    getAdapterDescriptionModel().remove(getInstanceModel(instanceURI));
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
      Model updatedModel = handleConfigureInstance(resourceInstance.getURI(), configureModel);
      configuredInstancesModel.add(updatedModel);
    }
    if (configuredInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to configure");
      throw new AdapterException(Response.Status.NOT_FOUND.name());
    }
    updateAdapterModel(configuredInstancesModel);
    return configuredInstancesModel;
  }
  
  private void updateAdapterModel(Model updatedModel){
    StmtIterator iter = updatedModel.listStatements();
    while(iter.hasNext()){
      Statement configureStatement = iter.next();
      getAdapterDescriptionModel().removeAll(configureStatement.getSubject(), configureStatement.getPredicate(), null);
      getAdapterDescriptionModel().add(configureStatement);
    }
  }
  
  private StmtIterator getResourceInstanceIterator(Model model) {
    return model.listStatements(null, RDF.type, getAdapterManagedResource());
  }
  
  public Model getInstanceModel(String instanceURI) {
    if (containsInstance(instanceURI)) {
      Model resourceModel = ModelFactory.createDefaultModel();
      Resource resource = getAdapterDescriptionModel().getResource(instanceURI);
      StmtIterator iter = resource.listProperties();
      while(iter.hasNext()){
        resourceModel.add(iter.next());
      }
      return resourceModel;
    }
    return null;
  }
  
  private boolean containsInstance(String instanceURI){
    Resource instance = getAdapterDescriptionModel().getResource(instanceURI);
    if(instance != null && getAdapterDescriptionModel().contains(instance, RDF.type, getAdapterManagedResource())){
      return true;
    }
    return false;
  }
  
  public Model getAllInstancesModel() {
    Model modelInstances = ModelFactory.createDefaultModel();
    MessageUtil.setCommonPrefixes(modelInstances);
    StmtIterator resourceIterator = getAdapterDescriptionModel().listStatements(null, RDF.type, getAdapterManagedResource());
    while(resourceIterator.hasNext()){
      modelInstances.add(getInstanceModel(resourceIterator.next().getSubject().getLocalName()));      
    }
    return modelInstances;
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
  
  protected abstract Model handleConfigureInstance(String instanceURI, Model configureModel);
  
  protected abstract Model handleCreateInstance(String instanceURI, Model newInstanceModel);
  
  protected abstract void handleDeleteInstance(String instanceURI);
  
  public static class AdapterException extends Exception {
    
    private static final long serialVersionUID = -1664977530188161479L;
    
    public AdapterException(String message) {
      super(message);
    }
  }
  
}
