package org.fiteagle.abstractAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Abstract class defining the basics all the current adapters are following Extend this class and implement the
 * abstract methods to get this to work
 */
public abstract class AbstractAdapter {
  
  private List<AdapterEventListener> listener = new ArrayList<AdapterEventListener>();
  protected HashMap<String, Object> instanceList = new HashMap<String, Object>();
  
  public abstract Resource getAdapterManagedResource();
  
  public abstract Resource getAdapterInstance();
  
  public abstract Resource getAdapterType();
  
  public abstract Model getAdapterDescriptionModel();
  
  public abstract void updateAdapterDescription();
  
  public String getAdapterDescription(String serializationFormat) {
    return MessageBusMsgFactory.serializeModel(getAdapterDescriptionModel());
  }
  
  public boolean createInstance(String instanceName, Model model) {
    if(instanceList.containsKey(instanceName)) {
      return false;
    }
    
    Map<String, String> properties = AdapterRDFHandler.getPropertyMapForResource(getAdapterInstancePrefix()[1]+instanceName, model);
    
    Object newInstance = handleCreateInstance(instanceName, properties);
    
    instanceList.put(instanceName, newInstance);
    return true;
  }
  
  public boolean createInstance(String instanceName, Map<String, String> properties) {
    if(instanceList.containsKey(instanceName)) {
      return false;
    }
    
    Object newInstance = handleCreateInstance(instanceName, properties);
    
    instanceList.put(instanceName, newInstance);
    return true;
  }
  
  protected static String getProperty(String propertyKey, Map<String, String> properties){
    String value = properties.get(propertyKey);
    if(value == null || value.isEmpty()){
      throw new InsufficentPropertiesException(propertyKey);
    }
    return properties.get(propertyKey);
  }
  
  public boolean terminateInstance(String instanceName) {
    if (instanceList.containsKey(instanceName)) {
      handleTerminateInstance(instanceName);
      instanceList.remove(instanceName);
      return true;
    }
    
    return false;
  }
  
  public String monitorInstance(String instanceName, String serializationFormat) {
    Model modelInstances = getSingleInstanceModel(instanceName);
    if (modelInstances.isEmpty()) {
      return "";
    }
    return MessageBusMsgFactory.serializeModel(modelInstances);
  }
  
  public Model getSingleInstanceModel(String instanceName) {
    Model modelOfInstance = ModelFactory.createDefaultModel();
    
    if (instanceList.containsKey(instanceName)) {
      modelOfInstance = handleMonitorInstance(instanceName, modelOfInstance);
    }
    
    return modelOfInstance;
  }
  
  public String getAllInstances(String serializationFormat) {
    // TODO: serializationFormat
    return MessageBusMsgFactory.serializeModel(getAllInstancesModel());
  }
  
  public Model getAllInstancesModel() {
    Model modelInstances = ModelFactory.createDefaultModel();
    setModelPrefixes(modelInstances);
    modelInstances = handleGetAllInstances(modelInstances);
    
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
    Model modelDiscover = getAdapterDescriptionModel();
    modelDiscover.add(getAllInstancesModel());
    
    return MessageBusMsgFactory.serializeModel(modelDiscover);
  }
  
  public void notifyListeners(Model eventRDF, String requestID) {
    for (AdapterEventListener name : listener) {
      name.rdfChange(eventRDF, requestID);
    }
  }
  
  public boolean addChangeListener(AdapterEventListener newListener) {
    listener.add(newListener);
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
  
  public Model createInformConfigureRDF(String instanceName, List<String> propertiesChanged) {
    Model modelPropertiesChanged = ModelFactory.createDefaultModel();
    setModelPrefixes(modelPropertiesChanged);
    
    Model wholeInstance = getSingleInstanceModel(instanceName);
    Resource currentInstance = wholeInstance.getResource(getAdapterInstancePrefix()[1]+instanceName);
    
    for (String currentPropertyString : propertiesChanged) {
      Property currentProperty = wholeInstance.getProperty(getAdapterSpecificPrefix()[1] + currentPropertyString);
      StmtIterator iter2 = currentInstance.listProperties(currentProperty);
      Statement stmtToAdd = iter2.nextStatement();
      modelPropertiesChanged.add(stmtToAdd);
    }
    
    return modelPropertiesChanged;
  }
  
  public abstract List<String> configureInstance(Statement configureStatement);
  
  public abstract Object handleCreateInstance(String instanceName, Map<String, String> properties);
  
  public abstract void handleTerminateInstance(String instanceName);
  
  public abstract String[] getAdapterSpecificPrefix();
  
  public abstract String[] getAdapterManagedResourcePrefix();
  
  public abstract String[] getAdapterInstancePrefix();
  
  public abstract Model handleMonitorInstance(String instanceName, Model modelInstances);
  
  public abstract Model handleGetAllInstances(Model modelInstances);
  
  public static class InsufficentPropertiesException extends RuntimeException {
    
    private static final long serialVersionUID = 2485932734534584797L;

    public InsufficentPropertiesException(String missingPropertyName) {
      super("no property called "+missingPropertyName+" could be found or it was empty");
    }
  }
  
}
