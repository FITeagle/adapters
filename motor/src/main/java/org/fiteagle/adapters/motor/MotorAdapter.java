package org.fiteagle.adapters.motor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  
  private Model adapterModel;
  private Resource adapterInstance;
  private static Resource adapter;
  private static Resource resource;
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
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
    }
    
    StmtIterator propertiesIterator = adapterModel.listStatements(null, RDFS.domain, resource);
    while (propertiesIterator.hasNext()) {
      Property p = adapterModel.getProperty(propertiesIterator.next().getSubject().getURI());
      motorControlProperties.add(p);
    }
    
    createDefaultAdapterInstance(adapterModel);
  }
  
  private static void createDefaultAdapterInstance(Model model){
    Resource adapterInstance = model.createResource("http://federation.av.tu-berlin.de/about#MotorGarage-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "A motor garage adapter that can simulate different dynamic motor resources.");
    adapterInstance.addLiteral(MessageBusOntologyModel.maxInstances, 10);
    Resource testbed = model.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(MessageBusOntologyModel.partOfFederation, testbed);
    Property longitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
    Property latitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
    adapterInstance.addProperty(latitude, "52.516377");
    adapterInstance.addProperty(longitude, "13.323732");
    
    new MotorAdapter(adapterInstance, model);
  }
  
  private MotorAdapter(Resource adapterInstance, Model adapterModel) {
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    adapterInstances.put(adapterInstance.getURI(), this);
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
