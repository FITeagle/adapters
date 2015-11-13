package org.fiteagle.adapters.ACSclient;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.adapters.ACSclient.model.Parameter;
import org.fiteagle.adapters.ACSclient.model.ParameterPlusValuesMap;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ACSclientAdapter extends AbstractAdapter{ 
  
  private static final List<Property> ACS_CTRL_PROPS = new ArrayList<Property>();
  
  private transient final HashMap<String, ACSclient> instanceList = new HashMap<String, ACSclient>();
  
  private static final Logger LOGGER = Logger.getLogger(ACSclient.class.toString());
  
  private final String url;
  
  private final String device;
  
  private final List<Parameter> parametersList;
  
  private static final String DEVICE_PARAMETERS = "/parameters/?device=";
  
  private final String powerParameter = "Device.Services.FAPService.1.CellConfig.LTE.RAN.PHY.ULPowerControl.P0NominalPUCCH";
  
  private final HashMap<String, ParameterPlusValuesMap> parametersNames = new HashMap<String, ParameterPlusValuesMap>();
  
  
  public ACSclientAdapter(final Model adapterModel, final Resource adapterABox) {
    
    super();
    this.uuid = UUID.randomUUID().toString();
    this.adapterTBox = adapterModel;
    
    this.url = parseConfig(adapterABox, "url");
    this.device = parseConfig(adapterABox, "device");
    
    Model model = ModelFactory.createDefaultModel();
    Resource adapterInstance = model.createResource(adapterABox.getURI());
    
    this.adapterABox = adapterInstance;
    final Resource adapterType = this.getAdapterClass();
    this.adapterABox.addProperty(RDF.type, adapterType);
    this.adapterABox.addProperty(RDFS.label, this.adapterABox.getLocalName());
    this.adapterABox.addProperty(RDFS.comment,
      "A client for Auto Configuration Server");


    final NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
        final Resource resource = resourceIterator.next().asResource();

        this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
        this.adapterABox.getModel().add(resource.getModel());
        final ResIterator propIterator = this.adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
        while (propIterator.hasNext()) {
          final Property property = this.adapterTBox.getProperty(propIterator.next().getURI());
          ACSclientAdapter.ACS_CTRL_PROPS.add(property);
        }
    }

    
    // call REST API to get parameters
    this.parametersList = getDeviceParameters();
    System.out.println("PARAMETER SIZE IS " + parametersList.size());
    
    setDefaultParameters();
  }
  

  @Override
  public Model createInstance(final String instanceURI, final Model modelCreate) {
    
    String createModel = MessageUtil.serializeModel(modelCreate, IMessageBus.SERIALIZATION_TURTLE);
    System.out.println("ACS create model \n" + createModel);
    
    final ACSclient acs_client = new ACSclient(this, instanceURI);
    acs_client.parseConfigureModel(modelCreate);
    
    this.instanceList.put(instanceURI, acs_client);
    
    try{
      ManagedThreadFactory managedThreadFactory = (ManagedThreadFactory) new InitialContext().lookup("java:jboss/ee/concurrency/factory/default");
      Thread acsThread = managedThreadFactory.newThread(acs_client);
      acsThread.start();
    } catch (NamingException e) {
      LOGGER.log(Level.SEVERE, "Thread couldn't be created ", e);
    }
    return this.parseToModel(acs_client);
    
  }
  
  Model parseToModel(ACSclient acs_client){
    Resource resource = ModelFactory.createDefaultModel().createResource(acs_client.getInstanceName());
    resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
    resource.addProperty(RDF.type, Omn.Resource);
    resource.addProperty(RDFS.label, resource.getLocalName());
    Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),Omn_lifecycle.hasState.getLocalName());
    property.addProperty(RDF.type, OWL.FunctionalProperty);
    resource.addProperty(property, Omn_lifecycle.Ready);
    
   
    return resource.getModel();
  }
  
  @Override
  @SuppressWarnings({ "PMD.GuardLogStatement", "PMD.GuardLogStatementJavaUtil" })
  public Model updateInstance(final String instanceURI, final Model configureModel) {
    return ModelFactory.createDefaultModel();
  }
  
  
  @Override
  public void deleteInstance(final String instanceURI) {
    ACSclient acs_client = this.getInstanceByName(instanceURI);
    acs_client.terminate();
    instanceList.remove(instanceURI);
  }
  
  private ACSclient getInstanceByName(final String instanceURI) {
    return this.instanceList.get(instanceURI);
      }

  @Override
  public Resource getAdapterABox() {
    return this.adapterABox;
    }
  
  @Override
  public Model getAdapterDescriptionModel() {
    return this.adapterTBox;
  }
  
  @Override
  public void updateAdapterDescription() {
LOGGER.warning("Not implemented.");
  }

  @Override
  public Model getInstance(final String instanceURI) throws InstanceNotFoundException {
    final ACSclient asc_client = this.instanceList.get(instanceURI);
    if(asc_client == null){
      throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
  }
  return parseToModel(asc_client);
  }
  
  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
    final Model model = ModelFactory.createDefaultModel();
    for (final String uri : this.instanceList.keySet()) {
      model.add(this.getInstance(uri));
      }
    return model;
  }

  @Override
  public void refreshConfig() throws ProcessingException {
    LOGGER.warning("Not implemented.");
  }

  @Override
  public String getId() {
    return this.uuid;
  }

  @Override
  public void shutdown() {
    LOGGER.warning("Not implemented.");
  }

  @Override
  @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
  public void configure(final Config configuration) {
    LOGGER.warning("Not implemented. Input: " + configuration);
  }
  
  private String parseConfig(Resource resource, String parameter){
    Model model = ModelFactory.createDefaultModel();
    return resource.getProperty(model.createProperty(Omn_service.getURI(), parameter)).getLiteral().getString();
  }
  
  protected String getURL(){
    return this.url;
  }
  
  protected String getDevice(){
    return this.device;
  }
  
  private List<Parameter> getDeviceParameters(){
    Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    WebTarget target = client.target(this.url+this.DEVICE_PARAMETERS+getDevice());
    List<Parameter> parameters = target.request().get(new GenericType<List<Parameter>>(){});
    return parameters;
  }
  
  private void setDefaultParameters(){
    
    // look for the parameter and put in the Map
    
    ParameterPlusValuesMap parameterPlusValuesMap = new ParameterPlusValuesMap();
    parameterPlusValuesMap.setValue("on", "1");
    parameterPlusValuesMap.setValue("off", "0");
    
    for(Parameter parameter : parametersList){
      if(this.powerParameter.equals(parameter.getName())){
        parameterPlusValuesMap.setParameter(parameter);
        break;
        }
      }
    this.parametersNames.put("power", parameterPlusValuesMap);
  }
  
  public HashMap<String, ParameterPlusValuesMap> getParametersNames(){
    return this.parametersNames;
  }
  
 
  
}
