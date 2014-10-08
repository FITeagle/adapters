package org.fiteagle.adapters.openstack;

import org.fiteagle.api.core.MessageBusOntologyModel;
import static org.junit.Assert.*;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapterTest {
  
  private OpenstackAdapter openstackAdapter = OpenstackAdapter.getInstance();
  
  @Test
  public void testAdapterModel() {
    Model adapterModel = openstackAdapter.getAdapterDescriptionModel();
    Resource adapterType = openstackAdapter.getAdapterType();
    Resource resourceType = openstackAdapter.getAdapterManagedResource();
    assertTrue(adapterModel.contains(adapterType, RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
    assertTrue(adapterModel.contains(resourceType, RDFS.subClassOf, MessageBusOntologyModel.classResource)); 
  }
  
}
