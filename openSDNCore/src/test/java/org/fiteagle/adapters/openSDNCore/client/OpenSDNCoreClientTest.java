package org.fiteagle.adapters.openSDNCore.client;

import org.junit.Test;
import static org.junit.Assert.*;

public class OpenSDNCoreClientTest {
  
  private OpenSDNCoreClient client = new OpenSDNCoreClient();
  
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
