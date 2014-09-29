package org.fiteagle.adapters.stopwatch.dm;

import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Test;

public class StopWatchAdapterMDBListenerTest {

	/**
	 * Test setup of MDBListener
	 */
    @Test
    public void testInit() {

        StopWatchAdapterMDBListener listener = new StopWatchAdapterMDBListener();
        try {
            listener.setup();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Assert.fail();
        }
    }

}

