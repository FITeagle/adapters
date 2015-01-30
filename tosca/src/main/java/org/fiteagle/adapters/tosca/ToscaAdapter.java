package org.fiteagle.adapters.tosca;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.MultipleNamespacesException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.MultiplePropertyValuesException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.RequiredResourceNotFoundException;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN.UnsupportedException;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
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
    Resource adapterInstance = model.createResource("http://federation.av.tu-berlin.de/about#Tosca-1");
    adapterInstance.addProperty(RDF.type, adapter);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "An adapter for TOSCA-compliant resources");
    Resource testbed = model.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);
    new ToscaAdapter(adapterInstance, model, new ToscaClient("http://localhost:8080/api/rest/tosca/v2/definitions"));
  }
  
  private ToscaAdapter(Resource adapterInstance, Model adapterModel, ToscaClient client) {
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterModel;
    this.client = client;
    
    adapterInstances.put(adapterInstance.getURI(), this);
  }
  
  @Override
  public Model createInstances(Model createModel){
    String definitions = null;
    try {
      definitions = OMN2Tosca.getTopology(createModel);
    } catch (JAXBException | InvalidModelException | MultipleNamespacesException | RequiredResourceNotFoundException
        | MultiplePropertyValuesException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    
    InputStream resultStream = new ByteArrayInputStream(definitions.getBytes());
    Model resultModel = null;
    try {
      resultModel = Tosca2OMN.getModel(resultStream);
    } catch (JAXBException | InvalidModelException | UnsupportedException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    
    return resultModel;
  }
  
  @Override
  public Model createInstance(String instanceURI, Model createModel) {
    return null;
  }
  
  @Override
  public Model configureInstance(String instanceURI, Model configureModel) {
    return null;
  }
  
  @Override
  public void deleteInstance(String instanceURI) {
    return;
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
    throw new InstanceNotFoundException();
  }

  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
    InputStream definitions = client.getDefinitionsStream();
    Model model = null;
    try {
      model = Tosca2OMN.getModel(definitions);
    } catch (JAXBException | InvalidModelException | UnsupportedException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return model;
  }
  
}
