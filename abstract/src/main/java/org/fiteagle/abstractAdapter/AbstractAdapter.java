package org.fiteagle.abstractAdapter;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.api.core.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

  //Defines the adapters TBox
  protected Model adapterTBox;
  //Defines the adapters ABox
  protected Resource adapterABox;
  protected String uuid;

  public AbstractAdapter(){

  }
  
  public void updateConfig(String adapterName, String configInput)throws ProcessingException, IOException{
	  	Config config = new Config(adapterName);
	    config.deletePropertiesFile();
	    
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try{
	    Properties property = gson.fromJson(configInput, Properties.class);
		config.writeProperties(property);
		refreshConfig();
		}catch(Exception e){
		LOGGER.log(Level.SEVERE, "Could not read the JSON serialized Config-File from REST-Interface");
		}
		
	  }
 

  /**
   * Creates a default properties file
   *
   * @param adapterInstanceName
   */
  public void createDefaultConfiguration(String adapterInstanceName){
    Config config = new Config(adapterInstanceName);
    config.createPropertiesFile();
  }


  /**
   *
   * Checks if the adapter is the recipent of the given message model
   * @param messageModel
   * TODO FIX!
   * @return
   */
  public boolean isRecipient(Model messageModel) {

    return messageModel.containsResource(this.adapterABox);
  }


  /**
   *
   *
   * @param model
   * @return The currently active instances of the adapter
   * @throws ProcessingException
   * @throws InvalidRequestException
   * @throws InstanceNotFoundException
   */
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
          LOGGER.log(Level.WARNING, "Could not find adapterABox: " + instanceURI);
        }
      }
    }
    if(instancesModel.isEmpty()){
      throw new InstanceNotFoundException("None of the requested instances could be found");
    }
    return instancesModel;
  }


  /**
   * Creates a new adapterABox of a resource adapter
   *
   * @param model Representation of the new adapter adapterABox
   * @return the newly created adapter adapterABox
   * @throws ProcessingException
   * @throws InvalidRequestException
   */
  public Model createInstances(Model model) throws ProcessingException, InvalidRequestException {
    Model createdInstancesModel = ModelFactory.createDefaultModel();
    for (Resource resource : getAdapterManagedResources()) {
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        String instanceURI = resourceInstanceIterator.next().getURI();
        LOGGER.log(Level.INFO, "Creating adapterABox: " + instanceURI);
        Model createdInstance = createInstance(instanceURI, model);
        createdInstancesModel.add(createdInstance);
      }
    }
      if (createdInstancesModel.isEmpty()) {
        LOGGER.log(Level.INFO, "Could not find any new instances to create");
        throw new ProcessingException(Response.Status.CONFLICT.name());
      }

    return createdInstancesModel;
  }

  /**
   * Deletes an adapter adapterABox defined by the model
   * @param model
   * @return
   * @throws InvalidRequestException
   * @throws ProcessingException
   */
  public Model deleteInstances(Model model) throws InvalidRequestException, ProcessingException {
    Model deletedInstancesModel = ModelFactory.createDefaultModel();
    for(Resource resource : getAdapterManagedResources()){
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        String instanceURI = resourceInstanceIterator.next().getURI();
        LOGGER.log(Level.INFO, "Deleting adapterABox: " + instanceURI);
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

  /**
   *
   * @param model
   * @return
   * @throws InvalidRequestException
   * @throws ProcessingException
   * @throws InstanceNotFoundException
   */
  public Model updateInstances(Model model) throws InvalidRequestException, ProcessingException, InstanceNotFoundException {
    Model updatedInstancesModel = ModelFactory.createDefaultModel();  
    for(Resource resource : getAdapterManagedResources()){
      ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, resource);
      while (resourceInstanceIterator.hasNext()) {
        Resource resourceInstance = resourceInstanceIterator.next();
        LOGGER.log(Level.INFO, "Updating adapterABox: " + resourceInstance);
        
        StmtIterator propertiesIterator = model.listStatements(resourceInstance, null, (RDFNode) null);
        Model updateModel = ModelFactory.createDefaultModel();
        while (propertiesIterator.hasNext()) {
          updateModel.add(propertiesIterator.next());
        }
        Model updatedModel = updateInstance(resourceInstance.getURI(), updateModel);
        updatedInstancesModel.add(updatedModel);
      }
      if (updatedInstancesModel.isEmpty()) {
        LOGGER.log(Level.INFO, "Could not find any instances to update");
        throw new InstanceNotFoundException("Could not find any instances to update");
      }
    }
    return updatedInstancesModel;
  }

  /**
   *
   * @return
   */
  public List<Resource> getAdapterManagedResources(){
    List<Resource> managedResources = new ArrayList<>();
    StmtIterator iter = this.adapterTBox.listStatements(null, Omn_lifecycle.canImplement, (RDFNode) null);
    while(iter.hasNext()){
      managedResources.add(iter.next().getResource());
    }
    return managedResources;
  }

  /**
   *
   * @param eventRDF
   * @param requestID
   * @param methodType
   * @param methodTarget
   */
  public void notifyListeners(Model eventRDF, String requestID, String methodType, String methodTarget) {
    for (AdapterEventListener listener : listeners) {
      listener.publishModelUpdate(eventRDF, requestID, methodType, methodTarget);
    }
  }

  /**
   *
   * @param newListener
   */
  public void addListener(AdapterEventListener newListener) {
    listeners.add(newListener);
  }



  public abstract Resource getAdapterABox();
  
  public abstract Model getAdapterDescriptionModel();
  
  public abstract void updateAdapterDescription() throws ProcessingException;
  
  public abstract Model updateInstance(String instanceURI, Model configureModel) throws InvalidRequestException, ProcessingException;
  
  public abstract Model createInstance(String instanceURI, Model newInstanceModel) throws ProcessingException, InvalidRequestException;
  
  public abstract void deleteInstance(String instanceURI) throws InstanceNotFoundException, InvalidRequestException, ProcessingException;
  
  public abstract Model getInstance(String instanceURI) throws InstanceNotFoundException, ProcessingException, InvalidRequestException;
  
  public abstract Model getAllInstances() throws InstanceNotFoundException, ProcessingException;
  
  public abstract void refreshConfig() throws ProcessingException;

  public  String getId(){
    return this.uuid;
  }

  protected void setId(String id){
    this.uuid = id;
  }

  public abstract void shutdown();

  public abstract void configure(Config configuration);



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
