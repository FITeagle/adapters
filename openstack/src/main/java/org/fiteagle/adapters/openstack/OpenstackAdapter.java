package org.fiteagle.adapters.openstack;

import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapter extends AbstractAdapter {

  private static final String FITEAGLE_ONTOLOGY_PREFIX = "http://fiteagle.org/ontology#";
  private static final String[] ADAPTER_SPECIFIC_PREFIX = { "openstack", "http://fiteagle.org/ontology/adapter/openstack#" };
  private static final String RESOURCE_INSTANCE_NAME = "OpenstackVM";
  private static final String ADAPTER_CLASS_NAME = "OpenstackAdapter";
  
  private static OpenstackAdapter openstackAdapterSingleton;

  private Resource openstackResourceInstance;
  
  public static OpenstackAdapter getInstance(){
    if(openstackAdapterSingleton == null){
      openstackAdapterSingleton = new OpenstackAdapter();
    }
    return openstackAdapterSingleton;
  }
  
  private OpenstackAdapter(){

    adapterName = "ADeployedOpenstackAdapter";
    
    modelGeneral = ModelFactory.createDefaultModel();

    modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
    modelGeneral.setNsPrefix(ADAPTER_SPECIFIC_PREFIX[0], ADAPTER_SPECIFIC_PREFIX[1]);
    modelGeneral.setNsPrefix("fiteagle", FITEAGLE_ONTOLOGY_PREFIX);
    modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
    modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

    openstackResourceInstance = modelGeneral.createResource(ADAPTER_SPECIFIC_PREFIX[1]+RESOURCE_INSTANCE_NAME);
    openstackResourceInstance.addProperty(RDF.type, OWL.Class);
    openstackResourceInstance.addProperty(RDFS.subClassOf, modelGeneral.createResource(FITEAGLE_ONTOLOGY_PREFIX+"Resource"));

    adapterType = modelGeneral.createResource(ADAPTER_SPECIFIC_PREFIX[1]+ADAPTER_CLASS_NAME);
    adapterType.addProperty(RDF.type, OWL.Class);
    adapterType.addProperty(RDFS.subClassOf, modelGeneral.createResource(FITEAGLE_ONTOLOGY_PREFIX+"Adapter"));

    adapterType.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, openstackResourceInstance);
    adapterType.addProperty(RDFS.label, modelGeneral.createLiteral("OpenstackAdapterType ", "en"));

    openstackResourceInstance.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapterType);
    openstackResourceInstance.addProperty(RDFS.label, modelGeneral.createLiteral(RESOURCE_INSTANCE_NAME, "en"));
    
    //TODO: properties
   
    adapterInstance = modelGeneral.createResource("http://fiteagleinternal#" + adapterName);
    adapterInstance.addProperty(RDF.type, adapterType);
    adapterInstance.addProperty(RDFS.label, modelGeneral.createLiteral("A deployed openstack adapter named: " + adapterName, "en"));
    adapterInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("An openstack adapter that can handle VMs.", "en"));
    
    adapterInstance.addProperty(modelGeneral.createProperty("http://fiteagleinternal#isAdapterIn"), modelGeneral.createResource("http://fiteagleinternal#FITEAGLE_Testbed"));
  }
  
  @Override
  public Resource getAdapterManagedResource() {
    return openstackResourceInstance;
  }

  @Override
  public Object handleCreateInstance(String instanceName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] getAdapterSpecificPrefix() {
    return ADAPTER_SPECIFIC_PREFIX.clone();
  }

  @Override
  public Model handleMonitorInstance(String instanceName, Model modelInstances) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Model handleGetAllInstances(Model modelInstances) {
    for(String key : instanceList.keySet()) {

      Server server = (Server) instanceList.get(key);

      Resource openstackInstance = modelInstances.createResource("http://fiteagleinternal#" + key);
      addPropertiesToResource(openstackInstance, server, key);
    }
    return modelInstances;
  }
  
  private void addPropertiesToResource(Resource openstackInstance, Server server, String instanceName) {
    openstackInstance.addProperty(RDF.type, openstackResourceInstance);
    openstackInstance.addProperty(RDFS.label, "OpenstackVM: " + instanceName);
    openstackInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Openstack Virtual Machine " + instanceName));
    
    //	TODO: properties
  }

  @Override
  public List<String> handleConfigureInstance(Statement configureStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
