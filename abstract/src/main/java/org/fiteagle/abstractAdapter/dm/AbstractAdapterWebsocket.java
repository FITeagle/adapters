package org.fiteagle.abstractAdapter.dm;

import info.openmultinet.ontology.vocabulary.Http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

import org.apache.jena.riot.RiotException;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

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
  
  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
     
  @PostConstruct
  public void setup() {
    for (AbstractAdapter adapter : getAdapterInstances().values()) {
      adapter.addListener(this);
    }
  }
  
  @OnMessage
  public String onMessage(final String message) throws InstanceNotFoundException, AdapterException {
    Model requestModel; 
    try{
      requestModel = MessageUtil.parseSerializedModel(message, IMessageBus.SERIALIZATION_TURTLE);
    } catch(RiotException e){
      return "Error: Input must be a turtle-serialized RDF model";
    }
    
    List<Resource> requests = getAllRequestResources(requestModel);
    if(requests.size() == 0){
      return "Error: No request resource was found";
    }
    if(requests.size() > 1){
      return "Error: Multiple requests are not supported";
    }
    
    Resource request = requests.get(0);
    
    Model responseModel = ModelFactory.createDefaultModel();
    for(AbstractAdapter adapterInstance : getAdapterInstances().values()){
      if(adapterInstance.isRecipient(requestModel)){
        if(request.hasProperty(RDF.type, Http.GetRequest)){
          responseModel.add(adapterInstance.getInstances(requestModel));
          break;
        }
        else if(request.hasProperty(RDF.type, Http.PostRequest)){
          adapterInstance.createInstances(requestModel);
        }
        else if(request.hasProperty(RDF.type, Http.PutRequest)){
          adapterInstance.configureInstances(requestModel);
        }
        else if(request.hasProperty(RDF.type, Http.DeleteRequest)){
          adapterInstance.deleteInstances(requestModel);
        }
      }
    }
    
    return MessageUtil.serializeModel(responseModel, IMessageBus.SERIALIZATION_TURTLE);
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
  
//  public static class WebSocketException extends Exception {
//    
//    public WebSocketException(String message){
//        super(message);
//    }
//    
//  }
//  
}
