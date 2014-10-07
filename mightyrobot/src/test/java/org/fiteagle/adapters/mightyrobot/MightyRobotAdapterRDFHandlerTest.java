package org.fiteagle.adapters.mightyrobot;

import org.junit.Assert;
import org.junit.Test;

public class MightyRobotAdapterRDFHandlerTest {
	
    /**
     * Test proper work of singleton.
     */
    @Test
    public void testSingleton() {
    	MightyRobotAdapterRDFHandler one = MightyRobotAdapterRDFHandler.getInstance();
        
    	MightyRobotAdapterRDFHandler two = MightyRobotAdapterRDFHandler.getInstance();

        // Only one instance of the AdapterRDFHandler can exist
        Assert.assertEquals(one, two);    
    }

}
