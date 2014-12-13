package org.fiteagle.abstractAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
  
  public String getAdapterDescription(String serializationFormat) {
    return MessageUtil.serializeModel(getAdapterDescriptionModel());
  }
  
  public boolean createInstance(String instanceName, Model model) {
    if(containsResourceInstance(instanceName)) {
      return false;
    }
    
    Resource createdInstanceModel = handleCreateInstance(instanceName, model);
    getAdapterDescriptionModel().add(createdInstanceModel.getModel());
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
    return MessageUtil.serializeModel(modelInstances);
  }
  
  public Model configureInstance(Statement configureStatement){
    handleConfigureInstance(configureStatement);
    
    getAdapterDescriptionModel().removeAll(configureStatement.getSubject(), configureStatement.getPredicate(), null);
    getAdapterDescriptionModel().add(configureStatement);
    return getSingleInstanceModel(configureStatement.getSubject().getLocalName());
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
    // TODO: serializationFormat
    return MessageUtil.serializeModel(getAllInstancesModel());
  }
  
  public int getAmountOfInstances(){
    StmtIterator iter = getAllInstancesModel().listStatements(null, RDF.type, getAdapterManagedResource());
    int amountOfInstances = 0;
    while(iter.hasNext()){
      amountOfInstances++;
      iter.next();
    }
    return amountOfInstances;
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
  
  public String getDiscoverAll(String serializationFormat) {
    //TODO: serializationFormat
    return MessageUtil.serializeModel(getAdapterDescriptionModel());
  }
  
  public void notifyListeners(Model eventRDF, String requestID) {
    for (AdapterEventListener listener : listeners) {
      listener.publishModelUpdate(eventRDF, requestID);
    }
  }
  
  public boolean addChangeListener(AdapterEventListener newListener) {
    listeners.add(newListener);
    return true;
  }
  
  public void registerAdapter() {
    updateAdapterDescription();
    notifyListeners(getAdapterDescriptionModel(), null);
  }
  
  public void restoreResourceInstances() {
    notifyListeners(getAdapterDescriptionModel(), null);
  }
  
  public void deregisterAdapter() {
    Model messageModel = ModelFactory.createDefaultModel();
    messageModel.add(MessageBusOntologyModel.internalMessage, MessageBusOntologyModel.methodReleases, getAdapterInstance());
    
    notifyListeners(messageModel, null);
  }
  
  public abstract void handleConfigureInstance(Statement configureStatement);
  
  public abstract Resource handleCreateInstance(String instanceName, Model newInstanceModel);
  
  public abstract void handleTerminateInstance(String instanceName);
  
  public abstract String[] getAdapterSpecificPrefix();
  
  public abstract String[] getAdapterManagedResourcePrefix();
  
  public abstract String[] getAdapterInstancePrefix();
  
  public static class InsufficentPropertiesException extends RuntimeException {
    
    private static final long serialVersionUID = 2485932734534584797L;

    public InsufficentPropertiesException(String missingPropertyName) {
      super("no property called "+missingPropertyName+" could be found or it was empty");
    }
  }
  
}
