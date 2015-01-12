package org.fiteagle.adapters.openMTC;

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
  private static Resource resource;
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
    
    StmtIterator resourceIterator = adapterModel.listStatements(adapter, MessageBusOntologyModel.propertyFiteagleImplements, (Resource) null);
    if (resourceIterator.hasNext()) {
      resource = resourceIterator.next().getObject().asResource();
    }
    
    StmtIterator propertiesIterator = adapterModel.listStatements(null, RDFS.domain, resource);
    while (propertiesIterator.hasNext()) {
      Property p = adapterModel.getProperty(propertiesIterator.next().getSubject().getURI());
      resourceInstanceProperties.add(p);
    }
    
    //TODO: remove this creation of a static instance
    Resource adapterInstance = adapterModel.createResource("http://federation.av.tu-berlin.de/about#OpenMTC-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterModel.createLiteral("An OpenMTC Adapter instance"));
    adapterInstance.addProperty(RDFS.comment, adapterModel.createLiteral("An OpenMTC Adapter instance that can handle multiple OpenMTC as a Service instances", "en"));
    adapterInstance.addProperty(adapterModel.createProperty("http://open-multinet.info/ontology/omn#partOfGroup"),adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed"));
    
    OpenMTCAdapter openMTCAdapter = new OpenMTCAdapter(adapterInstance, adapterModel);
      
    adapterInstances.put(adapterInstance.getURI(), openMTCAdapter);
  }
  
  
  private OpenMTCAdapter(Resource adapterInstance, Model adapterModel){
    openMTCClient = OpenMTCClient.getInstance();
    
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
  }
  
  @Override
  protected Model handleCreateInstance(String instanceName, Model createModel) {
    openMTCClient.setUpConnection(createModel);
    return null;
  }

  @Override
  protected void handleDeleteInstance(String instanceName) {
    //TODO
  }
  
  @Override
  public void updateAdapterDescription(){
    //TODO
  }
  
  @Override
  protected Model handleConfigureInstance(String instanceName, Model configureModel) {
    return null;
    // TODO Auto-generated method stub
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
