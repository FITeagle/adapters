/*
 * package org.fiteagle.adapters.motor;
 * 
 * import com.hp.hpl.jena.rdf.model.*; import
 * com.hp.hpl.jena.rdf.model.impl.StatementImpl; import
 * com.hp.hpl.jena.vocabulary.RDFS; import
 * info.openmultinet.ontology.vocabulary.Omn;
 * 
 * import org.easymock.EasyMock; import
 * org.fiteagle.abstractAdapter.AbstractAdapter; import
 * org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
 * import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
 * import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
 * import org.fiteagle.api.core.MessageBusOntologyModel; import
 * org.junit.Assert; import org.junit.BeforeClass; import org.junit.Test;
 * 
 * import com.hp.hpl.jena.vocabulary.RDF;
 * 
 * public class MotorAdapterTest {
 * 
 * private static AbstractAdapter adapter;
 * 
 * @BeforeClass public static void setup() { Model adapterTBOX =
 * EasyMock.createMock(Model.class); ResIterator resIterator =
 * EasyMock.createMock(ResIterator.class);
 * EasyMock.expect(resIterator.hasNext()).andReturn(true).once();
 * EasyMock.expect(resIterator.nextResource()).andReturn(MessageBusOntologyModel
 * .classAdapter).anyTimes();
 * EasyMock.expect(adapterTBOX.listResourcesWithProperty(RDFS.subClassOf,
 * MessageBusOntologyModel.classAdapter)).andReturn(resIterator).anyTimes();
 * Property longP = EasyMock.createMock(Property.class); Property latP =
 * EasyMock.createMock(Property.class);
 * 
 * EasyMock.expect(adapterTBOX.createProperty(
 * "http://www.w3.org/2003/01/geo/wgs84_pos#long")).andReturn(longP).anyTimes();
 * EasyMock.expect(adapterTBOX.createProperty(
 * "http://www.w3.org/2003/01/geo/wgs84_pos#lat")).andReturn(latP).anyTimes();
 * 
 * Resource adapterABox = EasyMock.createMock(Resource.class);
 * EasyMock.expect(adapterABox.getNameSpace()).andReturn("http://localhost").
 * anyTimes();
 * EasyMock.expect(adapterABox.addProperty(EasyMock.anyObject(Property.class),
 * EasyMock.anyObject(Resource.class))).andReturn(adapterABox).anyTimes();
 * EasyMock.expect(adapterABox.addProperty(EasyMock.anyObject(Property.class),
 * EasyMock.anyObject(String.class))).andReturn(adapterABox).anyTimes();
 * EasyMock.expect(adapterABox.getLocalName()).andReturn("InstanceOne").anyTimes
 * ();
 * EasyMock.expect(adapterABox.addLiteral(EasyMock.anyObject(Property.class),
 * EasyMock.anyInt())).andReturn(adapterABox).anyTimes();
 * 
 * StmtIterator stmtIterator = EasyMock.createMock(StmtIterator.class);
 * EasyMock.expect(stmtIterator.hasNext()).andReturn(true).once(); Statement
 * statement = EasyMock.createMock(Statement.class); RDFNode rdfNode =
 * EasyMock.createMock(RDFNode.class);
 * 
 * Resource resource = EasyMock.createMock(Resource.class);
 * EasyMock.expect(rdfNode.asResource()).andReturn(resource).anyTimes();
 * EasyMock.expect(stmtIterator.next()).andReturn(statement);
 * EasyMock.expect(statement.getObject()).andReturn(rdfNode).anyTimes();
 * EasyMock.expect(adapterABox.listProperties(EasyMock.anyObject(Property.class)
 * )).andReturn(stmtIterator).anyTimes();
 * EasyMock.expect(adapterTBOX.listSubjectsWithProperty(RDFS.domain,resource)).
 * andReturn(EasyMock.createMock(ResIterator.class));
 * EasyMock.expect(adapterTBOX.listStatements(EasyMock.anyObject(Resource.class)
 * ,EasyMock.anyObject(Property.class),EasyMock.anyObject(RDFNode.class))).
 * andReturn(EasyMock.createMock(StmtIterator.class)).anyTimes();
 * EasyMock.replay(adapterABox, adapterTBOX,
 * resIterator,stmtIterator,statement,rdfNode); adapter = new
 * MotorAdapter(adapterTBOX,adapterABox); }
 * 
 * @Test public void testCreateAndTerminate() throws InstanceNotFoundException,
 * ProcessingException, InvalidRequestException { String instanceURI =
 * adapter.getAdapterABox().getNameSpace()+"InstanceOne";
 * 
 * Model modelCreate = ModelFactory.createDefaultModel(); Resource motor =
 * modelCreate.createResource(instanceURI); motor.addProperty(RDF.type,
 * adapter.getAdapterManagedResources().get(0)); Property propertyRPM =
 * modelCreate.createProperty(adapter.getAdapterManagedResources().get(0).
 * getNameSpace()+"rpm"); motor.addLiteral(propertyRPM, 42);
 * motor.addProperty(RDF.type, Omn.Resource);
 * adapter.createInstances(modelCreate);
 * 
 * Model createdResourceModel = adapter.getInstance(instanceURI); Resource
 * resource = createdResourceModel.getResource(instanceURI);
 * Assert.assertEquals(42, resource.getProperty(propertyRPM).getInt());
 * 
 * adapter.deleteInstances(modelCreate); StmtIterator iter =
 * adapter.getAllInstances().listStatements();
 * Assert.assertFalse(iter.hasNext()); }
 * 
 * @Test(expected=InstanceNotFoundException.class) public void
 * testGetNonExistingInstance() throws InstanceNotFoundException,
 * ProcessingException, InvalidRequestException{ String instanceURI =
 * adapter.getAdapterABox().getNameSpace()+"InstanceOne";
 * adapter.getInstance(instanceURI); }
 * 
 * @Test public void testMonitor() throws InstanceNotFoundException,
 * ProcessingException, InvalidRequestException { String instanceURI =
 * adapter.getAdapterABox().getNameSpace()+"InstanceOne";
 * 
 * Model modelCreate = ModelFactory.createDefaultModel(); Resource motorResource
 * = modelCreate.createResource(instanceURI);
 * motorResource.addProperty(RDF.type,
 * adapter.getAdapterManagedResources().get(0));
 * motorResource.addProperty(RDF.type, Omn.Resource);
 * adapter.createInstances(modelCreate);
 * 
 * Model monitorData = adapter.getInstance(instanceURI);
 * Assert.assertFalse(monitorData.isEmpty());
 * Assert.assertTrue(monitorData.containsAll(modelCreate));
 * 
 * adapter.deleteInstance(instanceURI); }
 * 
 * @Test public void testGetters() {
 * Assert.assertNotNull(adapter.getAdapterManagedResources().get(0));
 * Assert.assertTrue(adapter.getAdapterManagedResources().get(0) instanceof
 * Resource); Assert.assertNotNull(adapter);
 * Assert.assertNotNull(adapter.getAdapterDescriptionModel());
 * Assert.assertTrue(adapter.getAdapterDescriptionModel() instanceof Model); }
 * 
 * @Test public void testConfigure() throws InstanceNotFoundException,
 * ProcessingException, InvalidRequestException { String instanceURI =
 * adapter.getAdapterABox().getNameSpace()+"InstanceOne";
 * 
 * Model modelCreate = ModelFactory.createDefaultModel(); Resource motorResource
 * = modelCreate.createResource(instanceURI);
 * motorResource.addProperty(RDF.type,
 * adapter.getAdapterManagedResources().get(0));
 * motorResource.addProperty(RDF.type, Omn.Resource);
 * adapter.createInstances(modelCreate);
 * 
 * Model modelConfigure = ModelFactory.createDefaultModel(); Resource motor =
 * modelConfigure.createResource(instanceURI); motor.addProperty(RDF.type,
 * adapter.getAdapterManagedResources().get(0)); Property propertyRPM =
 * modelConfigure.createProperty(adapter.getAdapterManagedResources().get(0).
 * getNameSpace()+"rpm"); motor.addLiteral(propertyRPM, 23); Property
 * propertyManufacturer =
 * modelConfigure.createProperty(adapter.getAdapterManagedResources().get(0).
 * getNameSpace()+"manufacturer"); motor.addLiteral(propertyManufacturer,
 * "TU Berlin");
 * 
 * Model updatedResourceModel = adapter.updateInstances(modelConfigure);
 * 
 * Resource updatedResource = updatedResourceModel.getResource(instanceURI);
 * Assert.assertEquals(23, updatedResource.getProperty(propertyRPM).getInt());
 * Assert.assertEquals("TU Berlin",
 * updatedResource.getProperty(propertyManufacturer).getString());
 * 
 * adapter.deleteInstance(instanceURI); }
 * 
 * }
 */
