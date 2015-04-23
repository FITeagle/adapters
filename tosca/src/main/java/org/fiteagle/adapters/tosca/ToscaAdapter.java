package org.fiteagle.adapters.tosca;

import info.openmultinet.ontology.Parser;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.AbstractConverter;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.MultipleNamespacesException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.MultiplePropertyValuesException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.RequiredResourceNotFoundException;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN.UnsupportedException;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class ToscaAdapter extends AbstractAdapter {
  
  private static Logger LOGGER = Logger.getLogger(ToscaAdapter.class.toString());
  
  private IToscaClient client;
  
  private Model adapterModel;
  private Resource adapterInstance;
  private static Resource adapter;
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/tosca.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next();
    }
    
    createDefaultAdapterInstance(adapterModel, new ToscaClient("http://localhost:8080/api/rest/tosca/v2/"));
  }
  
  protected static ToscaAdapter createDefaultAdapterInstance(Model model, IToscaClient client){
    Resource adapterInstance = model.createResource(OntologyModelUtil.getLocalNamespace()+"Tosca-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "An adapter for TOSCA-compliant resources");
    Resource testbed = model.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);
    return new ToscaAdapter(adapterInstance, model, client);
  }
  
  private ToscaAdapter(Resource adapterInstance, Model adapterModel, IToscaClient client) {
    createDefaultConfiguration(adapterInstance.getLocalName());
    
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    this.client = client;
    
    adapterInstances.put(adapterInstance.getURI(), this);
  }
  
  @Override
  public Model createInstances(Model createModel) throws ProcessingException, InvalidRequestException {
    String definitions = parseToDefinitions(createModel);
    LOGGER.log(Level.INFO, "Input definitions: \n"+definitions);
    
    Definitions resultDefinitions = client.createDefinitions(definitions);
    LOGGER.log(Level.INFO, "Result definitions: \n"+toString(resultDefinitions));
    
    return parseToModel(resultDefinitions);    
  }

  @Override
  public Model createInstance(String instanceURI, Model createModel) throws ProcessingException, InvalidRequestException {
    return createInstances(createModel);
  }
  
  @Override
  public Model deleteInstances(Model model) throws InvalidRequestException, ProcessingException {
    Model deletedInstancesModel = super.deleteInstances(model);
    
    ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, Omn.Topology);
    while(resourceInstanceIterator.hasNext()){
      String instanceURI = resourceInstanceIterator.next().getURI();
      LOGGER.log(Level.INFO, "Deleting instance: " + instanceURI);
      deleteInstance(instanceURI);
      Resource deletedInstance = deletedInstancesModel.createResource(instanceURI);
      deletedInstance.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Removing);
    }
    
    return deletedInstancesModel;
  }
  
  @Override
  public Model getInstances(Model model) throws ProcessingException, InvalidRequestException, InstanceNotFoundException {
    Model instancesModel = null;
    try{
      instancesModel = super.getInstances(model);
    } catch(InstanceNotFoundException e){
      LOGGER.log(Level.INFO, "No resource instances found, looking for topologies..");
    }
    
    ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, Omn.Topology);
    while(resourceInstanceIterator.hasNext()){
      String instanceURI = resourceInstanceIterator.next().getURI();
      Model createdInstance = getInstance(instanceURI);
      instancesModel.add(createdInstance);
    }
    
    return instancesModel;
  }

  public Model updateInstances(Model model) throws InvalidRequestException, ProcessingException {
    Model updatedInstancesModel = null;
    try{
      updatedInstancesModel = super.updateInstances(model);
    } catch(InstanceNotFoundException e){
      LOGGER.log(Level.INFO, "No resource instances found, looking for topologies..");
    }
    
    ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, Omn.Topology);
    while(resourceInstanceIterator.hasNext()){
      Resource resourceInstance = resourceInstanceIterator.next();
      LOGGER.log(Level.INFO, "Updating instance: " + resourceInstance);
      
      Model updatedModel = updateInstance(resourceInstance.getURI(), model);
      updatedInstancesModel.add(updatedModel);
    }
    
    return updatedInstancesModel;
  }
  
  @Override
  public Model updateInstance(String instanceURI, Model udpateModel) throws InvalidRequestException, ProcessingException {
    String id = getLocalname(instanceURI);
    String definitions = parseToDefinitions(udpateModel);
    LOGGER.log(Level.INFO, "Input definitions: \n"+definitions);
    
    Definitions resultDefinitions = client.updateDefinitions(id, definitions);     
    LOGGER.log(Level.INFO, "Result definitions: \n"+toString(resultDefinitions));
    
    return parseToModel(resultDefinitions);
  }

  @Override
  public void deleteInstance(String instanceURI) throws InvalidRequestException, ProcessingException {
    String id = getLocalname(instanceURI);
    client.deleteDefinitions(id);
  }
  
  @Override
  public Model getInstance(String instanceURI) throws InstanceNotFoundException, ProcessingException, InvalidRequestException {
    String id = getLocalname(instanceURI);
    Definitions definitions;
    try{
      definitions = client.getSingleNodeDefinitions(id);
    } catch(InstanceNotFoundException e){
      LOGGER.log(Level.INFO, "No node with id "+id+" found, looking for topologies");
      try{
        definitions = client.getDefinitions(id);
      } catch(InstanceNotFoundException e1){
        throw new InstanceNotFoundException("No node or topologies with id "+id+" found");
      }
    }
    LOGGER.log(Level.INFO, "Result definitions: \n"+toString(definitions));
    
    return parseToModel(definitions);
  }
  
  @Override
  public Model getAllInstances() throws InstanceNotFoundException, ProcessingException {
    Definitions definitions = client.getAllDefinitions();
    return parseToModel(definitions);
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
  public void updateAdapterDescription() throws ProcessingException {
    LOGGER.log(Level.INFO, "Updating adapter description: Getting types via ToscaClient..");
    Definitions types = client.getAllTypes();
    Model model = parseToModel(types);
    
    updateAdapterDescriptionWithModel(model);
  }

  protected void updateAdapterDescriptionWithModel(Model model) {
    List<Resource> resources = model.listSubjectsWithProperty(RDFS.subClassOf, Omn.Resource).toList();
    if(resources.isEmpty()){
      LOGGER.log(Level.WARNING, "Could not find any resources to manage!");
    }
    for(Resource resource : resources){
      LOGGER.log(Level.INFO, "Found resource: "+resource.getURI());
      adapterInstance.addProperty(Omn_lifecycle.canImplement, resource);
    }
    adapterModel.add(model);
    adapterModel.setNsPrefixes(model.getNsPrefixMap());
  }

  protected String parseToDefinitions(Model createModel) throws InvalidRequestException {
    try{
      InfModel infModel = createInfModel(createModel);
      return OMN2Tosca.getTopology(infModel);      
    } catch(InvalidModelException | JAXBException | MultiplePropertyValuesException | RequiredResourceNotFoundException | MultipleNamespacesException e){
      throw new InvalidRequestException(e);
    }
  }
  
  protected Model parseToModel(Definitions definitions) throws ProcessingException {
    try {
      Model resultModel = Tosca2OMN.getModel(definitions);
      adapterModel.setNsPrefixes(resultModel.getNsPrefixMap());
      return resultModel;
    } catch (UnsupportedException e) {
      throw new ProcessingException(e);
    }      
  }
    
  protected String getLocalname(String instanceURI) throws InvalidRequestException {
    try{
      return OntologyModelUtil.getNamespaceAndLocalname(instanceURI, adapterModel.getNsPrefixMap())[1];
    } catch(IllegalArgumentException e){
      throw new InvalidRequestException(e);
    }
  }
  
  protected static String toString(Definitions definitions) throws ProcessingException {
    try {
      return AbstractConverter.toString(definitions, OMN2Tosca.JAXB_PACKAGE_NAME);
    } catch (JAXBException e) {
      throw new ProcessingException(e);
    }
  }
  
  protected InfModel createInfModel(Model model) throws InvalidModelException{
    model.add(adapterModel);
    Parser parser = new Parser(model);
    return parser.getInfModel();
  }
  
}
