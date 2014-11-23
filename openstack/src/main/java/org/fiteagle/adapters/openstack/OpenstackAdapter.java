package org.fiteagle.adapters.openstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.openstack.client.IOpenstackClient;
import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.OpenstackParser;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
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
  
  private IOpenstackClient openstackClient;
  private OpenstackParser openstackParser;
  
  private static Resource adapter;
  private static Resource resource;
  public static List<Property> resourceInstanceProperties = new ArrayList<Property>();
  
  private Model adapterModel;
  private Resource adapterInstance;
  
  public static HashMap<String,OpenstackAdapter> openstackAdapterInstances = new HashMap<>();
  
  public static OpenstackAdapter getInstance(String URI){
    return openstackAdapterInstances.get(URI);
  }
  
  public static OpenstackAdapter getTestInstance(IOpenstackClient openstackClient){
    OpenstackAdapter testInstance = null;
    Model adapterModel = OntologyModels.loadModel("ontologies/openstack.ttl", IMessageBus.SERIALIZATION_TURTLE);
    StmtIterator adapterInstanceIterator = adapterModel.listStatements(null, RDF.type, adapter);
    while (adapterInstanceIterator.hasNext()) {
      Resource adapterInstance = adapterInstanceIterator.next().getSubject();
      testInstance = new OpenstackAdapter(adapterInstance, adapterModel, openstackClient);
      testInstance.updateAdapterDescription();
    }
    return testInstance;
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
      
      OpenstackAdapter openstackAdapter = new OpenstackAdapter(adapterInstance, adapterModel, null);
      openstackAdapter.openstackClient = OpenstackClient.getInstance(openstackAdapter);
      openstackAdapterInstances.put(adapterInstance.getURI(), openstackAdapter);
    }
  }
  
  private OpenstackAdapter(Resource adapterInstance, Model adapterModel, IOpenstackClient openstackClient){
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    
    ADAPTER_INSTANCE_PREFIX[1] = adapterInstance.getNameSpace();
    ADAPTER_INSTANCE_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_INSTANCE_PREFIX[1]);
    
    Property PROPERTY_IMAGES = adapterModel.getProperty(ADAPTER_SPECIFIC_PREFIX[1]+"images");
    Property PROPERTY_IMAGE = adapterModel.getProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"image");
    Property PROPERTY_ID = adapterModel.getProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"id");
    Property PROPERTY_IMAGE_ID = adapterModel.getProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"imageid");
    Property PROPERTY_KEYPAIRNAME = adapterModel.getProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"keypairname");
    
    this.openstackClient = openstackClient;
    openstackParser = OpenstackParser.getInstance(this, PROPERTY_ID, PROPERTY_IMAGE_ID, PROPERTY_IMAGES, PROPERTY_IMAGE, PROPERTY_KEYPAIRNAME);
  }
  
  @Override
  public Resource handleCreateInstance(String instanceName, Model newInstanceModel) {
    ServerForCreate serverForCreate = openstackParser.parseToServerForCreate(instanceName, newInstanceModel);
    Server server = openstackClient.createServer(serverForCreate);
    Resource openstackVM = openstackParser.parseToResource(server);
    return openstackVM;
  }

  @Override
  public void handleTerminateInstance(String instanceName) {
    openstackClient.deleteServer(openstackParser.getResourcePropertyID(instanceName));
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
  public void updateAdapterDescription(){
    Images images = openstackClient.listImages();
    if(images != null){
      openstackParser.addToAdapterInstanceDescription(images);
    }
    updateInstanceList();
  }
  
  private void updateInstanceList(){
    Servers servers = openstackClient.listServers();
    if(servers != null){
      openstackParser.addToAdapterInstanceDescription(servers);
    }
  }
  
  @Override
  public Model configureInstance(Statement configureStatement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Resource getAdapterManagedResource() {
    return resource;
  }
  
  public Resource getImageResource(){
    return adapterModel.getResource(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"OpenstackImage");
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
  
  public OpenstackParser getOpenstackParser(){
    return openstackParser;
  }

}
