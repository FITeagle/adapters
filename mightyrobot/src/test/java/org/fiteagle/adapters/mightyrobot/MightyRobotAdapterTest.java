package org.fiteagle.adapters.mightyrobot;

import org.fiteagle.api.core.IMessageBus;
import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class MightyRobotAdapterTest {


    /**
     * Test proper work of singleton.
     */
    @Test
    public void testSingleton() {
        MightyRobotAdapter mightyRobotAdapterOne = MightyRobotAdapter.getInstance();
        
        MightyRobotAdapter mightyRobotAdapterTwo = MightyRobotAdapter.getInstance();
        
        // Only one instance of the Adapter can exist        
        Assert.assertEquals(mightyRobotAdapterOne, mightyRobotAdapterTwo);
    }
    
    /**
     * Test creation and termination of instances
     */
    @Test
    public void testCreateAndTerminate() {
        MightyRobotAdapter adapter = MightyRobotAdapter.getInstance();

        // Creating first instance works
        Assert.assertTrue(adapter.createInstance("InstanceOne"));
        // Creating another instance with the same name FAILS
        Assert.assertFalse(adapter.createInstance("InstanceOne"));
        // Creating another instance with another name works fine
        Assert.assertTrue(adapter.createInstance("InstanceTwo"));
        
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
        MightyRobotAdapter adapter = MightyRobotAdapter.getInstance();
        
        String instanceName = "InstanceOne";
        
        // Monitor non-existing instance yields empty String
        Assert.assertEquals("", adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT));
        // create the instance
        adapter.createInstance(instanceName);
        
        // Monitoring existing instance yields a non-empty result
        String monitorData = adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT);
        Assert.assertNotEquals("", monitorData);
        
        // Monitoring data contains the instance name
        Assert.assertTrue(monitorData.contains(instanceName));
        
        // Monitoring data contains the adapter specific prefix
        Assert.assertTrue(monitorData.contains(adapter.getAdapterSpecificPrefix()[0]) && 
        		monitorData.contains(adapter.getAdapterSpecificPrefix()[1]));

        // release instances
        adapter.terminateInstance("InstanceOne");
    }      
    
    /**
     * Test getter methods, those must return actual data if everything was initialized properly
     */
    @Test
    public void testGetters(){
    	MightyRobotAdapter adapter = MightyRobotAdapter.getInstance(); 
    	
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
