package org.fiteagle.adapters.motor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class MotorAdapterTest {

    @Test
    public void testCreateAndTerminate() {

        MotorAdapter motorAdapter = MotorAdapter.getInstance();

        // create instance
        Assert.assertEquals(true, motorAdapter.createMotorInstance(1));

        // check instance shows up
        String returnStringTurtle = motorAdapter.getAllMotorInstances("TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":m1"));

        // terminate instance
        Assert.assertEquals(true, motorAdapter.terminateMotorInstance(1));

        // terminate again
        Assert.assertEquals(false, motorAdapter.terminateMotorInstance(1));

        // check instance is gone
        returnStringTurtle = motorAdapter.getAllMotorInstances("TURTLE");
        Assert.assertEquals(-1, returnStringTurtle.indexOf(":m1"));
    }

    @Test
    public void testControl() {

        MotorAdapter motorAdapter = MotorAdapter.getInstance();

        Assert.assertEquals(true, motorAdapter.createMotorInstance(1));

        // Check initial rpm is 0
        String returnStringTurtle = motorAdapter.monitorMotorInstance(1, "TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"0\""));

        // Change rpm
        InputStream is;
        try {
            is = new FileInputStream("target/test-classes/input.ttl");
            motorAdapter.controlMotorInstance(is, "TURTLE");

            // Check new rpm is 88 (as defined in input.ttl
            returnStringTurtle = motorAdapter.monitorMotorInstance(1, "TURTLE");
            Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"88\""));

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // terminate instance
        Assert.assertEquals(true, motorAdapter.terminateMotorInstance(1));
    }

}

