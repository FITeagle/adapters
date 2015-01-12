package org.fiteagle.adapters.motor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
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

public final class MotorAdapter extends AbstractAdapter {
  
  private static final String[] ADAPTER_MANAGED_RESOURCE_PREFIX = new String[2];
  
  private Model adapterModel;
  private Resource adapterInstance;
  private static Resource adapter;
  private static Resource resource;
  
  public static HashMap<String, MotorAdapter> motorAdapterInstances = new HashMap<>();
  
  public static MotorAdapter getInstance(String URI) {
    return motorAdapterInstances.get(URI);
  }
  
  private static List<Property> motorControlProperties = new ArrayList<Property>();
  
  protected HashMap<String, Motor> instanceList = new HashMap<String, Motor>();
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/motor.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    StmtIterator adapterIterator = adapterModel.listStatements(null, RDFS.subClassOf,
        MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next().getSubject();
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
      motorControlProperties.add(p);
    }
    
    StmtIterator adapterInstanceIterator = adapterModel.listStatements(null, RDF.type, adapter);
    while (adapterInstanceIterator.hasNext()) {
      Resource adapterInstance = adapterInstanceIterator.next().getSubject();
      
      MotorAdapter motorAdapter = new MotorAdapter(adapterInstance, adapterModel);
      
      motorAdapterInstances.put(adapterInstance.getURI(), motorAdapter);
    }
  }
  
  private MotorAdapter(Resource adapterInstance, Model adapterModel) {
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
  }
  
  @Override
  protected Model handleCreateInstance(String instanceURI, Model modelCreate) {
    Motor motor = new Motor(this, instanceURI);
    instanceList.put(instanceURI, motor);
    handleConfigureInstance(instanceURI, modelCreate);
    return parseToModel(motor);
  }
  
  protected Model parseToModel(Motor motor) {
    Resource resource = ModelFactory.createDefaultModel().createResource(motor.getInstanceName());
    resource.addProperty(RDF.type, MotorAdapter.resource);
    resource.addProperty(RDFS.label, motor.getInstanceName());
    for (Property p : motorControlProperties) {
      switch (p.getLocalName()) {
        case "rpm":
          resource.addLiteral(p, motor.getRpm());
          break;
        case "maxRpm":
          resource.addLiteral(p, motor.getMaxRpm());
          break;
        case "manufacturer":
          resource.addLiteral(p, motor.getManufacturer());
          break;
        case "throttle":
          resource.addLiteral(p, motor.getThrottle());
          break;
        case "isDynamic":
          resource.addLiteral(p, motor.isDynamic());
          break;
      }
    }
    return resource.getModel();
  }
  
  @Override
  protected Model handleConfigureInstance(String instanceURI, Model configureModel) {
    if (instanceList.containsKey(instanceURI)) {
      Motor currentMotor = (Motor) instanceList.get(instanceURI);
      StmtIterator iter = configureModel.listStatements();
      while(iter.hasNext()){
        currentMotor.updateProperty(iter.next());
      }
      return parseToModel(currentMotor);
    }
    return ModelFactory.createDefaultModel();
  }
  
  @Override
  protected void handleDeleteInstance(String instanceURI) {
    Motor motor = getInstanceByName(instanceURI);
    motor.terminate();
    instanceList.remove(instanceURI);
  }
  
  @Override
  public String[] getAdapterManagedResourcePrefix() {
    return ADAPTER_MANAGED_RESOURCE_PREFIX.clone();
  }
  
  public Motor getInstanceByName(String instanceURI) {
    return (Motor) instanceList.get(instanceURI);
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
