package org.fiteagle.adapters.stopwatch;

import org.fiteagle.api.core.IMessageBus;
import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;


public class StopWatchAdapterTest {

    /**
     * Test proper work of singleton.
     */
    @Test
    public void testSingleton() {
        StopWatchAdapter StopWatchAdapterOne = StopWatchAdapter.getInstance();
        
        StopWatchAdapter StopWatchAdapterTwo = StopWatchAdapter.getInstance();
        
        // Only one instance of the Adapter can exist        
        Assert.assertEquals(StopWatchAdapterOne, StopWatchAdapterTwo);    
    }
    
    /**
     * Test creation and termination of instances
     */
    @Test
    public void testCreateAndTerminate() {
        StopWatchAdapter adapter = StopWatchAdapter.getInstance();

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
        StopWatchAdapter adapter = StopWatchAdapter.getInstance();
        
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
    	StopWatchAdapter adapter = StopWatchAdapter.getInstance();
    	
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
//    @Test
//    public void testCreateAndTerminate() {
//
//        MotorAdapter motorAdapter = MotorAdapter.getInstance();
//
//        // create instance
//        Assert.assertEquals(true, motorAdapter.createInstance("Motor1"));
//
//        // check instance shows up
//        String returnStringTurtle = motorAdapter.getAllInstances("TURTLE");
//        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":m1"));
//
//        // terminate instance
//        Assert.assertEquals(true, motorAdapter.terminateInstance("Motor1"));
//
//        // terminate again
//        Assert.assertEquals(false, motorAdapter.terminateInstance("Motor1"));
//
//        // check instance is gone
//        returnStringTurtle = motorAdapter.getAllInstances("TURTLE");
//        Assert.assertEquals(-1, returnStringTurtle.indexOf(":m1"));
//    }
//
//    @Test
//    public void testControl() {
//
//        MotorAdapter motorAdapter = MotorAdapter.getInstance();
//
//        Assert.assertEquals(true, motorAdapter.createInstance("Motor1"));
//
//        // Check initial rpm is 0
//        String returnStringTurtle = motorAdapter.monitorInstance("Motor1", "TURTLE");
//        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"0\""));
//
//        String controlString = "";
//        BufferedReader reader;
//        String line;
//
//        try {
//            reader = new BufferedReader(new FileReader("target/test-classes/input.ttl"));
//            line = reader.readLine();
//            while (line != null) {
//                controlString += line;
//                line = reader.readLine();
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        // Change rpm
//        motorAdapter.controlInstance(controlString, "TURTLE");
//
//        // Check new rpm is 88 (as defined in input.ttl
//        returnStringTurtle = motorAdapter.monitorInstance(1, "TURTLE");
//        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"88\""));
//
//        // terminate instance
//        Assert.assertEquals(true, motorAdapter.terminateInstance(1));
//    }

}
