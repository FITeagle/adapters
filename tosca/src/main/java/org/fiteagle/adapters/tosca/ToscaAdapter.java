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
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.fiteagle.adapters.tosca.dm.ToscaMDBSender;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class ToscaAdapter extends AbstractAdapter {
  
  private static Logger LOGGER = Logger.getLogger(ToscaAdapter.class.toString());
  
  private IToscaClient client;
  
  private ToscaMDBSender sender;

  private Map<String, String> topologies = new HashMap<String, String>();

  
  public ToscaAdapter( Model adapterTBox, Resource adapterABox, ToscaMDBSender sender) {
    this.sender = sender;
    this.uuid = UUID.randomUUID().toString();
    this.adapterTBox = adapterTBox;
    this.adapterABox = adapterABox;
    Resource adapterType = null;
      ResIterator adapterIterator = adapterTBox.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
      if (adapterIterator.hasNext()) {
          adapterType = adapterIterator.next();
      }
    this.adapterABox.addProperty(RDF.type, adapterType);
      this.adapterABox.addProperty(RDFS.label, adapterABox.getLocalName());

      this.adapterABox.addProperty(RDFS.comment, "An adapter for TOSCA-compliant resources");
      Resource testbed = adapterABox.getModel().createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
      this.adapterABox.addProperty(Omn_federation.partOfFederation, testbed);

  }
  
  @Override
  public Model createInstances(Model createModel) throws ProcessingException, InvalidRequestException {
    
    try {
      ManagedThreadFactory threadFactory = (ManagedThreadFactory) new InitialContext().lookup("java:jboss/ee/concurrency/factory/default");
      CallOpenSDNcore callOpenSDNcore = new CallOpenSDNcore(createModel, this);
      Thread callOpenSDNcoreThread = threadFactory.newThread(callOpenSDNcore);
      callOpenSDNcoreThread.start();
    } catch (NamingException e) {
      LOGGER.log(Level.SEVERE, "REQUIRED VMs counldn't be created ", e);
    }
    
    
    Model returnModel = ModelFactory.createDefaultModel();
    ResIterator resIterator = createModel.listSubjectsWithProperty(Omn.isResourceOf);
    while(resIterator.hasNext()){
      Resource resource = resIterator.nextResource();
      Resource res = returnModel.createResource(resource.getURI());
      Property property = returnModel.createProperty(Omn_lifecycle.hasState.getNameSpace(), Omn_lifecycle.hasState.getLocalName());
      property.addProperty(RDF.type, OWL.FunctionalProperty);
      res.addProperty(property, Omn_lifecycle.Uncompleted);
    }
    
     return returnModel;
     
  }

  @Override
  public Model createInstance(String instanceURI, Model createModel) throws ProcessingException, InvalidRequestException {
    return createInstances(createModel);
  }
  
  @Override
  public Model deleteInstances(Model model) throws InvalidRequestException, ProcessingException {
    
    Model deletedInstancesModel = ModelFactory.createDefaultModel();
    
    ResIterator resourceInstanceIterator = model.listSubjectsWithProperty(RDF.type, Omn.Topology);
    while(resourceInstanceIterator.hasNext()){
      String instanceURI = resourceInstanceIterator.next().getURI();
      LOGGER.log(Level.INFO, "Deleting adapterABox: " + instanceURI);
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
      LOGGER.log(Level.INFO, "Updating adapterABox: " + resourceInstance);
      
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
    
      String deleteTopology = this.topologies.get(instanceURI);
      LOGGER.log(Level.INFO, "deleting Topology " + instanceURI + " . Its equivalent URI is " + deleteTopology);
      String id = getLocalname(deleteTopology);
      client.deleteDefinitions(id); 
      this.topologies.remove(instanceURI);
    
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
  public Resource getAdapterABox() {
    return this.adapterABox;
  }
  
  @Override
  public Model getAdapterDescriptionModel() {
    return this.adapterABox.getModel();
  }
  
  @Override
  public void updateAdapterDescription() throws ProcessingException {
  //  LOGGER.log(Level.INFO, "Updating adapter description: Getting types via ToscaClient..");
//    Definitions types = client.getAllTypes();
 //   Model model = parseToModel(types);
    
  //  updateAdapterDescriptionWithModel(model);
  }

  protected void updateAdapterDescriptionWithModel(Model model) {
    List<Resource> resources = model.listSubjectsWithProperty(RDFS.subClassOf, Omn.Resource).toList();
    if(resources.isEmpty()){
      LOGGER.log(Level.WARNING, "Could not find any resources to manage!");
    }
    for(Resource resource : resources){
      LOGGER.log(Level.INFO, "Found resource: "+resource.getURI());
      this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
    }
    this.adapterABox.getModel().add(model);
      this.adapterABox.getModel().setNsPrefixes(model.getNsPrefixMap());
  }

  protected String parseToDefinitions(Model createModel) throws InvalidRequestException {
    try{
      createModel.removeAll(null, Omn.isResourceOf, null);
      createModel.removeAll(null, Omn.hasReservation, null);
      createModel.removeAll(null, Omn_lifecycle.hasState, null);
      createModel.removeAll(null, Omn_lifecycle.implementedBy, null);
      
      System.out.println("CREATE MODEL " + createModel);
      Map<String,String> pref = createModel.getNsPrefixMap();

      InfModel infModel = createInfModel(createModel);
      infModel.setNsPrefix("osco","http://opensdncore.org/ontology/");
      return OMN2Tosca.getTopology(infModel);      
    } catch(InvalidModelException | JAXBException | MultiplePropertyValuesException | RequiredResourceNotFoundException | MultipleNamespacesException e){
      throw new InvalidRequestException(e);
    }
  }
  
  protected Model parseToModel(Definitions definitions) throws ProcessingException {
    try {
      Model resultModel = Tosca2OMN.getModel(definitions);
        this.adapterABox.getModel().setNsPrefixes(resultModel.getNsPrefixMap());
      return resultModel;
    } catch (UnsupportedException e) {
      throw new ProcessingException(e);
    }      
  }
    
  protected String getLocalname(String instanceURI) throws InvalidRequestException {
    try{
      return OntologyModelUtil.getNamespaceAndLocalname(instanceURI,  this.adapterABox.getModel().getNsPrefixMap())[1];
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
    model.add( this.adapterABox.getModel());
    List additionalOntologies = new ArrayList<String>();
    additionalOntologies.add("/ontologies/osco.ttl");
    Parser parser = new Parser(model, additionalOntologies);
    return parser.getInfModel();
  }

@Override
public void refreshConfig() throws ProcessingException {
	// TODO Auto-generated method stub
	
}

    @Override
    public void shutdown() {

    }

    @Override
    public void configure(Config configuration) {

    }

    public void setToscaClient(IToscaClient client) {
    this.client = client;
  }

    public IToscaClient getClient(){
      return this.client;
    }
    
    public ToscaMDBSender getSender(){
      return this.sender;
    }
    
    public Map<String, String> getTopologies(){
      return this.topologies;
    }
    
    public void setTopologies(String requestedTopology, String createdTopology){
      this.topologies.put(requestedTopology, createdTopology);
    }
}
