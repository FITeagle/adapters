package org.fiteagle.adapters.testbed;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestbedAdapterTest {
  
  @Test
  public void testGetModel() {
    assertNotNull(TestbedAdapter.getTestbedModel());
  }
  
  @Test
  public void testGetTestbed() {
    assertNotNull(TestbedAdapter.getTestbed());
  }
  
}
