package org.fiteagle.adapters.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.adapters.openstack.client.OpenstackTestClient;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapterTest {
  
  private static OpenstackAdapter adapter;
  
  @BeforeClass
  public static void loadModel(){
    adapter = OpenstackAdapter.getTestInstance(OpenstackTestClient.getInstance());
  }
  
  @Test
  public void testAdapterModel() {
    assertTrue(adapter.getAdapterDescriptionModel().contains(adapter.getAdapterType(), RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
  }
  
  @Test
  public void testAdapterPrefixes() {
    assertNotNull(adapter.getAdapterInstance().getNameSpace());
    assertNotNull(adapter.getAdapterManagedResource().getNameSpace());
  }
  
  @Test
  public void testAdapterResource() {
    assertTrue(adapter.getAdapterManagedResource().hasProperty(Omn_lifecycle.implementedBy, adapter.getAdapterType()));
    assertTrue(adapter.getAdapterManagedResource().hasProperty(RDFS.subClassOf, Omn.Resource));
  }

  @Test
  public void testAdapterType() {
      assertTrue(adapter.getAdapterType().hasProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
      assertTrue(adapter.getAdapterType().hasProperty(Omn_lifecycle.implements_, adapter.getAdapterManagedResource()));
  }
  
  @Test
  public void testAdapterInstance() {
      assertTrue(adapter.getAdapterInstance().hasProperty(RDF.type, adapter.getAdapterType()));
      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.comment));
      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.label)); 
  }
  
  @Test
  public void testCreateInstance() throws AdapterException, InstanceNotFoundException{
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource instanceResource = modelCreate.createResource(adapter.getAdapterInstance().getNameSpace()+"server1");
    instanceResource.addProperty(RDF.type, adapter.getAdapterManagedResource());
    instanceResource.addProperty(adapter.getOpenstackParser().getPROPERTY_IMAGE(), adapter.getAdapterInstance().getNameSpace()+"testImageName");
    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME(), "testKeypairName");
    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_FLAVOR(), 2);
    
    adapter.createInstances(modelCreate);
    Model instanceModel = adapter.getInstance(adapter.getAdapterInstance().getNameSpace()+"server1");
    assertNotNull(instanceModel);
    StmtIterator iterator = instanceModel.listStatements(null, RDF.type, adapter.getAdapterManagedResource());
    assertTrue(iterator.hasNext());
    Resource server = iterator.next().getSubject();
    assertEquals(server.toString(), adapter.getAdapterInstance().getNameSpace()+"server1");
    String imageURI = server.getProperty(adapter.getOpenstackParser().getPROPERTY_IMAGE()).getResource().getURI();
    assertEquals(imageURI, adapter.getAdapterInstance().getNameSpace()+"testImageName");
    String keyPairName = server.getProperty(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME()).getString();
    assertEquals(keyPairName, "testKeypairName");
    assertFalse(iterator.hasNext());
  }
  
  @Test(expected = InstanceNotFoundException.class)
  public void testTerminateInstance() throws AdapterException, InstanceNotFoundException{
    String instanceURI = adapter.getAdapterInstance().getNameSpace()+"server2";
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource instanceResource = modelCreate.createResource(instanceURI);
    instanceResource.addProperty(RDF.type, adapter.getAdapterManagedResource());
    instanceResource.addProperty(adapter.getOpenstackParser().getPROPERTY_IMAGE(), adapter.getAdapterInstance().getNameSpace()+"testImageName");
    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME(), "testKeypairName");
    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_FLAVOR(), 2);
    
    adapter.createInstances(modelCreate);
    adapter.deleteInstance(instanceURI);
    adapter.getInstance(instanceURI);
  }
  
  @Test(expected = InstanceNotFoundException.class)
  public void testTerminateNonExistingInstance() throws AdapterException, InstanceNotFoundException{
    String instanceURI = adapter.getAdapterInstance().getNameSpace()+"server2";
    adapter.deleteInstance(instanceURI);
  }
  
}
