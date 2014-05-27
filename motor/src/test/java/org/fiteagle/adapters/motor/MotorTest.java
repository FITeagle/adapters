package org.fiteagle.adapters.motor;

import org.junit.Assert;
import org.junit.Test;

public class MotorTest {
      
      @Test
      public void testDescription(){
        Motor motor = new Motor();
        Assert.assertNotEquals("", motor.getDescription());
      }
      
      @Test
      public void testAddNewInstance(){
        Motor motor = new Motor();
        Assert.assertEquals("", motor.getAllInstances());
        motor.createInstance(12);
        Assert.assertNotEquals("", motor.getAllInstances());
      }
      
      @Test
      public void testAddAndDeleteNewInstance(){
        Motor motor = new Motor();
        Assert.assertEquals("", motor.getAllInstances());
        motor.createInstance(12);
        Assert.assertNotEquals("", motor.getAllInstances());
        motor.terminateInstance(12);
        Assert.assertEquals("", motor.getAllInstances());
      }
      
      
}
