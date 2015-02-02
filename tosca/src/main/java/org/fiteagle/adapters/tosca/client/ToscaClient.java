package org.fiteagle.adapters.tosca.client;

import info.openmultinet.ontology.translators.AbstractConverter;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.translators.tosca.jaxb.TDefinitions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpException;

public class ToscaClient {
  
  private static Logger LOGGER = Logger.getLogger(ToscaClient.class.toString());
  
  private final String URL_TOSCA_DEFINITIONS;
  private final String URL_TOSCA_NODES;
  
  public ToscaClient(String definitionsURL, String nodesURL){
    URL_TOSCA_DEFINITIONS = definitionsURL;
    URL_TOSCA_NODES = nodesURL;
  }
  
  public String getDefinitions(){
    Client client = ClientBuilder.newClient();
    String result = client.target(URL_TOSCA_DEFINITIONS).request().get(String.class); 
    return result;
  }
  
  public InputStream getDefinitionsStream(){
    Client client = ClientBuilder.newClient();
    String result = client.target(URL_TOSCA_DEFINITIONS).request().get(String.class); 
    return new ByteArrayInputStream(result.getBytes());
  }
  
  public InputStream getSingleNodeDefinitions(String id){
    Client client = ClientBuilder.newClient();
    String result = client.target(URL_TOSCA_NODES+id).request().get(String.class); 
    return new ByteArrayInputStream(result.getBytes());
  }
  
  public Definitions createDefinitions(TDefinitions definitions) throws HttpException, IOException, JAXBException {
    String definitionsString = AbstractConverter.toString(definitions, OMN2Tosca.JAXB_PACKAGE_NAME);
    
    Client client = ClientBuilder.newClient();
    Entity<String> e = Entity.entity(definitionsString, MediaType.APPLICATION_XML);
    String result = client.target(URL_TOSCA_DEFINITIONS).request().post(e, String.class); 
    
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  public Definitions createDefinitions(String definitionsString) throws HttpException, IOException, JAXBException {
    Client client = ClientBuilder.newClient();
    Entity<String> e = Entity.entity(definitionsString, MediaType.APPLICATION_XML);
    String result = client.target(URL_TOSCA_DEFINITIONS).request().post(e, String.class); 
    
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  protected Definitions loadToscaResource(String path){
    InputStream input = getClass().getResourceAsStream(path);
    return convertToDefinitions(input);
  }
  
  private Definitions convertToDefinitions(InputStream input){
    Definitions definitions = null;
    try {
      JAXBContext context = JAXBContext.newInstance(Definitions.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      definitions = unmarshaller.unmarshal(new StreamSource(input), Definitions.class).getValue();
    } catch (JAXBException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return definitions;
  }
}

