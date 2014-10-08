package org.fiteagle.adapters.openstack;

import org.fiteagle.api.core.MessageBusOntologyModel;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapterTest {
  
  private OpenstackAdapter openstackAdapter = OpenstackAdapter.getInstance();
  
  Model adapterModel = openstackAdapter.getAdapterDescriptionModel();
  private Resource adapterType = openstackAdapter.getAdapterType();
  private Resource adapterInstanceType = openstackAdapter.getAdapterInstance();
  private Resource resourceType = openstackAdapter.getAdapterManagedResource();
  
  @Test
  public void testAdapterModel() {
    assertTrue(adapterModel.contains(adapterType, RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
    assertTrue(adapterModel.contains(adapterType, RDF.type, OWL.Class)); 
  }
  
  @Test
  public void testAdapterResourceModel() {
    assertTrue(adapterModel.contains(resourceType, RDFS.subClassOf, MessageBusOntologyModel.classResource)); 
    assertTrue(adapterModel.contains(resourceType, MessageBusOntologyModel.propertyFiteagleImplementedBy, adapterType)); 
    assertTrue(adapterModel.contains(resourceType, RDF.type, OWL.Class)); 
  }
  
  @Test
  public void testAdapterInstanceModel() {
    assertTrue(adapterModel.contains(adapterInstanceType, RDF.type, adapterType)); 
    assertTrue(adapterModel.contains(adapterInstanceType, RDF.type, adapterType)); 
  }
  
}
