package org.fiteagle.adapters.Attenuator;

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
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AttenuatorAdapter extends AbstractAdapter{
  
private static final List<Property> ATTENUATOR_CTRL_PROPS = new ArrayList<Property>();
  
  private transient final HashMap<String, AttenuatorSeter> instanceList = new HashMap<String, AttenuatorSeter>();
  
  private static final Logger LOGGER = Logger.getLogger(AttenuatorSeter.class.toString());
  
  private Attenuator attenuator;
  

public AttenuatorAdapter(final Model adapterModel, final Resource adapterABox) {
    
    super();
    this.uuid = UUID.randomUUID().toString();
    this.adapterTBox = adapterModel;
    
    this.attenuator = new Attenuator();
    this.attenuator.set_attenuator_url(parseConfig(adapterABox, "attenuator_url"));
    this.attenuator.set_attenuator_port(parseConfig(adapterABox, "attenuator_port"));
    this.attenuator.set_attenuator_id(parseConfig(adapterABox, "attenuator_id"));
    
    Model model = ModelFactory.createDefaultModel();
    Resource adapterInstance = model.createResource(adapterABox.getURI());
    
    this.adapterABox = adapterInstance;
    final Resource adapterType = this.getAdapterClass();
    this.adapterABox.addProperty(RDF.type, adapterType);
    this.adapterABox.addProperty(RDFS.label, this.adapterABox.getLocalName());
    this.adapterABox.addProperty(RDFS.comment,
      "Attenuator");


    final NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
        final Resource resource = resourceIterator.next().asResource();

        this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
        this.adapterABox.getModel().add(resource.getModel());
        final ResIterator propIterator = this.adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
        while (propIterator.hasNext()) {
          final Property property = this.adapterTBox.getProperty(propIterator.next().getURI());
          AttenuatorAdapter.ATTENUATOR_CTRL_PROPS.add(property);
        }
    }
    
}
  

  @Override
  public Model createInstance(final String instanceURI, final Model modelCreate) {
    
    String createModel = MessageUtil.serializeModel(modelCreate, IMessageBus.SERIALIZATION_TURTLE);
    System.out.println("Attenuator create model \n" + createModel);
    
    return this.parseToModel(instanceURI);
    
  }
  
  Model parseToModel(final String instanceURI){
    Resource resource = ModelFactory.createDefaultModel().createResource(instanceURI);
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
    
    String confModel = MessageUtil.serializeModel(configureModel, IMessageBus.SERIALIZATION_TURTLE);
    System.out.println("Attenuator Configure model \n" + confModel);
    
    AttenuatorSeter attenuatorSeter = new AttenuatorSeter(this.attenuator, configureModel);
    
    try{
      ManagedThreadFactory managedThreadFactory = (ManagedThreadFactory) new InitialContext().lookup("java:jboss/ee/concurrency/factory/default");
      Thread attenuatorThread = managedThreadFactory.newThread(attenuatorSeter);
      attenuatorThread.start();
    } catch (NamingException e) {
      LOGGER.log(Level.SEVERE, "Thread couldn't be created ", e);
    }
    return this.parseToModel(instanceURI);
    
  }
  
  
  @Override
  public void deleteInstance(final String instanceURI) {
  }
  
  private AttenuatorSeter getInstanceByName(final String instanceURI) {
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
    final AttenuatorSeter attenuator = this.instanceList.get(instanceURI);
    if(attenuator == null){
      throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
  }
  return parseToModel(instanceURI);
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
  
  
  
}
