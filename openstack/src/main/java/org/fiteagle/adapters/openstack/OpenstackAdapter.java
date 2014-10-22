package org.fiteagle.adapters.openstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.Servers;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModels;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapter extends AbstractAdapter {

  private static final String[] ADAPTER_SPECIFIC_PREFIX = new String[2];
  private static final String[] ADAPTER_MANAGED_RESOURCE_PREFIX = new String[2];
  private final String[] ADAPTER_INSTANCE_PREFIX = new String[2];
  
  
  private OpenstackClient openstackClient;
  
  private static Resource adapter;
  private static Resource resource;
  private static List<Property> resourceInstanceProperties = new ArrayList<Property>();
  
  private Model adapterModel;
  private Resource adapterInstance;
  private List<Model> resourceInstances = new ArrayList<Model>();
  
  public static HashMap<String,OpenstackAdapter> openstackAdapterInstances = new HashMap<>();
  
  public static OpenstackAdapter getInstance(String URI){
    return openstackAdapterInstances.get(URI);
  }
  
  static {
    Model adapterModel = OntologyModels.loadModel("ontologies/openstack.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    StmtIterator adapterIterator = adapterModel.listStatements(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next().getSubject();
      ADAPTER_SPECIFIC_PREFIX[1] = adapter.getNameSpace();
      ADAPTER_SPECIFIC_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_SPECIFIC_PREFIX[1]);
    }
    
    StmtIterator resourceIterator = adapterModel.listStatements(adapter, MessageBusOntologyModel.propertyFiteagleImplements, (Resource) null);
    if (resourceIterator.hasNext()) {
      resource = resourceIterator.next().getObject().asResource();
      ADAPTER_MANAGED_RESOURCE_PREFIX[1] = resource.getNameSpace();
      ADAPTER_MANAGED_RESOURCE_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_MANAGED_RESOURCE_PREFIX[1]);
    }
    
    StmtIterator propertiesIterator = adapterModel.listStatements(null, RDFS.domain, resource);
    while (propertiesIterator.hasNext()) {
      Property p = adapterModel.getProperty(propertiesIterator.next().getSubject().getURI());
      resourceInstanceProperties.add(p);
    }
    
    StmtIterator adapterInstanceIterator = adapterModel.listStatements(null, RDF.type, adapter);
    while (adapterInstanceIterator.hasNext()) {
      Resource adapterInstance = adapterInstanceIterator.next().getSubject();
      
      OpenstackAdapter openstackAdapter = new OpenstackAdapter(adapterInstance, adapterModel);
      openstackAdapter.ADAPTER_INSTANCE_PREFIX[1] = adapterInstance.getNameSpace();
      openstackAdapter.ADAPTER_INSTANCE_PREFIX[0] = adapterModel.getNsURIPrefix(openstackAdapter.ADAPTER_INSTANCE_PREFIX[1]);
      
      openstackAdapterInstances.put(adapterInstance.getURI(), openstackAdapter);
    }
  }
  
  
  private OpenstackAdapter(Resource adapterInstance, Model adapterModel){
    openstackClient = OpenstackClient.getInstance();
	  
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
  }
  
  @Override
  public Object handleCreateInstance(String instanceName) {
    String imageId_ubuntu = "7bef2175-b4cd-4302-be23-dbeb35b41702";
    String flavorId_small = "2"; 
    String keyPairName = "mitja_tub";
    Server newServer = openstackClient.createServer(imageId_ubuntu, flavorId_small, instanceName, keyPairName);
    return newServer;
  }

  @Override
  public void handleTerminateInstance(String instanceName) {
    Server serverToDelete = (Server) instanceList.get(instanceName);
    openstackClient.deleteServer(serverToDelete.getId());
  }
  
  @Override
  public String[] getAdapterSpecificPrefix() {
    return ADAPTER_SPECIFIC_PREFIX.clone();
  }
  
  @Override
  public String[] getAdapterManagedResourcePrefix() {
    return ADAPTER_MANAGED_RESOURCE_PREFIX.clone();
  }
  
  @Override
  public String[] getAdapterInstancePrefix() {
    return ADAPTER_INSTANCE_PREFIX.clone();
  }

  @Override
  public Model handleMonitorInstance(String instanceName, Model modelInstances) {
    Server currentServer = (Server) instanceList.get(instanceName);

    Resource serverInstance = modelInstances.createResource(ADAPTER_INSTANCE_PREFIX[1] + instanceName);
    addPropertiesToResource(serverInstance, currentServer, instanceName);

    return modelInstances;
  }

  @Override
  public Model handleGetAllInstances(Model modelInstances) {
    for(String key : instanceList.keySet()) {

      Server server = (Server) instanceList.get(key);

      Resource openstackInstance = modelInstances.createResource(ADAPTER_INSTANCE_PREFIX[1] + key);
      addPropertiesToResource(openstackInstance, server, key);
    }
    return modelInstances;
  }
  
  @Override
  public void updateInstanceList(){
    instanceList.clear();
    Servers servers = openstackClient.listServers();
    for(Server server : servers.getList()){
      instanceList.put(server.getName(), server);
      
      Model createdResourceInstanceModel = getSingleInstanceModel(server.getName());
      resourceInstances.add(createdResourceInstanceModel);
      adapterModel.add(createdResourceInstanceModel);
    }
  }
  
  private void addPropertiesToResource(Resource openstackInstance, Server server, String instanceName) {
    openstackInstance.addProperty(RDF.type, resource);
    openstackInstance.addProperty(RDFS.label, "OpenstackVM: " + instanceName);
    openstackInstance.addProperty(RDFS.comment, adapterModel.createLiteral("Openstack Virtual Machine " + instanceName));
    
    for(Property p : resourceInstanceProperties){
      switch(p.getLocalName()){
        case "id": 
          openstackInstance.addLiteral(p, server.getId());
          break;
      }
    }
   
  }

  @Override
  public List<String> configureInstance(Statement configureStatement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Resource getAdapterManagedResource() {
    return resource;
  }

  @Override
  public Resource getAdapterInstance() {
    return adapterInstance;
  }

  @Override
  public Resource getAdapterType() {
    return adapter;
  }

  @Override
  public Model getAdapterDescriptionModel() {
    return adapterModel;
  }

}
