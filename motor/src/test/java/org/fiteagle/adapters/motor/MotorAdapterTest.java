package org.fiteagle.adapters.motor;

import java.util.Iterator;

import org.fiteagle.api.core.IMessageBus;
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
  public void testCreateAndTerminate() {
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource motor = modelCreate.createResource(adapter.getAdapterInstancePrefix()[1]+"InstanceOne");
    motor.addProperty(RDF.type, adapter.getAdapterManagedResource());
    Property propertyRPM = modelCreate.createProperty(adapter.getAdapterManagedResourcePrefix()[1]+"rpm");
    motor.addLiteral(propertyRPM, 42);
    
    // Creating first instance works
    Assert.assertTrue(adapter.createInstance("InstanceOne", modelCreate));
    // Creating another instance with the same name FAILS
    Assert.assertFalse(adapter.createInstance("InstanceOne", modelCreate));
    // Creating another instance with another name works fine
    Assert.assertTrue(adapter.createInstance("InstanceTwo", modelCreate));
    
    Resource updatedResource = adapter.getAdapterDescriptionModel().getResource(adapter.getAdapterInstancePrefix()[1]+"InstanceOne");
    Assert.assertEquals(42, updatedResource.getProperty(propertyRPM).getInt());

    Assert.assertEquals(2, adapter.getAmountOfInstances());
    
    Assert.assertTrue(adapter.terminateInstance("InstanceOne"));
    Assert.assertFalse(adapter.terminateInstance("InstanceOne"));
    Assert.assertTrue(adapter.terminateInstance("InstanceTwo"));
  }
  
  @Test
  public void testMonitor() {
    
    String instanceName = "InstanceOne";
    
    // Monitor non-existing instance yields empty String
    Assert.assertEquals("", adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT));
    // create the instance
    Model modelCreate = ModelFactory.createDefaultModel();
    adapter.createInstance(instanceName, modelCreate);
    
    // Monitoring existing instance yields a non-empty result
    String monitorData = adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT);
    Assert.assertNotEquals("", monitorData);
    
    // Monitoring data contains the instance name
    Assert.assertTrue(monitorData.contains(instanceName));
   
    // Monitoring data contains the adapter specific prefix
    Assert.assertTrue(monitorData.contains(adapter.getAdapterManagedResourcePrefix()[1]));
    
    // release instances
    Assert.assertTrue(adapter.terminateInstance(instanceName));
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
  public void testConfigure() {
    String instanceName = "InstanceOne";
    
    Model modelCreate = ModelFactory.createDefaultModel();
    adapter.createInstance(instanceName, modelCreate);

    Model modelConfigure = ModelFactory.createDefaultModel();
    Resource motor = modelConfigure.createResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
    motor.addProperty(RDF.type, adapter.getAdapterManagedResource());
    Property propertyRPM = modelConfigure.createProperty(adapter.getAdapterManagedResourcePrefix()[1]+"rpm");
    motor.addLiteral(propertyRPM, 23);
    Property propertyManufacturer = modelConfigure.createProperty(adapter.getAdapterManagedResourcePrefix()[1]+"manufacturer");
    motor.addLiteral(propertyManufacturer, "TU Berlin");
    
    Model updatedResourceModel = null;
    StmtIterator iter = modelConfigure.listStatements();
    while(iter.hasNext()){
      updatedResourceModel = adapter.configureInstance(iter.next());
    }
    Resource updatedResource = updatedResourceModel.getResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
    Assert.assertEquals(23, updatedResource.getProperty(propertyRPM).getInt());
    Assert.assertEquals("TU Berlin", updatedResource.getProperty(propertyManufacturer).getString());
    
    Assert.assertTrue(adapter.terminateInstance(instanceName));
  }
  
}
