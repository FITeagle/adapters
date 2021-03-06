package org.fiteagle.abstractAdapter.dm;

import info.openmultinet.ontology.vocabulary.Http;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.ws.rs.core.Response;

import org.apache.jena.riot.RiotException;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * subclasses must be annotated as
 * 
 * @ServerEndpoint("/") for this to work!
 */
public abstract class AbstractAdapterWebsocket implements AdapterEventListener {
  
  private static final Logger LOGGER = Logger.getLogger(AbstractAdapterWebsocket.class.getName());
  
  private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
  
  protected abstract Collection<AbstractAdapter> getAdapterInstances();
     
  @PostConstruct
  public void setup() {
    for (AbstractAdapter adapter : getAdapterInstances() ){
      adapter.addListener(this);
    }
  }
  
  @OnMessage
  public String onMessage(final String message) {
    Model responseModel = ModelFactory.createDefaultModel();
    Resource response = responseModel.createResource();
    response.addProperty(RDF.type, Http.Response);
    
    Literal responseCode = responseModel.createLiteral(String.valueOf(Response.Status.OK.getStatusCode()));
    Literal responseBody = null;
    try{
      Model requestModel = parseInputString(message);
      
      Resource request = getRequestResource(requestModel);
      responseModel.add(request, Http.response, response);
      
      AbstractAdapter targetAdapter = getTargetAdapterInstance(requestModel);
    
      Model responseBodyModel = null;
      if(request.hasProperty(RDF.type, Http.GetRequest)){
        responseBodyModel = targetAdapter.getInstances(requestModel);
      }
      else if(request.hasProperty(RDF.type, Http.PostRequest)){
        responseBodyModel = targetAdapter.createInstances(requestModel);
      }
      else if(request.hasProperty(RDF.type, Http.PutRequest)){
        responseBodyModel = targetAdapter.updateInstances(requestModel);
      }
      else if(request.hasProperty(RDF.type, Http.DeleteRequest)){
        responseBodyModel = targetAdapter.deleteInstances(requestModel);
      }
      responseModel.add(responseBodyModel);
      
    } catch(ProcessingException e){
      responseBody = getResponseBodyFromException(responseModel, e);
      response.addProperty(Http.body, responseBody);
      responseCode = getServerErrorResponseCode(responseModel);
    } catch (InvalidRequestException e) {
      responseBody = getResponseBodyFromException(responseModel, e);
      response.addProperty(Http.body, responseBody);
      responseCode = getBadRequestResponseCode(responseModel);
    } catch (InstanceNotFoundException e) {
      responseBody = getResponseBodyFromException(responseModel, e);
      response.addProperty(Http.body, responseBody);
      responseCode = getNotFoundResponseCode(responseModel);
    }
    
    response.addProperty(Http.responseCode, responseCode);
    
    return MessageUtil.serializeModel(responseModel, IMessageBus.SERIALIZATION_TURTLE);
  }
  
  private Literal getNotFoundResponseCode(Model responseModel) {
    return responseModel.createLiteral(String.valueOf(Response.Status.NOT_FOUND.getStatusCode()));
  }
  
  private Literal getServerErrorResponseCode(Model responseModel) {
    return responseModel.createLiteral(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
  }
  
  private Literal getBadRequestResponseCode(Model responseModel) {
    return responseModel.createLiteral(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()));
  }

  private Literal getResponseBodyFromException(Model responseModel, Exception e) {
    Literal responseBody;
    if(e.getCause() != null){
      responseBody = responseModel.createLiteral(e.getCause().getMessage());
    } 
    else {
      responseBody = responseModel.createLiteral(e.getMessage());
    }
    return responseBody;
  }

  private AbstractAdapter getTargetAdapterInstance(Model requestModel) throws ProcessingException, InvalidRequestException, InstanceNotFoundException {
    AbstractAdapter targetAdapter = null;
    for(AbstractAdapter adapterInstance : getAdapterInstances()){
      if(adapterInstance.isRecipient(requestModel)){
        targetAdapter = adapterInstance;
      }
    }
    if(targetAdapter == null){
      throw new InstanceNotFoundException("No target adapter found");
    }
    return targetAdapter;
  }

  private Model parseInputString(final String message) throws InvalidRequestException {
    Model requestModel; 
    try{
      requestModel = MessageUtil.parseSerializedModel(message, IMessageBus.SERIALIZATION_TURTLE);
    } catch(RiotException e){
      throw new InvalidRequestException("Input must be a turtle-serialized RDF model: "+e.getMessage());
    }
    return requestModel;
  }
  
  private Resource getRequestResource(Model requestModel) throws InvalidRequestException {
    List<Resource> requests = getAllRequestResources(requestModel);
    if(requests.size() == 0){
      throw new InvalidRequestException("No request resource was found");
    }
    if(requests.size() > 1){
      throw new InvalidRequestException("Multiple requests are not supported");
    }
    
    Resource request = requests.get(0);
    return request;
  }
 
  private static List<Resource> getAllRequestResources(Model requestModel) {
    List<Resource> requests = new ArrayList<>();
    requests.addAll(getRequestResources(requestModel, Http.GetRequest));
    requests.addAll(getRequestResources(requestModel, Http.PutRequest));
    requests.addAll(getRequestResources(requestModel, Http.PostRequest));
    requests.addAll(getRequestResources(requestModel, Http.DeleteRequest));
    return requests;
  }
  
  private static List<Resource> getRequestResources(Model requestModel, Resource requestType) {
    return requestModel.listSubjectsWithProperty(RDF.type, requestType).toList();
  }
  
  @OnOpen
  public void onOpen(final Session session, final EndpointConfig config) throws IOException {
    LOGGER.log(Level.INFO, "Opening WebSocket connection with " + session.getId() + "...");
    LOGGER.log(Level.INFO, "Sending adapter description... ");
    for(AbstractAdapter adapter : getAdapterInstances()){
      String description = MessageUtil.serializeModel(adapter.getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_TURTLE);
      session.getBasicRemote().sendText(description);
    }
    queue.add(session);
  }
  
  @OnError
  public void error(Session session, Throwable t) {
    LOGGER.log(Level.INFO, "Error on session " + session.getId() + ": " + t.getMessage());
    t.printStackTrace();
    queue.remove(session);
  }
  
  @OnClose
  public void closedConnection(Session session) {
    LOGGER.log(Level.INFO, "Closed session: " + session.getId());
    queue.remove(session);
  }
  
  private static void sendToAllSessions(String message) {
    ArrayList<Session> closedSessions = new ArrayList<Session>();
    for (Session session : queue) {
      if (!session.isOpen()) {
        LOGGER.log(Level.INFO, "Closed session: " + session.getId());
        closedSessions.add(session);
      } else {
        try {
          session.getBasicRemote().sendText(message);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    queue.removeAll(closedSessions);
  }
  
  @Override
  public void publishModelUpdate(Model eventRDF, String requestID, String methodType, String methodTarget) {
    String serializedModel = MessageUtil.serializeModel(eventRDF, IMessageBus.SERIALIZATION_TURTLE);
    sendToAllSessions(serializedModel);
  }
  
}
