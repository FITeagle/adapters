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
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapter extends AbstractAdapter {

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
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/openstack.ttl", IMessageBus.SERIALIZATION_TURTLE);
    StmtIterator adapterInstanceIterator = adapterModel.listStatements(null, RDF.type, adapter);
    while (adapterInstanceIterator.hasNext()) {
      Resource adapterInstance = adapterInstanceIterator.next().getSubject();
      testInstance = new OpenstackAdapter(adapterInstance, adapterModel, openstackClient);
      testInstance.updateAdapterDescription();
    }
    return testInstance;
  }
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/openstack.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    StmtIterator adapterIterator = adapterModel.listStatements(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next().getSubject();
    }
    
    StmtIterator resourceIterator = adapterModel.listStatements(adapter, MessageBusOntologyModel.propertyFiteagleImplements, (Resource) null);
    if (resourceIterator.hasNext()) {
      resource = resourceIterator.next().getObject().asResource();
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
    
    Property PROPERTY_IMAGES = adapterModel.getProperty(getAdapterManagedResource().getNameSpace()+"images");
    Property PROPERTY_IMAGE = adapterModel.getProperty(getAdapterManagedResource().getNameSpace()+"image");
    Property PROPERTY_ID = adapterModel.getProperty(getAdapterManagedResource().getNameSpace()+"id");
    Property PROPERTY_IMAGE_ID = adapterModel.getProperty(getAdapterManagedResource().getNameSpace()+"imageid");
    Property PROPERTY_KEYPAIRNAME = adapterModel.getProperty(getAdapterManagedResource().getNameSpace()+"keypairname");
    Property PROPERTY_FLAVOR = adapterModel.getProperty(getAdapterManagedResource().getNameSpace()+"flavor");
    
    this.openstackClient = openstackClient;
    openstackParser = OpenstackParser.getInstance(this, PROPERTY_ID, PROPERTY_IMAGE_ID, PROPERTY_IMAGES, PROPERTY_IMAGE, PROPERTY_KEYPAIRNAME, PROPERTY_FLAVOR);
  }
  
  @Override
  protected Model handleCreateInstance(String instanceURI, Model newInstanceModel) {
    ServerForCreate serverForCreate = openstackParser.parseToServerForCreate(instanceURI, newInstanceModel);
    Server server = openstackClient.createServer(serverForCreate);
    Model openstackVM = openstackParser.parseToModel(server);
    return openstackVM;
  }

  @Override
  protected void handleDeleteInstance(String instanceURI) {
    openstackClient.deleteServer(openstackParser.getResourcePropertyID(instanceURI));
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
  protected Model handleConfigureInstance(String instanceURI, Model configureModel) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Resource getAdapterManagedResource() {
    return resource;
  }
  
  public Resource getImageResource(){
    return adapterModel.getResource(getAdapterManagedResource().getNameSpace()+"OpenstackImage");
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
