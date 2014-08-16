package org.fiteagle.adapters.motor.dm;

import javax.naming.NamingException;

import org.junit.Test;

public class MotorAdapterMDBListenerTest {

    @Test
    public void testInit() {

        MotorAdapterMDBListener listener = new MotorAdapterMDBListener();
        try {
            listener.setup();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

