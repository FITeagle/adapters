package org.fiteagle.abstractAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class AdapterRDFHandler {
  
  private static Logger LOGGER = Logger.getLogger(AdapterRDFHandler.class.toString());
  
  private AbstractAdapter adapter;
  
  private static HashMap<AbstractAdapter, AdapterRDFHandler> instances = new HashMap<AbstractAdapter, AdapterRDFHandler>();
  
  public static synchronized AdapterRDFHandler getInstance(AbstractAdapter adapter) {
    if (instances.get(adapter) == null) {
      instances.put(adapter, new AdapterRDFHandler(adapter));
    }
    return instances.get(adapter);
  }
  
  private AdapterRDFHandler(AbstractAdapter adapter){
    this.adapter = adapter;
  }
  
  private StmtIterator getResourceInstanceIterator(Model model) {
    return model.listStatements(new SimpleSelector(null, RDF.type, adapter.getAdapterManagedResource()));
  }
  
  public String parseCreateModel(Model modelCreate, String requestID) {
    
    Model createdInstancesModel = ModelFactory.createDefaultModel();
    adapter.setModelPrefixes(createdInstancesModel);
    
    StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelCreate);
    
    AdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for resources to create...");
    
    Statement currentResourceInstanceStatement = null;
    while (iteratorResourceInstance.hasNext()) {
      currentResourceInstanceStatement = iteratorResourceInstance.nextStatement();
      
      String instanceName = currentResourceInstanceStatement.getSubject().getLocalName();
      
      if (adapter.createInstance(instanceName)) {
        LOGGER.log(Level.INFO, "Created instance: " + instanceName + " (" + currentResourceInstanceStatement.toString() + ")");
        // Configure additional parameters directly after creation
        adapter.configureInstance(currentResourceInstanceStatement);
        Model createdInstanceValues = createInformRDF(instanceName);
        createdInstancesModel.add(createdInstanceValues);
      }
      else{
        return Response.Status.CONFLICT.name();
      }
    }

    if (createdInstancesModel.isEmpty()) {
      return Response.Status.BAD_REQUEST.name();
    }
    
    adapter.notifyListeners(createdInstancesModel, requestID);
    
    return Response.Status.OK.name();
  }
  
  public String parseReleaseModel(Model modelRelease, String requestID) {
    StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelRelease);
    
    AdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for resources to release...");
    
    Statement currentResourceInstanceStatement = null;
    while (iteratorResourceInstance.hasNext()) {
      currentResourceInstanceStatement = iteratorResourceInstance.nextStatement();
      
      String instanceName = currentResourceInstanceStatement.getSubject().getLocalName();
      AdapterRDFHandler.LOGGER.log(Level.INFO, "Releasing instance: " + instanceName + " ("
          + currentResourceInstanceStatement.toString() + ")");
      
      if (adapter.terminateInstance(instanceName)) {
        adapter.notifyListeners(createInformReleaseRDF(instanceName), requestID);
        return Response.Status.OK.name();
      }
    }
    return Response.Status.NOT_FOUND.name();
  }
  
  public String parseDiscoverModel(Model modelDiscover) {
    StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelDiscover);
    
    Statement currentInstanceStatement = null;
    while (iteratorResourceInstance.hasNext()) {
      currentInstanceStatement = iteratorResourceInstance.nextStatement();
      
      String instanceName = currentInstanceStatement.getSubject().getLocalName();
      AdapterRDFHandler.LOGGER.log(Level.INFO, "Discovering instance: " + instanceName + " ("
          + currentInstanceStatement.toString() + ")");
      
      String response = adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT);
      if (response.isEmpty()) {
        return Response.Status.NOT_FOUND.name();
      } else {
        return response;
      }
    }
    // No specific instance requested, show all
    return adapter.getDiscoverAll(IMessageBus.SERIALIZATION_DEFAULT);
  }
  
  public String parseConfigureModel(Model modelConfigure, String requestID) {
    
    Model changedInstancesModel = ModelFactory.createDefaultModel();
    adapter.setModelPrefixes(changedInstancesModel);
    
    StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelConfigure);
    
    AdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for resources to configure...");
    
    Statement currentConfigureStatement = null;
    while (iteratorResourceInstance.hasNext()) {
      currentConfigureStatement = iteratorResourceInstance.nextStatement();
      
      String instanceName = currentConfigureStatement.getSubject().getLocalName();
      
      AdapterRDFHandler.LOGGER.log(Level.INFO, "Configuring instance: "
          + currentConfigureStatement.getSubject().getLocalName() + " (" + currentConfigureStatement.toString() + ")");
      
      List<String> updatedProperties = adapter.configureInstance(currentConfigureStatement);
      Model changedInstanceValues = adapter.createInformConfigureRDF(instanceName, updatedProperties);
      changedInstancesModel.add(changedInstanceValues);
    }
    
    if (changedInstancesModel.isEmpty()) {
      return Response.Status.NOT_FOUND.name();
    }
    
    adapter.notifyListeners(changedInstancesModel, requestID);
    
    return Response.Status.OK.name();
  }
  
  public Model createInformRDF(String instanceName) {
    return adapter.getSingleInstanceModel(instanceName);
  }
  
  public Model createInformReleaseRDF(String instanceName) {
    
    Model modelInstances = ModelFactory.createDefaultModel();
    
    adapter.setModelPrefixes(modelInstances);
    
    Resource releaseInstance = modelInstances.createResource(adapter.getAdapterInstancePrefix()[1] + instanceName);
    modelInstances.add(adapter.getAdapterInstance(), MessageBusOntologyModel.methodReleases, releaseInstance);
    
    return modelInstances;
  }
  
}
