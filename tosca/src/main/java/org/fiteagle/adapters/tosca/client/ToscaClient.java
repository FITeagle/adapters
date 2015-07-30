package org.fiteagle.adapters.tosca.client;

import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
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

import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;

public class ToscaClient implements IToscaClient {
  
  private static Logger LOGGER = Logger.getLogger(ToscaClient.class.toString());
  
  private final String URL_TOSCA_DEFINITIONS;
  private final String URL_TOSCA_NODES;
  private final String URL_TOSCA_NODETYPES;
  
  public ToscaClient(String serverURL){
    URL_TOSCA_DEFINITIONS = serverURL+"definitions/";
    URL_TOSCA_NODES = serverURL+"nodes/";
    URL_TOSCA_NODETYPES = serverURL+"types/";
  }
  
  @Override
  public Definitions getAllDefinitions() throws ProcessingException{
    Client client = ClientBuilder.newClient();
    String result;
    try{
      result = client.target(URL_TOSCA_DEFINITIONS).request().get(String.class); 
    } catch(WebApplicationException e){
      throw new ProcessingException(e);
    }
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  @Override
  public Definitions getAllTypes() throws ProcessingException {
    Client client = ClientBuilder.newClient();
    String result = null;
    try{
      result = client.target(URL_TOSCA_NODETYPES).request().get(String.class); 
    } catch(WebApplicationException e){
      throw new ProcessingException("Adapter couldn't get Definitions from end_point" ,e);
    }
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  @Override
  public Definitions getDefinitions(String id) throws InstanceNotFoundException, ProcessingException{
    Client client = ClientBuilder.newClient();
    try{
      String result = client.target(URL_TOSCA_DEFINITIONS+id).request().get(String.class); 
      InputStream input = new ByteArrayInputStream(result.getBytes());
      return convertToDefinitions(input);
    } catch(NotFoundException e){
      throw new InstanceNotFoundException("Definitions with id "+id+" not found");
    } catch(WebApplicationException e){
      throw new ProcessingException(e);
    }
  }
  
  @Override
  public Definitions getSingleNodeDefinitions(String id) throws InstanceNotFoundException, ProcessingException{
    Client client = ClientBuilder.newClient();
    try{
      String result = client.target(URL_TOSCA_NODES+id).request().get(String.class);
      InputStream input = new ByteArrayInputStream(result.getBytes());
      return convertToDefinitions(input);
    } catch(NotFoundException e){
      throw new InstanceNotFoundException("Node with id "+id+" not found");
    } catch(WebApplicationException e){
      throw new ProcessingException(e);
    }
  }
  
  @Override
  public void deleteDefinitions(String id) throws ProcessingException{
    Client client = ClientBuilder.newClient();
    Response response = client.target(URL_TOSCA_DEFINITIONS+id).request().delete(); 
    if(!(response.getStatusInfo().equals(Response.Status.NO_CONTENT) || response.getStatusInfo().equals(Response.Status.OK))){
      throw new ProcessingException("Unexpected response while delete: "+response.getStatus());
    }
  }
  
  @Override
  public Definitions updateDefinitions(String id, String definitionsString) throws ProcessingException {
    Client client = ClientBuilder.newClient();
    Entity<String> entity = Entity.entity(definitionsString, MediaType.APPLICATION_XML);
    String result;
    try{
      result = client.target(URL_TOSCA_DEFINITIONS+id).request().put(entity, String.class); 
    } catch(WebApplicationException e){
      throw new ProcessingException(e);
    }
    
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  @Override
  public Definitions createDefinitions(String definitionsString) throws ProcessingException, InvalidRequestException {
    Client client = ClientBuilder.newClient();
    Entity<String> entity = Entity.entity(definitionsString, MediaType.APPLICATION_XML);
    String result;
    try{
      result = client.target(URL_TOSCA_DEFINITIONS).request().post(entity, String.class); 
    } catch(BadRequestException e){
      throw new InvalidRequestException(e);
    } catch(WebApplicationException e){
      throw new ProcessingException(e);
    }
    
    InputStream input = new ByteArrayInputStream(result.getBytes());
    return convertToDefinitions(input);
  }
  
  protected Definitions convertToDefinitions(InputStream input){
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

