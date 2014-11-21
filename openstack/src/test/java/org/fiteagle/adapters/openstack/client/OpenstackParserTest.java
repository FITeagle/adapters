package org.fiteagle.adapters.openstack.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.client.model.Images;
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
  public void testParseToImages(){
    String imagesString = "{\"images\": [{\"status\": \"ACTIVE\", \"updated\": \"2014-11-18T15:04:18Z\", \"links\": [{\"href\": \"http://server1.av.tu-berlin.de:8774/v1.1/b0a6462868aa402aa11a405c670553fa/images/5c7fdee9-ea59-4b95-bd92-77e8b9137fbc\", \"rel\": \"self\"}, {\"href\": \"http://server1.av.tu-berlin.de:8774/b0a6462868aa402aa11a405c670553fa/images/5c7fdee9-ea59-4b95-bd92-77e8b9137fbc\", \"rel\": \"bookmark\"}, {\"href\": \"http://130.149.167.11:9292/b0a6462868aa402aa11a405c670553fa/images/5c7fdee9-ea59-4b95-bd92-77e8b9137fbc\", \"type\": \"application/vnd.openstack.image\", \"rel\": \"alternate\"}], \"id\": \"5c7fdee9-ea59-4b95-bd92-77e8b9137fbc\", \"OS-EXT-IMG-SIZE:size\": 1212743680, \"name\": \"ubuntu-with-user-and-openvpn\", \"created\": \"2014-11-18T11:53:45Z\", \"minDisk\": 20, \"server\": {\"id\": \"19dc2b23-4638-493d-80db-b9a519fa60a5\", \"links\": [{\"href\": \"http://server1.av.tu-berlin.de:8774/v1.1/b0a6462868aa402aa11a405c670553fa/servers/19dc2b23-4638-493d-80db-b9a519fa60a5\", \"rel\": \"self\"}, {\"href\": \"http://server1.av.tu-berlin.de:8774/b0a6462868aa402aa11a405c670553fa/servers/19dc2b23-4638-493d-80db-b9a519fa60a5\", \"rel\": \"bookmark\"}]}, \"progress\": 100, \"minRam\": 0, \"metadata\": {\"instance_uuid\": \"19dc2b23-4638-493d-80db-b9a519fa60a5\", \"image_location\": \"snapshot\", \"image_state\": \"available\", \"instance_type_memory_mb\": \"2048\", \"instance_type_swap\": \"0\", \"description\": null, \"image_type\": \"snapshot\", \"instance_type_id\": \"5\", \"ramdisk_id\": null, \"instance_type_name\": \"m1.small\", \"instance_type_ephemeral_gb\": \"0\", \"instance_type_rxtx_factor\": \"1.0\", \"kernel_id\": null, \"network_allocated\": \"True\", \"instance_type_flavorid\": \"2\", \"instance_type_vcpus\": \"1\", \"user_id\": \"b8787462e3e042aca17626fab70a48f2\", \"instance_type_root_gb\": \"20\", \"base_image_ref\": \"7bef2175-b4cd-4302-be23-dbeb35b41702\", \"owner_id\": \"b0a6462868aa402aa11a405c670553fa\"}}]}";
    
    Images images = OpenstackParser.parseToImages(imagesString);
    assertEquals(1, images.getList().size());
    assertEquals("ubuntu-with-user-and-openvpn", images.getList().get(0).getName());
    assertEquals("ACTIVE", images.getList().get(0).getStatus());
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
