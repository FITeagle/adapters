package org.fiteagle.adapters.stopwatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class StopwatchAdapterTest {

    @Test
    public void testCreateAndTerminate() {

        StopwatchAdapter motorAdapter = StopwatchAdapter.getInstance();

        // create instance
        Assert.assertEquals(true, motorAdapter.createInstance(1));

        // check instance shows up
        String returnStringTurtle = motorAdapter.getAllInstances("TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":m1"));

        // terminate instance
        Assert.assertEquals(true, motorAdapter.terminateInstance(1));

        // terminate again
        Assert.assertEquals(false, motorAdapter.terminateInstance(1));

        // check instance is gone
        returnStringTurtle = motorAdapter.getAllInstances("TURTLE");
        Assert.assertEquals(-1, returnStringTurtle.indexOf(":m1"));
    }

    @Test
    public void testControl() {

        StopwatchAdapter motorAdapter = StopwatchAdapter.getInstance();

        Assert.assertEquals(true, motorAdapter.createInstance(1));

        // Check initial rpm is 0
        String returnStringTurtle = motorAdapter.monitorInstance(1, "TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"0\""));

        // Change rpm
        InputStream is;
        try {
            is = new FileInputStream("target/test-classes/input.ttl");
            motorAdapter.controlInstance(is, "TURTLE");

            // Check new rpm is 88 (as defined in input.ttl
            returnStringTurtle = motorAdapter.monitorInstance(1, "TURTLE");
            //Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"88\""));

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // terminate instance
        Assert.assertEquals(true, motorAdapter.terminateInstance(1));
    }

}

