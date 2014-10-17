package org.fiteagle.adapters.openstack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.Servers;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OpenstackAdapter extends AbstractAdapter {

  private static final String FITEAGLE_ONTOLOGY_PREFIX = "http://fiteagle.org/ontology#";
  private static final String[] ADAPTER_SPECIFIC_PREFIX = { "openstack", "http://fiteagle.org/ontology/adapter/openstack#" };
  private static final String RESOURCE_INSTANCE_NAME = "OpenstackVM";
  private static final String ADAPTER_CLASS_NAME = "OpenstackAdapter";
  
  private OpenstackClient openstackClient;
  
  private Model adapterModel;
  private Resource adapterInstance;
  private Resource adapter;
  private Resource resource;
  private List<Model> resourceInstances = new ArrayList<Model>();
  private String adapterName;
  
  private List<Property> resourceInstanceProperties = new LinkedList<Property>();
  private Property propertyID;
  
  private static OpenstackAdapter openstackAdapterSingleton;
  public static OpenstackAdapter getInstance(){
    if(openstackAdapterSingleton == null){
      openstackAdapterSingleton = new OpenstackAdapter("OpenstackAdapter1");
    }
    return openstackAdapterSingleton;
  }
  
  private OpenstackAdapter(String adapterName){
    openstackClient = OpenstackClient.getInstance();
	  
    this.adapterName = adapterName;
    
    adapterModel = ModelFactory.createDefaultModel();

    adapterModel.setNsPrefix("", "http://fiteagleinternal#");
    adapterModel.setNsPrefix(ADAPTER_SPECIFIC_PREFIX[0], ADAPTER_SPECIFIC_PREFIX[1]);
    adapterModel.setNsPrefix("fiteagle", FITEAGLE_ONTOLOGY_PREFIX);
    adapterModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
    adapterModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    adapterModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    adapterModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

    resource = adapterModel.createResource(ADAPTER_SPECIFIC_PREFIX[1]+RESOURCE_INSTANCE_NAME);
    resource.addProperty(RDF.type, OWL.Class);
    resource.addProperty(RDFS.subClassOf, MessageBusOntologyModel.classResource);

    adapter = adapterModel.createResource(ADAPTER_SPECIFIC_PREFIX[1]+ADAPTER_CLASS_NAME);
    adapter.addProperty(RDF.type, OWL.Class);
    adapter.addProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);

    adapter.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, resource);
    adapter.addProperty(RDFS.label, adapterModel.createLiteral("OpenstackAdapterType ", "en"));

    resource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapter);
    resource.addProperty(RDFS.label, adapterModel.createLiteral(RESOURCE_INSTANCE_NAME, "en"));
    
    propertyID = adapterModel.createProperty(ADAPTER_SPECIFIC_PREFIX[1]+"id");
    propertyID.addProperty(RDF.type, OWL.DatatypeProperty);
    propertyID.addProperty(RDFS.domain, resource);
    propertyID.addProperty(RDFS.range, XSD.xstring);
    propertyID.addProperty(RDFS.label, "The ID of the Openstack VM", "en");
    resourceInstanceProperties.add(propertyID);
    
    adapterInstance = adapterModel.createResource("http://fiteagleinternal#" + adapterName);
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterModel.createLiteral("A deployed openstack adapter named: " + adapterName, "en"));
    adapterInstance.addProperty(RDFS.comment, adapterModel.createLiteral("An openstack adapter that can handle VMs.", "en"));
    
    updateInstanceList();
    for(Model instance : resourceInstances){
      adapterModel.add(instance);
    }
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
  public Model handleMonitorInstance(String instanceName, Model modelInstances) {
    Server currentServer = (Server) instanceList.get(instanceName);

    Resource serverInstance = modelInstances.createResource("http://fiteagleinternal#" + instanceName);
    addPropertiesToResource(serverInstance, currentServer, instanceName);

    return modelInstances;
  }

  @Override
  public Model handleGetAllInstances(Model modelInstances) {
    for(String key : instanceList.keySet()) {

      Server server = (Server) instanceList.get(key);

      Resource openstackInstance = modelInstances.createResource("http://fiteagleinternal#" + key);
      addPropertiesToResource(openstackInstance, server, key);
    }
    return modelInstances;
  }
  
  protected void updateInstanceList(){
    instanceList.clear();
    Servers servers = openstackClient.listServers();
    for(Server server : servers.getList()){
      instanceList.put(server.getName(), server);
      
      Model createdResourceInstanceModel = getSingleInstanceModel(server.getName());
      resourceInstances.add(createdResourceInstanceModel);
    }
  }
  
  private void addPropertiesToResource(Resource openstackInstance, Server server, String instanceName) {
    openstackInstance.addProperty(RDF.type, resource);
    openstackInstance.addProperty(RDFS.label, "OpenstackVM: " + instanceName);
    openstackInstance.addProperty(RDFS.comment, adapterModel.createLiteral("Openstack Virtual Machine " + instanceName));
    
    openstackInstance.addLiteral(propertyID, server.getId());
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
  
  @Override
  public String getAdapterName() {
    return adapterName;
  }

}
