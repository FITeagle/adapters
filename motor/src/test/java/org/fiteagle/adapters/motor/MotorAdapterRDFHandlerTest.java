package org.fiteagle.adapters.motor;

import org.junit.Assert;
import org.junit.Test;

public class MotorAdapterRDFHandlerTest {

    /**
     * Test proper work of singleton.
     */
    @Test
    public void testSingleton() {
    	MotorAdapterRDFHandler one = MotorAdapterRDFHandler.getInstance();
        
    	MotorAdapterRDFHandler two = MotorAdapterRDFHandler.getInstance();

        // Only one instance of the AdapterRDFHandler can exist
        Assert.assertEquals(one, two);    
    }
    
}
