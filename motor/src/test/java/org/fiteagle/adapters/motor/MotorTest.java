package org.fiteagle.adapters.motor;

import org.junit.Assert;
import org.junit.Test;

public class MotorTest {
      
      @Test
      public void testDescription(){
        Motor motor = new Motor();
        Assert.assertNotEquals("", motor.getDescription());
      }
      
}
