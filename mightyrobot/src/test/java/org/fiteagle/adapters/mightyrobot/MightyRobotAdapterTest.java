package org.fiteagle.adapters.mightyrobot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

public class MightyRobotAdapterTest {

    @Test
    public void testCreateAndTerminate() {

        MightyRobotAdapter mightyRobotAdapter = MightyRobotAdapter.getInstance();

        // create instance
        Assert.assertEquals(true, mightyRobotAdapter.createInstance(1));

        // check instance shows up
        String returnStringTurtle = mightyRobotAdapter.getAllInstances("TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":m1"));

        // terminate instance
        Assert.assertEquals(true, mightyRobotAdapter.terminateInstance(1));

        // terminate again
        Assert.assertEquals(false, mightyRobotAdapter.terminateInstance(1));

        // check instance is gone
        returnStringTurtle = mightyRobotAdapter.getAllInstances("TURTLE");
        Assert.assertEquals(-1, returnStringTurtle.indexOf(":m1"));
    }

    @Test
    public void testControl() {

        MightyRobotAdapter mightyRobotAdapter = MightyRobotAdapter.getInstance();

        Assert.assertEquals(true, mightyRobotAdapter.createInstance(1));

        // Check initial rpm is 0
        String returnStringTurtle = mightyRobotAdapter.monitorInstance(1, "TURTLE");
        Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"0\""));

        // Change rpm
        InputStream is;
        try {
            is = new FileInputStream("target/test-classes/input.ttl");
            mightyRobotAdapter.controlInstance(is, "TURTLE");

            // Check new rpm is 88 (as defined in input.ttl
            returnStringTurtle = mightyRobotAdapter.monitorInstance(1, "TURTLE");
            Assert.assertNotEquals(-1, returnStringTurtle.indexOf(":rpm           \"88\""));

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // terminate instance
        Assert.assertEquals(true, mightyRobotAdapter.terminateInstance(1));
    }

}

