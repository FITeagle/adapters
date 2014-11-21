package org.fiteagle.adapters.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.fiteagle.adapters.openstack.client.OpenstackTestClient;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapterTest {
  
  private static OpenstackAdapter adapter;
  
  @BeforeClass
  public static void loadModel(){
    adapter = OpenstackAdapter.getInstance(OpenstackTestClient.getInstance());
  }
  
  @Test
  public void testAdapterModel() {
    assertTrue(adapter.getAdapterDescriptionModel().contains(adapter.getAdapterType(), RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
  }
  
  @Test
  public void testAdapterPrefixes() {
    assertNotNull(adapter.getAdapterSpecificPrefix()[0]);
    assertNotNull(adapter.getAdapterManagedResourcePrefix()[0]);
    assertNotNull(adapter.getAdapterInstancePrefix()[0]);
    assertNotNull(adapter.getAdapterSpecificPrefix()[1]);
    assertNotNull(adapter.getAdapterManagedResourcePrefix()[1]);
    assertNotNull(adapter.getAdapterInstancePrefix()[1]);
  }
  
  @Test
  public void testAdapterResource() {
    assertTrue(adapter.getAdapterManagedResource().hasProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapter.getAdapterType()));
    assertTrue(adapter.getAdapterManagedResource().hasProperty(RDFS.subClassOf, MessageBusOntologyModel.classResource));
  }

  @Test
  public void testAdapterType() {
      assertTrue(adapter.getAdapterType().hasProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
      assertTrue(adapter.getAdapterType().hasProperty(MessageBusOntologyModel.propertyFiteagleImplements, adapter.getAdapterManagedResource()));
  }
  
  @Test
  public void testAdapterInstance() {
      assertTrue(adapter.getAdapterInstance().hasProperty(RDF.type, adapter.getAdapterType()));
      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.comment));
      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.label)); 
  }
  
  @Test
  public void testCreateInstance(){
    Map<String, String> properties = new HashMap<>();
    properties.put(adapter.getOpenstackParser().getPROPERTY_IMAGE().getURI(), adapter.getAdapterInstancePrefix()[1]+"testImageName");
    properties.put(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME().getURI(), adapter.getAdapterInstancePrefix()[1]+"testKeypairName");
    
    adapter.createInstance("server1", properties);
    Model instanceModel = adapter.getSingleInstanceModel("server1");
    assertNotNull(instanceModel);
    StmtIterator iterator = instanceModel.listStatements(null, RDF.type, adapter.getAdapterManagedResource());
    assertTrue(iterator.hasNext());
    Resource server = iterator.next().getSubject();
    assertEquals(server.toString(), adapter.getAdapterInstancePrefix()[1]+"server1");
    String imageURI = server.getProperty((adapter.getOpenstackParser().getPROPERTY_IMAGE())).getResource().getURI();
    assertEquals(imageURI, adapter.getAdapterInstancePrefix()[1]+"testImageName");
    String keyPairName = server.getProperty((adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME())).getString();
    assertEquals(keyPairName, adapter.getAdapterInstancePrefix()[1]+"testKeypairName");
    assertFalse(iterator.hasNext());
  }
  
  @Test
  public void testTerminateInstance(){
    Map<String, String> properties = new HashMap<>();
    properties.put(adapter.getOpenstackParser().getPROPERTY_IMAGE().getURI(), adapter.getAdapterInstancePrefix()[1]+"testImageName");
    properties.put(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME().getURI(), adapter.getAdapterInstancePrefix()[1]+"testKeypairName");
    
    adapter.createInstance("server1", properties);
    adapter.terminateInstance("server1");
    Model instanceModel = adapter.getSingleInstanceModel("server1");
    assertNull(instanceModel);
    StmtIterator iterator = adapter.getAllInstancesModel().listStatements();
    assertFalse(iterator.hasNext());
  }
  
}
