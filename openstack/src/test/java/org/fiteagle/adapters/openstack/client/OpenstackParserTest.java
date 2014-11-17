package org.fiteagle.adapters.openstack.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.fiteagle.abstractAdapter.AdapterResource;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.junit.BeforeClass;
import org.junit.Test;

public class OpenstackParserTest {
  
  private static OpenstackParser openstackparser;
  
  @BeforeClass
  public static void setup(){
    OpenstackAdapter adapter = null;
    Iterator<String> iter = OpenstackAdapter.openstackAdapterInstances.keySet().iterator();
    if(iter.hasNext()){
        adapter = OpenstackAdapter.getInstance(iter.next());
    }
    openstackparser = adapter.getOpenstackParser();
  }
  
  @Test
  public void testCreationOfParser() {
    assertNotNull(openstackparser);
  }
  
  @Test
  public void testParseToAdapterResource(){
    String serverString = "{\"id\": \"12345\",\"name\": \"server1\"}";
    AdapterResource resource = openstackparser.parseToAdapterResource(serverString);
    assertEquals("12345", resource.getProperty(openstackparser.getPROPERTY_ID()));
    assertEquals("server1", resource.getName());
  }
  
}
