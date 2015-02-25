package org.fiteagle.adapters.tosca.client;

import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpException;
import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;

public class ToscaClient {
  
  private static Logger LOGGER = Logger.getLogger(ToscaClient.class.toString());
  
  private final String URL_TOSCA_DEFINITIONS;
  private final String URL_TOSCA_NODES;
  private final String URL_TOSCA_NODETYPES;
  
  public ToscaClient(String serverURL){
    URL_TOSCA_DEFINITIONS = serverURL+"definitions/";
    URL_TOSCA_NODES = serverURL+"nodes/";
    URL_TOSCA_NODETYPES = serverURL+"types/";
  }
  
  public Definitions getAllDefinitions() throws AdapterException{
    Client client = ClientBuilder.newClient();
    String result;
    try{
      result = client.target(URL_TOSCA_DEFINITIONS).request().get(String.class); 
    } catch(WebApplicationException e){
      throw new AdapterException(e);
    }
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  public Definitions getAllNodeTypes() throws AdapterException{
    Client client = ClientBuilder.newClient();
    String result = null;
    try{
      result = client.target(URL_TOSCA_NODETYPES).request().get(String.class); 
    } catch(WebApplicationException e){
      throw new AdapterException(e);
    }
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  public Definitions getDefinitions(String id) throws InstanceNotFoundException, AdapterException{
    Client client = ClientBuilder.newClient();
    try{
      String result = client.target(URL_TOSCA_DEFINITIONS+id).request().get(String.class); 
      InputStream input = new ByteArrayInputStream(result.getBytes());
      return convertToDefinitions(input);
    } catch(NotFoundException e){
      throw new InstanceNotFoundException("Definitions with id "+id+" not found");
    } catch(WebApplicationException e){
      throw new AdapterException(e);
    }
  }
  
  public Definitions getSingleNodeDefinitions(String id) throws InstanceNotFoundException, AdapterException{
    Client client = ClientBuilder.newClient();
    try{
      String result = client.target(URL_TOSCA_NODES+id).request().get(String.class);
      InputStream input = new ByteArrayInputStream(result.getBytes());
      return convertToDefinitions(input);
    } catch(NotFoundException e){
      throw new InstanceNotFoundException("Node with id "+id+" not found");
    } catch(WebApplicationException e){
      throw new AdapterException(e);
    }
  }
  
  public void deleteDefinitions(String id) throws AdapterException{
    Client client = ClientBuilder.newClient();
    Response response = client.target(URL_TOSCA_DEFINITIONS+id).request().delete(); 
    if(!(response.getStatusInfo().equals(Response.Status.NO_CONTENT) || response.getStatusInfo().equals(Response.Status.OK))){
      throw new AdapterException("Unexpected response while delete: "+response.getStatus());
    }
  }
  
  public Definitions updateDefinitions(String id, String definitionsString) throws HttpException, IOException, JAXBException, AdapterException {
    Client client = ClientBuilder.newClient();
    Entity<String> entity = Entity.entity(definitionsString, MediaType.APPLICATION_XML);
    String result;
    try{
      result = client.target(URL_TOSCA_DEFINITIONS+id).request().put(entity, String.class); 
    } catch(WebApplicationException e){
      throw new AdapterException(e);
    }
    
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  public Definitions createDefinitions(String definitionsString) throws HttpException, IOException, JAXBException, AdapterException {
    Client client = ClientBuilder.newClient();
    Entity<String> entity = Entity.entity(definitionsString, MediaType.APPLICATION_XML);
    String result;
    try{
      result = client.target(URL_TOSCA_DEFINITIONS).request().post(entity, String.class); 
    } catch(WebApplicationException e){
      throw new AdapterException(e);
    }
    
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

