package org.fiteagle.adapters.tosca.client;

import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToscaClientTest {
  
  private ToscaClient client = new ToscaClient();
  
  @Test
  public void testLoadResource() {
    String resource = client.loadResource("/openMTCGateway.json");
    assertNotNull(resource);
    assertFalse(resource.isEmpty());
  }
  
//  @Test
//  public void test() {
//    client.getTest();
//  }
  
}
