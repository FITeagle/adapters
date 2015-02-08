package org.fiteagle.adapters.tosca.client;

import static org.junit.Assert.assertNotNull;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.translators.tosca.jaxb.TDefinitions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpException;
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
    } catch(ProcessingException | NotFoundException e){
      LOGGER.log(Level.INFO, "Unable to connect to tosca interface at: "+URL_TOSCA);
      return;
    }
    assertNotNull(definitons);
  }
  
//  @Test
  public void testCreateDefinitions() throws JAXBException, HttpException, IOException {
    TDefinitions inputDefinitions = client.loadToscaResource("/dummy.xml");
    Definitions definitions = null; 
    try {
      definitions = client.createDefinitions(inputDefinitions);
    } catch(ProcessingException e){
      LOGGER.log(Level.INFO, "Unable to connect to tosca interface at: "+URL_TOSCA);
      return;
    }
    assertNotNull(definitions);
    System.out.println(definitions);
  }
  
}
