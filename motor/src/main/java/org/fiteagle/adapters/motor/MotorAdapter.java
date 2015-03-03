package org.fiteagle.adapters.motor;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

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
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class MotorAdapter extends AbstractAdapter {
  
  private Model adapterModel;
  private Resource adapterInstance;
  private static Resource adapter;
  private static List<Resource> resources = new ArrayList<>();
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
  private static List<Property> motorControlProperties = new ArrayList<Property>();
  
  protected HashMap<String, Motor> instanceList = new HashMap<String, Motor>();
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/motor.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next();
    }
    
    StmtIterator resourceIterator = adapter.listProperties(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
      Resource resource = resourceIterator.next().getObject().asResource();
      resources.add(resource);
      
      ResIterator propertiesIterator = adapterModel.listSubjectsWithProperty(RDFS.domain, resource);
      while (propertiesIterator.hasNext()) {
        Property p = adapterModel.getProperty(propertiesIterator.next().getURI());
        motorControlProperties.add(p);
      }
    }
    
    createDefaultAdapterInstance(adapterModel);
  }
  
  private static void createDefaultAdapterInstance(Model model){
    Resource adapterInstance = model.createResource(OntologyModelUtil.getLocalNamespace()+"MotorGarage-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(Omn_lifecycle.parentTo,model.createResource("http://open-multinet.info/ontology/resource/motor#Motor"));
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "A motor garage adapter that can simulate different dynamic motor resources.");
    adapterInstance.addLiteral(MessageBusOntologyModel.maxInstances, 10);
    Resource testbed = model.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);
    Property longitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
    Property latitude = model.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
    adapterInstance.addProperty(latitude, "52.516377");
    adapterInstance.addProperty(longitude, "13.323732");
    
    new MotorAdapter(adapterInstance, model);
  }
  
  private MotorAdapter(Resource adapterInstance, Model adapterModel) {
    super(adapterInstance.getLocalName());
    
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    adapterInstances.put(adapterInstance.getURI(), this);
  }
  
  @Override
  public Model createInstance(String instanceURI, Model modelCreate) {
    Motor motor = new Motor(this, instanceURI);
    instanceList.put(instanceURI, motor);
    updateInstance(instanceURI, modelCreate);
    return parseToModel(motor);
  }
  
  protected Model parseToModel(Motor motor) {
    Resource resource = ModelFactory.createDefaultModel().createResource(motor.getInstanceName());
    resource.addProperty(RDF.type, MotorAdapter.resources.get(0));
    resource.addProperty(RDF.type, Omn.Resource);
    resource.addProperty(RDFS.label, resource.getLocalName());
    resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
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
  public Model updateInstance(String instanceURI, Model configureModel) {
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
  public void deleteInstance(String instanceURI) {
    Motor motor = getInstanceByName(instanceURI);
    motor.terminate();
    instanceList.remove(instanceURI);
  }
  
  public Motor getInstanceByName(String instanceURI) {
    return (Motor) instanceList.get(instanceURI);
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
  public void updateAdapterDescription() {
  }

  @Override
  public Model getInstance(String instanceURI) throws InstanceNotFoundException {
    Motor motor = instanceList.get(instanceURI);
    if(motor == null){
      throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
    }
    return parseToModel(motor);
  }

  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
    Model model = ModelFactory.createDefaultModel();
    for(String uri : instanceList.keySet()){
      model.add(getInstance(uri));
    }
    return model;
  }
  
}
