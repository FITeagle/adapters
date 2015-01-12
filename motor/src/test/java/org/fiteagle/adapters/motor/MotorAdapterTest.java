package org.fiteagle.adapters.motor;

import java.util.Iterator;

import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class MotorAdapterTest {
  
  private static MotorAdapter adapter;
  
  @BeforeClass
  public static void setup() {
    Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
    if (iterator.hasNext()) {
      adapter = MotorAdapter.getInstance(iterator.next());
    }
  }
  
  @Test
  public void testCreateAndTerminate() throws AdapterException {
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource motor = modelCreate.createResource(adapter.getAdapterInstancePrefix()[1]+"InstanceOne");
    motor.addProperty(RDF.type, adapter.getAdapterManagedResource());
    Property propertyRPM = modelCreate.createProperty(adapter.getAdapterManagedResourcePrefix()[1]+"rpm");
    motor.addLiteral(propertyRPM, 42);
    
    adapter.createInstances(modelCreate);
    
    Resource updatedResource = adapter.getAdapterDescriptionModel().getResource(adapter.getAdapterInstancePrefix()[1]+"InstanceOne");
    Assert.assertEquals(42, updatedResource.getProperty(propertyRPM).getInt());

    adapter.deleteInstances(modelCreate);
    StmtIterator iter = adapter.getAllInstancesModel().listStatements();
    Assert.assertFalse(iter.hasNext());
  }
  
  @Test
  public void testMonitor() throws AdapterException {
    String instanceName = "InstanceOne";    
    Assert.assertTrue(adapter.getInstanceModel(instanceName) == null);
    
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource motorResource = modelCreate.createResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
    motorResource.addProperty(RDF.type, adapter.getAdapterManagedResource());
    adapter.createInstances(modelCreate);
    
    Model monitorData = adapter.getInstanceModel(instanceName);
    Assert.assertFalse(monitorData.isEmpty());
    Assert.assertTrue(monitorData.containsAll(modelCreate));
    
    adapter.deleteInstance(instanceName);
  }
  
  @Test
  public void testGetters() {
    // Getting Adapter Managed Resource must be implemented and return actual data
    Assert.assertNotNull(adapter.getAdapterManagedResource());
    Assert.assertTrue(adapter.getAdapterManagedResource() instanceof Resource);
    // Same for Getting Adapter Instance
    Assert.assertNotNull(adapter.getAdapterInstance());
    Assert.assertTrue(adapter.getAdapterInstance() instanceof Resource);
    // And Adapter Type
    Assert.assertNotNull(adapter.getAdapterType());
    Assert.assertTrue(adapter.getAdapterInstance() instanceof Resource);
    // And Adapter DescriptionModel
    Assert.assertNotNull(adapter.getAdapterDescriptionModel());
    Assert.assertTrue(adapter.getAdapterDescriptionModel() instanceof Model);
  }
  
  @Test
  public void testConfigure() throws AdapterException {
    String instanceName = "InstanceOne";
    
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource motorResource = modelCreate.createResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
    motorResource.addProperty(RDF.type, adapter.getAdapterManagedResource());
    adapter.createInstances(modelCreate);

    Model modelConfigure = ModelFactory.createDefaultModel();
    Resource motor = modelConfigure.createResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
    motor.addProperty(RDF.type, adapter.getAdapterManagedResource());
    Property propertyRPM = modelConfigure.createProperty(adapter.getAdapterManagedResourcePrefix()[1]+"rpm");
    motor.addLiteral(propertyRPM, 23);
    Property propertyManufacturer = modelConfigure.createProperty(adapter.getAdapterManagedResourcePrefix()[1]+"manufacturer");
    motor.addLiteral(propertyManufacturer, "TU Berlin");
    
    Model updatedResourceModel = adapter.configureInstances(modelConfigure);
    
    Resource updatedResource = updatedResourceModel.getResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
    Assert.assertEquals(23, updatedResource.getProperty(propertyRPM).getInt());
    Assert.assertEquals("TU Berlin", updatedResource.getProperty(propertyManufacturer).getString());
    
    adapter.deleteInstance(instanceName);
  }
  
}
