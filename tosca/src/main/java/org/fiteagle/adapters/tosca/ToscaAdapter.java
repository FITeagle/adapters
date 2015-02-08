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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpException;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class ToscaAdapter extends AbstractAdapter {
  
  private static Logger LOGGER = Logger.getLogger(ToscaAdapter.class.toString());
  
  private ToscaClient client;
  
  private Model adapterModel;
  private Resource adapterInstance;
  private static Resource adapter;
  
  private static List<Resource> resources = new ArrayList<>();
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/tosca.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next();
    }
    
    StmtIterator resourceIterator = adapter.listProperties(Omn_lifecycle.implements_);
    while (resourceIterator.hasNext()) {
      Resource resource = resourceIterator.next().getResource();
      resources.add(resource);
    }
    
    createDefaultAdapterInstance(adapterModel);
  }
  
  private static void createDefaultAdapterInstance(Model model){
    Resource adapterInstance = model.createResource(OntologyModelUtil.getLocalNamespace()+"Tosca-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "An adapter for TOSCA-compliant resources");
    Resource testbed = model.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);
    new ToscaAdapter(adapterInstance, model, new ToscaClient("http://localhost:8080/api/rest/tosca/v2/"));
  }
  
  private ToscaAdapter(Resource adapterInstance, Model adapterModel, ToscaClient client) {
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    this.client = client;
    
    adapterInstances.put(adapterInstance.getURI(), this);
  }
  
  @Override
  public Model createInstances(Model createModel) throws AdapterException {
    Model resultModel = ModelFactory.createDefaultModel();
    try{
      InfModel infModel = createInfModel(createModel);
      String definitions = OMN2Tosca.getTopology(infModel);      
      LOGGER.log(Level.INFO, "Input definitions: \n"+definitions);
      
      Definitions resultDefinitions = client.createDefinitions(definitions);
      
      String resultString = AbstractConverter.toString(resultDefinitions, OMN2Tosca.JAXB_PACKAGE_NAME);      
      LOGGER.log(Level.INFO, "Result definitions: \n"+resultString);
      
      resultModel = Tosca2OMN.getModel(resultDefinitions);      
      adapterModel.setNsPrefixes(resultModel.getNsPrefixMap());
      
    } catch(InvalidModelException | JAXBException | UnsupportedException | HttpException | IOException | MultiplePropertyValuesException |RequiredResourceNotFoundException | MultipleNamespacesException e){
      throw new AdapterException(e);
    }
    return resultModel;
  }
  
  @Override
  public Model createInstance(String instanceURI, Model createModel) throws AdapterException {
    return createInstances(createModel);
  }
  
  @Override
  public Model configureInstance(String instanceURI, Model configureModel) {
    return null;
  }
  
  @Override
  public void deleteInstance(String instanceURI) throws AdapterException {
    String id;
    try{
      id = OntologyModelUtil.getNamespaceAndLocalname(instanceURI, adapterModel.getNsPrefixMap())[1];
    } catch(IllegalArgumentException e){
      throw new AdapterException(e);
    }
    client.deleteDefinitions(id);
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
  public void updateAdapterDescription() throws AdapterException {
    LOGGER.log(Level.INFO, "Updating adapter description: Getting NodeTypes via ToscaClient");
    Definitions nodeTypes = client.getAllNodeTypes();
    Model model = null;
    try {
      model = Tosca2OMN.getModel(nodeTypes);
    } catch (UnsupportedException e) {
      throw new AdapterException(e);
    }
    List<Resource> resources = model.listSubjectsWithProperty(RDFS.subClassOf, Omn.Resource).toList();
    if(resources.isEmpty()){
      LOGGER.log(Level.WARNING, "Could not find any resources to manage!");
    }
    for(Resource resource : resources){
      LOGGER.log(Level.INFO, "Updating adapter description: Found resource: "+resource.getURI());
      adapterInstance.addProperty(Omn_lifecycle.implements_, resource);
    }
    adapterModel.add(model);
    adapterModel.setNsPrefixes(model.getNsPrefixMap());
  }

  @Override
  public Model getInstance(String instanceURI) throws InstanceNotFoundException, AdapterException {
    String id;
    try{
      id = OntologyModelUtil.getNamespaceAndLocalname(instanceURI, adapterModel.getNsPrefixMap())[1];
    } catch(IllegalArgumentException e){
      throw new AdapterException(e);
    }
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
    
    try {
      String resultString = AbstractConverter.toString(definitions, OMN2Tosca.JAXB_PACKAGE_NAME);
      LOGGER.log(Level.INFO, "Result definitions: \n"+resultString);
    } catch (JAXBException e) {
      throw new AdapterException(e);
    }      
    
    
    Model model = null;
    try {
      model = Tosca2OMN.getModel(definitions);
    } catch (UnsupportedException e) {
      throw new AdapterException(e);
    }
    return model;
  }

  @Override
  public Model getAllInstances() throws InstanceNotFoundException, AdapterException {
    Definitions definitions = client.getAllDefinitions();
    Model model = null;
    try {
      model = Tosca2OMN.getModel(definitions);
    } catch (UnsupportedException e) {
      throw new AdapterException(e);
    }
    return model;
  }
  
  private static InfModel createInfModel(Model model) throws InvalidModelException{
    Parser parser = new Parser(model);
    final InfModel infModel = parser.getInfModel();
    return infModel;
  }
  
}
