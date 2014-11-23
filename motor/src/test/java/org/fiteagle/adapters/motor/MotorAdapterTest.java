package org.fiteagle.adapters.motor;

import java.util.Iterator;

import org.fiteagle.api.core.IMessageBus;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class MotorAdapterTest {


	private static MotorAdapter adapter;
	
	@BeforeClass
	public static void setup(){
	    Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
	    if(iterator.hasNext()){
	    	adapter = MotorAdapter.getInstance(iterator.next());
	    }
	}
	
    /**
     * Test creation and termination of instances
     */
    @Test
    public void testCreateAndTerminate() {
      Model modelCreate = ModelFactory.createDefaultModel();
        
        // Creating first instance works
        Assert.assertTrue(adapter.createInstance("InstanceOne", modelCreate));
        // Creating another instance with the same name FAILS
        Assert.assertFalse(adapter.createInstance("InstanceOne", modelCreate));
        // Creating another instance with another name works fine
        Assert.assertTrue(adapter.createInstance("InstanceTwo", modelCreate));
        
        // Terminate existing instance
        Assert.assertTrue(adapter.terminateInstance("InstanceOne"));
        // Terminate non-existing instance
        Assert.assertFalse(adapter.terminateInstance("InstanceOne"));
        // Terminate remaining instance
        Assert.assertTrue(adapter.terminateInstance("InstanceTwo"));
    }   
  
    /**
     * Test Monitoring of instances
     */
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
 
    /**
     * Test getter methods, those must return actual data if everything was initialized properly
     */
    @Test
    public void testGetters(){
    	
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
    
}
