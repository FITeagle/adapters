package org.fiteagle.adapters.stopwatch;

import org.junit.Assert;
import org.junit.Test;

public class StopWatchAdapterRDFHandlerTest {

    /**
     * Test proper work of singleton.
     */
    @Test
    public void testSingleton() {
    	StopWatchAdapterRDFHandler one = StopWatchAdapterRDFHandler.getInstance();
        
    	StopWatchAdapterRDFHandler two = StopWatchAdapterRDFHandler.getInstance();

        // Only one instance of the AdapterRDFHandler can exist
        Assert.assertEquals(one, two);    
    }
    
}
