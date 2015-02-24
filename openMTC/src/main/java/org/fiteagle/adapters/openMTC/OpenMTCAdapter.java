package org.fiteagle.adapters.openMTC;

import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.openMTC.client.OpenMTCClient;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenMTCAdapter extends AbstractAdapter {

  private OpenMTCClient openMTCClient;
  
  private static Resource adapter;
  private static List<Resource> resources = new ArrayList<>();
  public static List<Property> resourceInstanceProperties = new ArrayList<Property>();
  
  private Model adapterModel;
  private Resource adapterInstance;
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/openMTC.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    StmtIterator adapterIterator = adapterModel.listStatements(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next().getSubject();
    }
    
    StmtIterator resourceIterator = adapterModel.listStatements(adapter, Omn_lifecycle.implements_, (Resource) null);
    if (resourceIterator.hasNext()) {
      Resource resource = resourceIterator.next().getObject().asResource();
      resources.add(resource);
      
      StmtIterator propertiesIterator = adapterModel.listStatements(null, RDFS.domain, resource);
      while (propertiesIterator.hasNext()) {
        Property p = adapterModel.getProperty(propertiesIterator.next().getSubject().getURI());
        resourceInstanceProperties.add(p);
      }
    }
    
    Resource adapterInstance = adapterModel.createResource(OntologyModelUtil.getLocalNamespace()+"OpenMTC-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterModel.createLiteral("An OpenMTC Adapter instance"));
    adapterInstance.addProperty(RDFS.comment, adapterModel.createLiteral("An OpenMTC Adapter instance that can handle multiple OpenMTC as a Service instances", "en"));
    adapterInstance.addProperty(Omn_federation.partOfFederation, adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed"));
    
    OpenMTCAdapter openMTCAdapter = new OpenMTCAdapter(adapterInstance, adapterModel);
      
    adapterInstances.put(adapterInstance.getURI(), openMTCAdapter);
  }
  
  
  private OpenMTCAdapter(Resource adapterInstance, Model adapterModel){
    super(adapterInstance.getLocalName());
    
    openMTCClient = OpenMTCClient.getInstance();
    
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
  }
  
  @Override
  public Model createInstance(String instanceName, Model createModel) {
    openMTCClient.setUpConnection(createModel);
    return null;
  }

  @Override
  public void deleteInstance(String instanceName) {
    //TODO
  }
  
  @Override
  public void updateAdapterDescription(){
    //TODO
  }
  
  @Override
  public Model configureInstance(String instanceName, Model configureModel) {
    return null;
    // TODO Auto-generated method stub
  }

  @Override
  public List<Resource> getAdapterManagedResources() {
    return resources;
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
  public Model getInstance(String instanceURI) throws InstanceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

}
