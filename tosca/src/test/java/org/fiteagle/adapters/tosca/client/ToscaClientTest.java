package org.fiteagle.adapters.tosca.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.translators.tosca.jaxb.TDefinitions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpException;
import org.junit.Test;

public class ToscaClientTest {
  
  private static Logger LOGGER = Logger.getLogger(ToscaClientTest.class.toString());
  
  private static String URL_TOSCA_DEFINITIONS = "http://localhost:8080/api/rest/tosca/v2/definitions";
  
  private ToscaClient client = new ToscaClient(URL_TOSCA_DEFINITIONS);
  
  @Test
  public void testLoadToscaResource() {
    Definitions resource = client.loadToscaResource("/dummy.xml");
    assertNotNull(resource);
  }
  
  @Test
  public void testGetDefinitions() {
    String definitons = null; 
    try {
      definitons = client.getDefinitions();
    } catch(ProcessingException e){
      LOGGER.log(Level.INFO, "Unable to connect to tosca interface at: "+URL_TOSCA_DEFINITIONS);
      return;
    }
    assertNotNull(definitons);
    assertFalse(definitons.isEmpty());
  }
  
//  @Test
  public void testCreateDefinitions() throws JAXBException, HttpException, IOException {
    TDefinitions inputDefinitions = client.loadToscaResource("/dummy.xml");
    Definitions definitions = null; 
    try {
      definitions = client.createDefinitions(inputDefinitions);
    } catch(ProcessingException e){
      LOGGER.log(Level.INFO, "Unable to connect to tosca interface at: "+URL_TOSCA_DEFINITIONS);
      return;
    }
    assertNotNull(definitions);
    System.out.println(definitions);
  }
  
}
