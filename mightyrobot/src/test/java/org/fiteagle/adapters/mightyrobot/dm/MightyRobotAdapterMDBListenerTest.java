package org.fiteagle.adapters.mightyrobot.dm;

import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Test;

public class MightyRobotAdapterMDBListenerTest {
	
	/**
	 * Test setup of MDBListener
	 */
    @Test
    public void testInit() {

        MightyRobotAdapterMDBListener listener = new MightyRobotAdapterMDBListener();
        try {
            listener.setup();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Assert.fail();
        }
    }
    
}
