package org.fiteagle.abstractAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapter {
  
  private List<AdapterEventListener> listeners = new ArrayList<AdapterEventListener>();
  
  public abstract Resource getAdapterManagedResource();
  
  public abstract Resource getAdapterInstance();
  
  public abstract Resource getAdapterType();
  
  public abstract Model getAdapterDescriptionModel();
  
  public abstract void updateAdapterDescription();
  
  private final Logger LOGGER = Logger.getLogger(this.getClass().toString());
  
  public String getAdapterDescription(String serializationFormat) {
    return MessageUtil.serializeModel(getAdapterDescriptionModel(), serializationFormat);
  }
  
  public Model handleCreateModel(Model model, String requestID) throws AdapterException {
    Model createdInstancesModel = ModelFactory.createDefaultModel();    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    LOGGER.log(Level.INFO, "Searching for resources to create...");

    if(!resourceInstanceIterator.hasNext()){
      LOGGER.log(Level.INFO, "Could not find any instances to create");
      throw new AdapterException(Response.Status.BAD_REQUEST.name());
    }
    
    Boolean createdAtLeastOne = false;
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceToCreate = resourceInstanceIterator.next().getSubject();
      
      String instanceName = resourceToCreate.getLocalName();
      if (createInstance(instanceName, model)) {
        createdAtLeastOne = true;
        LOGGER.log(Level.INFO, "Created instance: " + resourceToCreate);
        Model createdInstanceValues = getSingleInstanceModel(instanceName);
        createdInstancesModel.add(createdInstanceValues);
      }
    }
    if (createdAtLeastOne == false) {
      LOGGER.log(Level.INFO, "Could not find any new instances to create");
      throw new AdapterException(Response.Status.CONFLICT.name());
    }
    notifyListeners(createdInstancesModel, requestID, IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);
    return createdInstancesModel;
  }
  
  public void handleDeleteModel(Model model) {
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    LOGGER.log(Level.INFO, "Searching for resources to delete...");
    
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceToRelease = resourceInstanceIterator.next().getSubject();      
      LOGGER.log(Level.INFO, "Releasing instance: " + resourceToRelease);
      terminateInstance(resourceToRelease.getLocalName());
    }
  }
  
  public Model handleConfigureModel(Model model, String requestID) throws AdapterException {
    Model configuredInstancesModel = ModelFactory.createDefaultModel();    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(model);    
    LOGGER.log(Level.INFO, "Searching for resources to configure...");
    
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceInstance = resourceInstanceIterator.next().getSubject();
      LOGGER.log(Level.INFO, "Configuring instance: " + resourceInstance);
      
      StmtIterator propertiesIterator = model.listStatements(resourceInstance, null, (RDFNode) null);
      Model configureModel = ModelFactory.createDefaultModel();
      while (propertiesIterator.hasNext()) {
        configureModel.add(propertiesIterator.next());
      }
      Model changedInstanceValues = configureInstance(resourceInstance.getLocalName(), configureModel);
      configuredInstancesModel.add(changedInstanceValues);
    }
    
    if (configuredInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to configure");
      throw new AdapterException(Response.Status.NOT_FOUND.name());
    }
    notifyListeners(configuredInstancesModel, requestID, IMessageBus.TYPE_INFORM,  IMessageBus.TARGET_ORCHESTRATOR);
    return configuredInstancesModel;
  }
  
  private StmtIterator getResourceInstanceIterator(Model model) {
    return model.listStatements(null, RDF.type, getAdapterManagedResource());
  }
  
  public boolean createInstance(String instanceName, Model model) {
    if(containsResourceInstance(instanceName)) {
      return false;
    }
    
    Resource createdInstance = handleCreateInstance(instanceName, model);
    getAdapterDescriptionModel().add(createdInstance.getModel());
    return true;
  }
  
  public static String getProperty(String propertyKey, Map<String, String> properties){
    String value = properties.get(propertyKey);
    if(value == null || value.isEmpty()){
      throw new InsufficentPropertiesException(propertyKey);
    }
    return properties.get(propertyKey);
  }
  
  public boolean terminateInstance(String instanceName) {
    if (containsResourceInstance(instanceName)) {
      handleTerminateInstance(instanceName);
      getAdapterDescriptionModel().remove(getSingleInstanceModel(instanceName));
      return true;
    }
    
    return false;
  }
  
  public String monitorInstance(String instanceName, String serializationFormat) {
    Model modelInstances = getSingleInstanceModel(instanceName);
    if (modelInstances == null || modelInstances.isEmpty()) {
      return "";
    }
    return MessageUtil.serializeModel(modelInstances, serializationFormat);
  }
  
  public Model configureInstance(String instanceName, Model configureModel){
    handleConfigureInstance(instanceName, configureModel);
    
    StmtIterator iter = configureModel.listStatements();
    while(iter.hasNext()){
      Statement configureStatement = iter.next();
      getAdapterDescriptionModel().removeAll(configureStatement.getSubject(), configureStatement.getPredicate(), null);
      getAdapterDescriptionModel().add(configureStatement);
    }
    
    return getSingleInstanceModel(instanceName);
  }
  
  public Model getSingleInstanceModel(String instanceName) {
    if (containsResourceInstance(instanceName)) {
      Model resourceModel = ModelFactory.createDefaultModel();
      Resource resource = getAdapterDescriptionModel().getResource(getAdapterInstancePrefix()[1]+instanceName);
      StmtIterator iter = resource.listProperties();
      while(iter.hasNext()){
        resourceModel.add(iter.next());
      }
      return resourceModel;
    }
    return null;
  }
  
  public boolean containsResourceInstance(String instanceName){
    Resource instance = getAdapterDescriptionModel().getResource(getAdapterInstancePrefix()[1]+instanceName);
    if(instance != null && getAdapterDescriptionModel().contains(instance, RDF.type, getAdapterManagedResource())){
      return true;
    }
    return false;
  }
  
  public String getAllInstances(String serializationFormat) {
    return MessageUtil.serializeModel(getAllInstancesModel(), serializationFormat);
  }
  
  public Model getAllInstancesModel() {
    Model modelInstances = ModelFactory.createDefaultModel();
    setModelPrefixes(modelInstances);
    StmtIterator resourceIterator = getAdapterDescriptionModel().listStatements(null, RDF.type, getAdapterManagedResource());
    while(resourceIterator.hasNext()){
      modelInstances.add(getSingleInstanceModel(resourceIterator.next().getSubject().getLocalName()));      
    }
    return modelInstances;
  }
  
  public void setModelPrefixes(Model model) {
    model.setNsPrefix(getAdapterSpecificPrefix()[0], getAdapterSpecificPrefix()[1]);
    model.setNsPrefix(getAdapterManagedResourcePrefix()[0], getAdapterManagedResourcePrefix()[1]);
    model.setNsPrefix(getAdapterInstancePrefix()[0], getAdapterInstancePrefix()[1]);
    model.setNsPrefix("omn", "http://open-multinet.info/ontology/omn#");
    model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
    model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
  }

  public void notifyListeners(Model eventRDF, String requestID, String methodType, String methodTarget) {
    for (AdapterEventListener listener : listeners) {
      listener.publishModelUpdate(eventRDF, requestID, methodType, methodTarget);
    }
  }
  
  public void addListener(AdapterEventListener newListener) {
    listeners.add(newListener);
  }
  
  public void registerAdapter() {
    updateAdapterDescription();
    notifyListeners(getAdapterDescriptionModel(), null, IMessageBus.TYPE_CREATE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
  }
  
  public void deregisterAdapter() {
    Model messageModel = ModelFactory.createDefaultModel();
    messageModel.add(getAdapterInstance(), RDF.type, getAdapterType());
    notifyListeners(messageModel, null, IMessageBus.TYPE_DELETE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
  }
  
  public abstract void handleConfigureInstance(String instanceName, Model configureModel);
  
  public abstract Resource handleCreateInstance(String instanceName, Model newInstanceModel);
  
  public abstract void handleTerminateInstance(String instanceName);
  
  public abstract String[] getAdapterSpecificPrefix();
  
  public abstract String[] getAdapterManagedResourcePrefix();
  
  public abstract String[] getAdapterInstancePrefix();
  
  public static class AdapterException extends Exception {
    
    private static final long serialVersionUID = -1664977530188161479L;
    
    public AdapterException(String message) {
      super(message);
    }
  }
  
  public static class InsufficentPropertiesException extends RuntimeException {
    
    private static final long serialVersionUID = 2485932734534584797L;

    public InsufficentPropertiesException(String missingPropertyName) {
      super("no property called "+missingPropertyName+" could be found or it was empty");
    }
  }
  
}
