//package org.sshService;
//
//
//import junit.framework.TestCase;
//
//import org.fiteagle.adapters.sshService.SshServiceAdapter;
//
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//
//
//	public class SshServiceTest extends TestCase{	  
//	  private static SshServiceAdapter adapter;
//	  
////	  @BeforeClass
//	  public static void loadModel(){
//		  adapter = new SshServiceAdapter();
//	  }
//	  
////	  @Test
//	  public void testAdapterModel() {
//		  
//		  adapter.testCreateAccess("publickey", "deploytestuser");
//	  }
	  
//	  @Test
//	  public void testAdapterPrefixes() {
//	    assertNotNull(adapter.getAdapterInstance().getNameSpace());
//	    assertNotNull(adapter.getAdapterManagedResources().get(0).getNameSpace());
//	  }
//	  
//	  @Test
//	  public void testAdapterResource() {
//	    assertTrue(adapter.getAdapterManagedResources().get(0).hasProperty(Omn_lifecycle.implementedBy, adapter.getAdapterType()));
//	    assertTrue(adapter.getAdapterManagedResources().get(0).hasProperty(RDFS.subClassOf, Omn.Resource));
//	  }
//
//	  @Test
//	  public void testAdapterType() {
//	      assertTrue(adapter.getAdapterType().hasProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
//	      assertTrue(adapter.getAdapterType().hasProperty(Omn_lifecycle.implements_, adapter.getAdapterManagedResources().get(0)));
//	  }
//	  
//	  @Test
//	  public void testAdapterInstance() {
//	      assertTrue(adapter.getAdapterInstance().hasProperty(RDF.type, adapter.getAdapterType()));
//	      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.comment));
//	      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.label)); 
//	  }
//	  
//	  @Test
//	  public void testCreateInstance() throws InstanceNotFoundException, ProcessingException, InvalidRequestException{
//	    Model modelCreate = ModelFactory.createDefaultModel();
//	    Resource instanceResource = modelCreate.createResource(adapter.getAdapterInstance().getNameSpace()+"server1");
//	    instanceResource.addProperty(RDF.type, adapter.getAdapterManagedResources().get(0));
//	    instanceResource.addProperty(RDF.type,Omn.Resource);
//	    instanceResource.addProperty(adapter.getOpenstackParser().getPROPERTY_IMAGE(), adapter.getAdapterInstance().getNameSpace()+"testImageName");
//	    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME(), "testKeypairName");
//	    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_FLAVOR(), 2);
//	    
//	    adapter.createInstances(modelCreate);
//	    Model instanceModel = adapter.getInstance(adapter.getAdapterInstance().getNameSpace()+"server1");
//	    assertNotNull(instanceModel);
//	    StmtIterator iterator = instanceModel.listStatements(null, RDF.type, adapter.getAdapterManagedResources().get(0));
//	    assertTrue(iterator.hasNext());
//	    Resource server = iterator.next().getSubject();
//	    assertEquals(server.toString(), adapter.getAdapterInstance().getNameSpace()+"server1");
//	    String imageURI = server.getProperty(adapter.getOpenstackParser().getPROPERTY_IMAGE()).getResource().getURI();
//	    assertEquals(imageURI, adapter.getAdapterInstance().getNameSpace()+"testImageName");
//	    String keyPairName = server.getProperty(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME()).getString();
//	    assertEquals(keyPairName, "testKeypairName");
//	    assertFalse(iterator.hasNext());
//	  }
//	  
//	  @Test(expected = InstanceNotFoundException.class)
//	  public void testTerminateInstance() throws InstanceNotFoundException, ProcessingException, InvalidRequestException{
//	    String instanceURI = adapter.getAdapterInstance().getNameSpace()+"server2";
//	    Model modelCreate = ModelFactory.createDefaultModel();
//	    Resource instanceResource = modelCreate.createResource(instanceURI);
//	    instanceResource.addProperty(RDF.type, adapter.getAdapterManagedResources().get(0));
//	    instanceResource.addProperty(RDF.type,Omn.Resource);
//	    instanceResource.addProperty(adapter.getOpenstackParser().getPROPERTY_IMAGE(), adapter.getAdapterInstance().getNameSpace()+"testImageName");
//	    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_KEYPAIRNAME(), "testKeypairName");
//	    instanceResource.addLiteral(adapter.getOpenstackParser().getPROPERTY_FLAVOR(), 2);
//
//	    adapter.createInstances(modelCreate);
//	    adapter.deleteInstance(instanceURI);
//	    adapter.getInstance(instanceURI);
//	  }
//	  
//	  @Test(expected = InstanceNotFoundException.class)
//	  public void testTerminateNonExistingInstance() throws InstanceNotFoundException{
//	    String instanceURI = adapter.getAdapterInstance().getNameSpace()+"server2";
//	    adapter.deleteInstance(instanceURI);
//	  }
	  
//	}

	

