package org.fiteagle.adapters.openstack.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

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
    Resource resource = openstackparser.parseToResource(serverString);
    assertEquals("server1", resource.getLocalName());
    assertEquals("server1", resource.getProperty(RDFS.label).getLiteral().getValue());
    assertEquals("12345", resource.getProperty(openstackparser.getPROPERTY_ID()).getLiteral().getValue());
    
  }
  
}
