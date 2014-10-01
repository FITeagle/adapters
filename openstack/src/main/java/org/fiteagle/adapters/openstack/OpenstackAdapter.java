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

  private String[] adapterSpecificPrefix = { "openstack", "http://fiteagle.org/ontology/adapter/openstack#" };
  private static OpenstackAdapter openstackAdapterSingleton;
  
  private Resource openstackResource;
  
  public static OpenstackAdapter getInstance(){
    if(openstackAdapterSingleton == null){
      openstackAdapterSingleton = new OpenstackAdapter();
    }
    return openstackAdapterSingleton;
  }
  
  private OpenstackAdapter(){

    adapterName = "DeployedOpenstackAdapter";
    
    modelGeneral = ModelFactory.createDefaultModel();

    modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
    modelGeneral.setNsPrefix("openstack", "http://fiteagle.org/ontology/adapter/openstack#");
    modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
    modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
    modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

    openstackResource = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/openstack#Openstack");
    openstackResource.addProperty(RDF.type, OWL.Class);
    openstackResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

    adapterType = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/openstack#OpenstackVM");
    adapterType.addProperty(RDF.type, OWL.Class);
    adapterType.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));

    adapterType.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, openstackResource);
    adapterType.addProperty(RDFS.label, modelGeneral.createLiteral("OpenstackAdapterType ", "en"));

    openstackResource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapterType);
    openstackResource.addProperty(RDFS.label, modelGeneral.createLiteral("OpenstackVM", "en"));

   
    adapterInstance = modelGeneral.createResource("http://fiteagleinternal#" + adapterName);
    adapterInstance.addProperty(RDF.type, adapterType);
    adapterInstance.addProperty(RDFS.label, modelGeneral.createLiteral("A deployed openstack adapter named: " + adapterName, "en"));
    adapterInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("An openstack adapter that can handle VMs.", "en"));
  }
  
  @Override
  public Resource getAdapterManagedResource() {
    return openstackResource;
  }

  @Override
  public Object handleCreateInstance(String instanceName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String[] getAdapterSpecificPrefix() {
    return adapterSpecificPrefix.clone();
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
    openstackInstance.addProperty(RDF.type, openstackResource);
    openstackInstance.addProperty(RDFS.label, "Openstack: " + instanceName);
    openstackInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Openstack Virtual Machine " + instanceName));
//    openstackInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
  }

  @Override
  public List<String> handleConfigureInstance(Statement configureStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
