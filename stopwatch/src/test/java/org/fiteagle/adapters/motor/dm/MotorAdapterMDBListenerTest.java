package org.fiteagle.adapters.motor.dm;

import javax.naming.NamingException;

import org.fiteagle.adapters.stopwatch.dm.StopWatchAdapterMDBListener;
import org.junit.Test;

public class MotorAdapterMDBListenerTest {

    @Test
    public void testInit() {

        StopWatchAdapterMDBListener listener = new StopWatchAdapterMDBListener();
        try {
            listener.setup();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

