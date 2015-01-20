package org.fiteagle.adapters.openstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
  public static OpenstackAdapter getTestInstance(IOpenstackClient openstackClient){
    OpenstackAdapter instance = (OpenstackAdapter) adapterInstances.values().iterator().next();
    OpenstackAdapter testInstance = new OpenstackAdapter(instance.getAdapterInstance(), instance.getAdapterDescriptionModel(), openstackClient);
    testInstance.updateAdapterDescription();
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
    
    createDefaultAdapterInstance(adapterModel);
  }
  
  private static void createDefaultAdapterInstance(Model model){
    Resource adapterInstance = model.createResource("http://federation.av.tu-berlin.de/about#Openstack-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "An openstack vm server that can handle different VMs.");
    Resource testbed = model.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(MessageBusOntologyModel.partOfFederation, testbed);
    new OpenstackAdapter(adapterInstance, model, new OpenstackClient());
  }
  
  private OpenstackAdapter(Resource adapterInstance, Model adapterModel, IOpenstackClient openstackClient){
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    
    this.openstackClient = openstackClient;
    this.openstackParser = OpenstackParser.getInstance(this);
    
    adapterInstances.put(adapterInstance.getURI(), this);
  }
  
  @Override
  public Model createInstance(String instanceURI, Model newInstanceModel) {
    ServerForCreate serverForCreate = openstackParser.parseToServerForCreate(instanceURI, newInstanceModel);
    Server server = openstackClient.createServer(serverForCreate);
    Model model = openstackParser.parseToModel(server);
    return model;
  }

  @Override
  public void deleteInstance(String instanceURI) throws InstanceNotFoundException {
    Model model = getInstance(instanceURI);
    StmtIterator iter = getResourceInstanceIterator(model);
    if (iter.hasNext()) {
      Resource instance = iter.next().getSubject();
      String id = instance.getRequiredProperty(openstackParser.getPROPERTY_ID()).getLiteral().getString();
      openstackClient.deleteServer(id);
      return;
    }
    throw new InstanceNotFoundException();
  }
  
  @Override
  public void updateAdapterDescription(){
    Images images = openstackClient.listImages();
    if(images != null){
      openstackParser.addToAdapterInstanceDescription(images);
    }
  }
  
  @Override
  public Model configureInstance(String instanceURI, Model configureModel) {
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

  @Override
  public Model getInstance(String instanceURI) throws InstanceNotFoundException {
    Servers servers = openstackClient.listServers();
    for(Server server : servers.getList()){
      if(server.getName().equals(instanceURI)){
        return openstackParser.parseToModel(server);
      }
    }
    throw new InstanceNotFoundException();
  }

  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
    Model model = ModelFactory.createDefaultModel();
    Servers servers = openstackClient.listServers();
    for(Server server : servers.getList()){
      model.add(getInstance(server.getName()));
    }
    return model;
  }

}
