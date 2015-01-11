package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @ServerEndpoint("/websocket") for this to work!
 */
public abstract class AbstractAdapterWebsocket implements AdapterEventListener {
  
  private AbstractAdapter adapter = getAdapter();
  
  private Session wsSession;
  
  protected abstract AbstractAdapter getAdapter();
  
  @PostConstruct
  public void setup() {
    adapter.addListener(this);
  }
  
  @OnMessage
  public String onMessage(final String message) {
    if (message.equals("description.ttl")) {
      
      return MessageUtil.serializeModel(getAdapter().getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_TURTLE);
      
    } else if (message.equals("instances.ttl")) {
      
      return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), IMessageBus.SERIALIZATION_TURTLE);
      
    } else if (message.equals("instances.rdf")) {
      
      return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), IMessageBus.SERIALIZATION_RDFXML);
      
    } else if (message.equals("instances.ntriple")) {
      
      return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), IMessageBus.SERIALIZATION_NTRIPLE);
    }
    
    return message;
  }
  
  @OnOpen
  public void onOpen(final Session wsSession, final EndpointConfig config) throws IOException {
    this.wsSession = wsSession;
  }
  
  @Override
  public void publishModelUpdate(Model eventRDF, String requestID, String methodType, String methodTarget) {
    if (wsSession != null && wsSession.isOpen()) {
      Set<Session> sessions = wsSession.getOpenSessions();

      String serializedModel = MessageUtil.serializeModel(eventRDF, IMessageBus.SERIALIZATION_TURTLE);
      
      for (Session client : sessions) {
        client.getAsyncRemote().sendText(serializedModel);
      }
    }
  }
  
}
