package org.fiteagle.adapters.motor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class MotorAdapterTest {

    @Test
    public void testCreateAndTerminate() {

        MotorAdapter motorAdapter = MotorAdapter.getInstance();

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

        MotorAdapter motorAdapter = MotorAdapter.getInstance();

        Assert.assertEquals(true, motorAdapter.createInstance(1));

        // Check initial rpm is 0
        String returnStringTurtle = motorAdapter.monitorInstance(1, "TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"0\""));

        String controlString = "";
        BufferedReader reader;
        String line;

        try {
            reader = new BufferedReader(new FileReader("target/test-classes/input.ttl"));
            line = reader.readLine();
            while (line != null) {
                controlString += line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Change rpm
        motorAdapter.controlInstance(controlString, "TURTLE");

        // Check new rpm is 88 (as defined in input.ttl
        returnStringTurtle = motorAdapter.monitorInstance(1, "TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"88\""));

        // terminate instance
        Assert.assertEquals(true, motorAdapter.terminateInstance(1));
    }

}
