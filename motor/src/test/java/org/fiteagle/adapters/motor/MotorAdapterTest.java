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
    Resource motor = modelCreate.createResource(adapter.getAdapterInstance().getNameSpace()+"InstanceOne");
    motor.addProperty(RDF.type, adapter.getAdapterManagedResource());
    Property propertyRPM = modelCreate.createProperty(adapter.getAdapterManagedResource().getNameSpace()+"rpm");
    motor.addLiteral(propertyRPM, 42);
    
    adapter.createInstances(modelCreate);
    
    Resource updatedResource = adapter.getAdapterDescriptionModel().getResource(adapter.getAdapterInstance().getNameSpace()+"InstanceOne");
    Assert.assertEquals(42, updatedResource.getProperty(propertyRPM).getInt());

    adapter.deleteInstances(modelCreate);
    StmtIterator iter = adapter.getAllInstancesModel().listStatements();
    Assert.assertFalse(iter.hasNext());
  }
  
  @Test
  public void testMonitor() throws AdapterException {
    String instanceName = adapter.getAdapterInstance().getNameSpace()+"InstanceOne";  
    Assert.assertTrue(adapter.getInstanceModel(instanceName) == null);
    
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource motorResource = modelCreate.createResource(instanceName);
    motorResource.addProperty(RDF.type, adapter.getAdapterManagedResource());
    adapter.createInstances(modelCreate);
    
    Model monitorData = adapter.getInstanceModel(instanceName);
    Assert.assertFalse(monitorData.isEmpty());
    Assert.assertTrue(monitorData.containsAll(modelCreate));
    
    adapter.deleteInstance(instanceName);
  }
  
  @Test
  public void testGetters() {
    Assert.assertNotNull(adapter.getAdapterManagedResource());
    Assert.assertTrue(adapter.getAdapterManagedResource() instanceof Resource);
    Assert.assertNotNull(adapter.getAdapterInstance());
    Assert.assertTrue(adapter.getAdapterInstance() instanceof Resource);
    Assert.assertNotNull(adapter.getAdapterType());
    Assert.assertTrue(adapter.getAdapterInstance() instanceof Resource);
    Assert.assertNotNull(adapter.getAdapterDescriptionModel());
    Assert.assertTrue(adapter.getAdapterDescriptionModel() instanceof Model);
  }
  
  @Test
  public void testConfigure() throws AdapterException {
    String instanceName = adapter.getAdapterInstance().getNameSpace()+"InstanceOne";
    
    Model modelCreate = ModelFactory.createDefaultModel();
    Resource motorResource = modelCreate.createResource(instanceName);
    motorResource.addProperty(RDF.type, adapter.getAdapterManagedResource());
    adapter.createInstances(modelCreate);

    Model modelConfigure = ModelFactory.createDefaultModel();
    Resource motor = modelConfigure.createResource(instanceName);
    motor.addProperty(RDF.type, adapter.getAdapterManagedResource());
    Property propertyRPM = modelConfigure.createProperty(adapter.getAdapterManagedResource().getNameSpace()+"rpm");
    motor.addLiteral(propertyRPM, 23);
    Property propertyManufacturer = modelConfigure.createProperty(adapter.getAdapterManagedResource().getNameSpace()+"manufacturer");
    motor.addLiteral(propertyManufacturer, "TU Berlin");
    
    Model updatedResourceModel = adapter.configureInstances(modelConfigure);
    
    Resource updatedResource = updatedResourceModel.getResource(instanceName);
    Assert.assertEquals(23, updatedResource.getProperty(propertyRPM).getInt());
    Assert.assertEquals("TU Berlin", updatedResource.getProperty(propertyManufacturer).getString());
    
    adapter.deleteInstance(instanceName);
  }
  
}
