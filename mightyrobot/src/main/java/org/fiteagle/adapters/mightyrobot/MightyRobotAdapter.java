package org.fiteagle.adapters.mightyrobot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class MightyRobotAdapter extends AbstractAdapter {
  
  private static final String[] ADAPTER_SPECIFIC_PREFIX = {"mightyrobotfactory","http://open-multinet.info/ontology/resource/mightyrobotfactory#"};
  private static final String[] ADAPTER_MANAGED_RESOURCE_PREFIX = {"mightyrobot","http://open-multinet.info/ontology/resource/mightyrobot#"};
  private final String[] ADAPTER_INSTANCE_PREFIX = {"av","http://federation.av.tu-berlin.de/about#"};
  
  private Model adapterModel;
  private Resource adapterInstance;
  private Resource adapter;
  private Resource resource;
  private String adapterName;
  
  private static MightyRobotAdapter mightyRobotAdapterSingleton;
  
  public static synchronized MightyRobotAdapter getInstance() {
    if (mightyRobotAdapterSingleton == null)
      mightyRobotAdapterSingleton = new MightyRobotAdapter();
    return mightyRobotAdapterSingleton;
  }
  
  private String instanceClassResourceString = "MightyRobot";
  private String adapterResourceString = "MightyRobotAdapter";
  
  private Property mightyRobotPropertyDancing;
  private Property mightyRobotPropertyExploded;
  private Property mightyRobotPropertyHeadRotation;
  private Property mightyRobotPropertyNickname;
  
  private List<Property> resourceControlProperties = new LinkedList<Property>();
  
  public MightyRobotAdapter() {
    
    adapterName = "MightyRobotFactory-1";
    
    // Provide model with needed prefixes
    adapterModel = ModelFactory.createDefaultModel();
    adapterModel.setNsPrefix(ADAPTER_SPECIFIC_PREFIX[0], ADAPTER_SPECIFIC_PREFIX[1]);
    adapterModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
    adapterModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    adapterModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    adapterModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    
    // instantiate instance class resource
    resource = adapterModel.createResource(ADAPTER_MANAGED_RESOURCE_PREFIX[1] + instanceClassResourceString);
    resource.addProperty(RDF.type, OWL.Class);
    resource.addProperty(RDFS.subClassOf, adapterModel.createResource(MessageBusOntologyModel.classResource));
    
    // instantiate adapter type resource
    adapter = adapterModel.createResource(ADAPTER_SPECIFIC_PREFIX[1] + adapterResourceString);
    adapter.addProperty(RDF.type, OWL.Class);
    adapter.addProperty(RDFS.subClassOf, adapterModel.createResource(MessageBusOntologyModel.classAdapter));
    
    adapter.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, resource);
    adapter.addProperty(RDFS.label, adapterModel.createLiteral(adapterResourceString + "Type ", "en"));
    
    resource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapter);
    resource.addProperty(RDFS.label, adapterModel.createLiteral(instanceClassResourceString + "Resource", "en"));
    
    // create properties
    mightyRobotPropertyDancing = generateProperty(adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1] + "dancing"),
        XSD.xboolean);
    resourceControlProperties.add(mightyRobotPropertyDancing);
    
    mightyRobotPropertyExploded = generateProperty(adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1] + "exploded"),
        XSD.xboolean);
    resourceControlProperties.add(mightyRobotPropertyExploded);
    
    mightyRobotPropertyHeadRotation = generateProperty(
        adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1] + "headRotation"), XSD.integer);
    resourceControlProperties.add(mightyRobotPropertyHeadRotation);
    
    mightyRobotPropertyNickname = generateProperty(adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1] + "nickname"),
        XSD.xstring);
    
    // instantiate adapter instance resource
    adapterInstance = adapterModel.createResource(ADAPTER_INSTANCE_PREFIX[1] + adapterName);
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label,
        adapterModel.createLiteral("A deployed Mighty Robot Adapter named: " + adapterName, "en"));
    adapterInstance.addProperty(RDFS.comment, adapterModel.createLiteral(
        "A Mighty Robot Adapter that can handle multiple Robots and their shenanigans.", "en"));
    // Testbed Addendum
    adapterInstance.addProperty(adapterModel.createProperty("http://open-multinet.info/ontology/omn#partOfGroup"),
        adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed"));
  }
  
  private Property generateProperty(Property template, Resource XSDType) {
    template.addProperty(RDF.type, OWL.DatatypeProperty);
    template.addProperty(RDFS.domain, resource);
    template.addProperty(RDFS.range, XSDType);
    return template;
  }
  
  public void init() {
    
  }
  
  @Override
  public Object handleCreateInstance(String instanceName, Map<String, String> properties) {
    MightyRobot temp = new MightyRobot(this, instanceName);
    return temp;
  }
  
  @Override
  public Model handleMonitorInstance(String instanceName, Model modelInstances) {
    MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceName);
    
    Resource mightyRobotInstance = modelInstances.createResource(ADAPTER_INSTANCE_PREFIX[1] + instanceName);
    addPropertiesToResource(mightyRobotInstance, currentMightyRobot, instanceName);
    
    return modelInstances;
  }
  
  @Override
  public Model handleGetAllInstances(Model modelInstances) {
    
    for (String key : instanceList.keySet()) {
      
      MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(key);
      
      Resource mightyRobotInstance = modelInstances.createResource(ADAPTER_INSTANCE_PREFIX[1] + key);
      addPropertiesToResource(mightyRobotInstance, currentMightyRobot, key);
      
    }
    return modelInstances;
  }
  
  public void addPropertiesToResource(Resource mightyRobotInstance, MightyRobot currentMightyRobot, String instanceName) {
    mightyRobotInstance.addProperty(RDF.type, resource);
    mightyRobotInstance.addProperty(RDFS.label, "MightyRobot: " + instanceName);
    mightyRobotInstance.addProperty(RDFS.comment,
        adapterModel.createLiteral("MightyRobot in da house " + instanceName, "en"));
    mightyRobotInstance.addLiteral(mightyRobotPropertyDancing, currentMightyRobot.getDancing());
    mightyRobotInstance.addLiteral(mightyRobotPropertyExploded, currentMightyRobot.getExploded());
    mightyRobotInstance.addLiteral(mightyRobotPropertyHeadRotation, currentMightyRobot.getHeadRotation());
    mightyRobotInstance.addLiteral(mightyRobotPropertyNickname, currentMightyRobot.getNickname());
  }
  
  @Override
  public List<String> configureInstance(Statement configureStatement) {
    // TODO Auto-generated method stub
    
    Resource currentResource = configureStatement.getSubject();
    String instanceName = currentResource.getLocalName();
    
    List<String> updatedProperties = new LinkedList<String>();
    
    if (instanceList.containsKey(instanceName)) {
      MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceName);
      
      for (Property currentProperty : resourceControlProperties) {
        StmtIterator iter2 = currentResource.listProperties(currentProperty);
        
        while (iter2.hasNext()) {
          
          if (currentProperty == mightyRobotPropertyDancing) {
            currentMightyRobot.setDancing(iter2.nextStatement().getObject().asLiteral().getBoolean());
            updatedProperties.add("dancing");
          } else if (currentProperty == mightyRobotPropertyExploded) {
            currentMightyRobot.setExploded(iter2.nextStatement().getObject().asLiteral().getBoolean());
            updatedProperties.add("exploded");
          } else if (currentProperty == mightyRobotPropertyHeadRotation) {
            currentMightyRobot.setHeadRotation((int) iter2.nextStatement().getObject().asLiteral().getLong());
            updatedProperties.add("headRotation");
          } else if (currentProperty == mightyRobotPropertyNickname) {
            currentMightyRobot.setNickname(iter2.nextStatement().getObject().asLiteral().getString());
            updatedProperties.add("nickname");
          }
        }
      }
      
    }
    
    return updatedProperties;
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
  public void updateInstanceList() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void handleTerminateInstance(String instanceName) {
    // TODO Auto-generated method stub
    
  }
  
}
