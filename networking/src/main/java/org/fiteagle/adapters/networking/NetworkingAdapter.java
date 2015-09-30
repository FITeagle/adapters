package org.fiteagle.adapters.networking;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.Config;

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

public final class NetworkingAdapter extends AbstractAdapter{
  
  private static final List<Property> networkingControlProperties = new ArrayList<Property>();

  private final HashMap<String, Networking> instanceList = new HashMap<String, Networking>();
  
  public NetworkingAdapter(Model adapterModel, Resource adapterABox) {
    this.uuid = UUID.randomUUID().toString();
    this.adapterTBox = adapterModel;
    this.adapterABox = adapterABox;
    Resource adapterType =getAdapterClass();
    this.adapterABox.addProperty(RDF.type,adapterType);
    this.adapterABox.addProperty(RDFS.label,  this.adapterABox.getLocalName());
    this.adapterABox.addProperty(RDFS.comment, "A networking adapter that can create virtual link among resources.");

    NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
        Resource resource = resourceIterator.next().asResource();

        this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
        this.adapterABox.getModel().add(resource.getModel());
        ResIterator propertiesIterator = adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
        while (propertiesIterator.hasNext()) {
            Property p = adapterTBox.getProperty(propertiesIterator.next().getURI());
            networkingControlProperties.add(p);
        }
    }
    }
  
  @Override
  public Model createInstance(String instanceURI, Model modelCreate) {
    
    Networking networking = new Networking(this, instanceURI);
    instanceList.put(instanceURI, networking);
//    updateInstance(instanceURI, modelCreate);
    return parseToModel(networking);
    
  }
  
  
  Model parseToModel(Networking networking){
    Resource resource = ModelFactory.createDefaultModel().createResource(networking.getLinkName());
    resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
    resource.addProperty(RDF.type, Omn.Resource);
    resource.addProperty(RDFS.label, resource.getLocalName());
    Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),Omn_lifecycle.hasState.getLocalName());
    property.addProperty(RDF.type, OWL.FunctionalProperty);
    resource.addProperty(property, Omn_lifecycle.Ready);
    
   
    return resource.getModel();
  }
  
  @Override
  public Model updateInstance(String instanceURI, Model configureModel) {
    if (instanceList.containsKey(instanceURI)) {
      Networking networking = instanceList.get(instanceURI);
      StmtIterator iter = configureModel.listStatements();
      while(iter.hasNext()){
        networking.updateProperty(iter.next());
      }
      return parseToModel(networking);
    }
    return ModelFactory.createDefaultModel();
  }
  
  @Override
  public void deleteInstance(String instanceURI) {
    Networking link = getInstanceByName(instanceURI);
    link.terminate();
    instanceList.remove(instanceURI);
  }
  
  private Networking getInstanceByName(String instanceURI) {
    return instanceList.get(instanceURI);
    }



  @Override
  public Resource getAdapterABox() {
    return adapterABox;
    }

  @Override
  public Model getAdapterDescriptionModel() {
    return adapterTBox;
    }

  @Override
  public void updateAdapterDescription() {
    
  }
  
  @Override
  public Model getInstance(String instanceURI) throws InstanceNotFoundException {
    Networking networking = instanceList.get(instanceURI);
    if(networking == null){
      throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
  }
  return parseToModel(networking);
  }
  
  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
      Model model = ModelFactory.createDefaultModel();
      for(String uri : instanceList.keySet()){
          model.add(getInstance(uri));
      }
      return model;
  }

  @Override
  public void refreshConfig() throws ProcessingException {
      // TODO Auto-generated method stub

  }

  @Override
  public String getId() {
      return this.uuid;
  }

  @Override
  public void shutdown() {

  }

  @Override
  public void configure(Config configuration) {

  }
}
