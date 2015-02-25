package org.fiteagle.adapters.tosca.client;

import static org.junit.Assert.assertNotNull;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;

import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.junit.Test;

public class ToscaClientTest {
  
  private static Logger LOGGER = Logger.getLogger(ToscaClientTest.class.toString());
  
  private static String URL_TOSCA = "http://localhost:8080/api/rest/tosca/v2/";
  
  private ToscaClient client = new ToscaClient(URL_TOSCA);
  
  @Test
  public void testLoadToscaResource() {
    Definitions resource = client.loadToscaResource("/dummy.xml");
    assertNotNull(resource);
  }
  
  @Test
  public void testGetDefinitions() {
    Definitions definitons = null; 
    try {
      definitons = client.getAllDefinitions();
    } catch(ProcessingException | NotFoundException | AdapterException e){
      LOGGER.log(Level.INFO, "Unable to connect to tosca interface at: "+URL_TOSCA);
      return;
    }
    assertNotNull(definitons);
  }
  
}
