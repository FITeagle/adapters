package org.fiteagle.adapters.openstack;

import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

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
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapter extends AbstractAdapter {

  private IOpenstackClient openstackClient;
  private OpenstackParser openstackParser;
  
  private static Resource adapter;
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
    
    ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next();
    }
    
    createDefaultAdapterInstance(adapterModel);
  }
  
  private static void createDefaultAdapterInstance(Model adapterModel){
    Resource adapterInstance = adapterModel.createResource(OntologyModelUtil.getLocalNamespace()+"Openstack-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "An openstack vm server that can handle different VMs.");
    Resource testbed = adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);
    
    StmtIterator resourceIterator = adapter.listProperties(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
      Resource resource = resourceIterator.next().getObject().asResource();
      
      adapterInstance.addProperty(Omn_lifecycle.parentTo, resource);
      ResIterator propertiesIterator = adapterModel.listSubjectsWithProperty(RDFS.domain, resource);
      while (propertiesIterator.hasNext()) {
        Property p = adapterModel.getProperty(propertiesIterator.next().getURI());
        resourceInstanceProperties.add(p);
      }
    }
    
    new OpenstackAdapter(adapterInstance, adapterModel, new OpenstackClient());
  }
  
  private OpenstackAdapter(Resource adapterInstance, Model adapterModel, IOpenstackClient openstackClient){
    super(adapterInstance.getLocalName());
    
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
    ResIterator iter = model.listSubjectsWithProperty(RDF.type, getAdapterManagedResources().get(0));
    if (iter.hasNext()) {
      Resource instance = iter.next();
      String id = instance.getRequiredProperty(openstackParser.getPROPERTY_ID()).getLiteral().getString();
      openstackClient.deleteServer(id);
      return;
    }
    throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
  }
  
  @Override
  public void updateAdapterDescription(){
    Images images = openstackClient.listImages();
    if(images != null){
      openstackParser.addToAdapterInstanceDescription(images);
    }
  }
  
  @Override
  public Model updateInstance(String instanceURI, Model configureModel) {
    // TODO Auto-generated method stub
    return null;
  }

  public Resource getImageResource(){
    return adapterModel.getResource(getAdapterManagedResources().get(0).getNameSpace()+"OpenstackImage");
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
    throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
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
