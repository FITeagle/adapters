package org.fiteagle.adapters.openSDNCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class OpenSDNCoreAdapter extends AbstractAdapter {
  
  private static final String[] ADAPTER_SPECIFIC_PREFIX = new String[2];
  private static final String[] ADAPTER_MANAGED_RESOURCE_PREFIX = new String[2];
  private final String[] ADAPTER_INSTANCE_PREFIX = new String[2];
  
  private Model adapterModel;
  private Resource adapterInstance;
  private static Resource adapter;
  private static Resource resource;
  
  public static HashMap<String, OpenSDNCoreAdapter> adapterInstances = new HashMap<>();
  
  public static OpenSDNCoreAdapter getInstance(String URI) {
    return adapterInstances.get(URI);
  }
  
  private static List<Property> properties = new ArrayList<Property>();
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/openSDNCore.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    StmtIterator adapterIterator = adapterModel.listStatements(null, RDFS.subClassOf,
        MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next().getSubject();
      ADAPTER_SPECIFIC_PREFIX[1] = adapter.getNameSpace();
      ADAPTER_SPECIFIC_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_SPECIFIC_PREFIX[1]);
    }
    
    StmtIterator resourceIterator = adapterModel.listStatements(adapter,
        MessageBusOntologyModel.propertyFiteagleImplements, (Resource) null);
    if (resourceIterator.hasNext()) {
      resource = resourceIterator.next().getObject().asResource();
      ADAPTER_MANAGED_RESOURCE_PREFIX[1] = resource.getNameSpace();
      ADAPTER_MANAGED_RESOURCE_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_MANAGED_RESOURCE_PREFIX[1]);
    }
    
    StmtIterator propertiesIterator = adapterModel.listStatements(null, RDFS.domain, resource);
    while (propertiesIterator.hasNext()) {
      Property p = adapterModel.getProperty(propertiesIterator.next().getSubject().getURI());
      properties.add(p);
    }
    
    StmtIterator adapterInstanceIterator = adapterModel.listStatements(null, RDF.type, adapter);
    while (adapterInstanceIterator.hasNext()) {
      Resource adapterInstance = adapterInstanceIterator.next().getSubject();
      
      OpenSDNCoreAdapter motorAdapter = new OpenSDNCoreAdapter(adapterInstance, adapterModel);
      
      adapterInstances.put(adapterInstance.getURI(), motorAdapter);
    }
  }
  
  private OpenSDNCoreAdapter(Resource adapterInstance, Model adapterModel) {
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    
    ADAPTER_INSTANCE_PREFIX[1] = adapterInstance.getNameSpace();
    ADAPTER_INSTANCE_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_INSTANCE_PREFIX[1]);
  }
  
  @Override
  protected Model handleCreateInstance(String instanceURI, Model modelCreate) {
    return null;
  }
  
  @Override
  protected Model handleConfigureInstance(String instanceURI, Model configureModel) {
    return null;
  }
  
  @Override
  protected void handleDeleteInstance(String instanceURI) {
    return;
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
  public void updateAdapterDescription() {
  }
  
}
