package org.fiteagle.adapters.motor;

import java.util.HashMap;
import java.util.Map;

import org.fiteagle.api.core.IMessageBus;
import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class MotorAdapterTest {


    /**
     * Test proper work of singleton.
     */
    @Test
    public void testSingleton() {
        MotorAdapter MotorAdapterOne = MotorAdapter.getInstance();
        
        MotorAdapter MotorAdapterTwo = MotorAdapter.getInstance();

        // Only one instance of the Adapter can exist
        Assert.assertEquals(MotorAdapterOne, MotorAdapterTwo);    
    }
    
    /**
     * Test creation and termination of instances
     */
    @Test
    public void testCreateAndTerminate() {
        MotorAdapter adapter = MotorAdapter.getInstance();
        Map<String, String> properties = new HashMap<>();
        
        // Creating first instance works
        Assert.assertTrue(adapter.createInstance("InstanceOne", properties));
        // Creating another instance with the same name FAILS
        Assert.assertFalse(adapter.createInstance("InstanceOne", properties));
        // Creating another instance with another name works fine
        Assert.assertTrue(adapter.createInstance("InstanceTwo", properties));
        
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
        MotorAdapter adapter = MotorAdapter.getInstance();
        
        String instanceName = adapter.getAdapterInstancePrefix()[1]+"InstanceOne";
        
        // Monitor non-existing instance yields empty String
        Assert.assertEquals("", adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT));
        // create the instance
        Map<String, String> properties = new HashMap<>();
        adapter.createInstance(instanceName, properties);
        
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
    	MotorAdapter adapter = MotorAdapter.getInstance();
    	
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
