package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @ServerEndpoint("/websocket") for this to work!
 */
public abstract class AbstractAdapterWebsocket implements AdapterEventListener {
  
  private Session wsSession;
  
  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
  
  @PostConstruct
  public void setup() {
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      adapter.addListener(this);
    }
  }
  
  @OnMessage
  public String onMessage(final String message) throws InstanceNotFoundException, AdapterException {
    for(AbstractAdapter adapter : getAdapterInstances().values()){
      if (message.equals(adapter.getAdapterInstance().getLocalName())) {
        return MessageUtil.serializeModel(adapter.getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_TURTLE);
        
      } else if (message.equals(adapter.getAdapterInstance().getLocalName()+"/instances/ttl")) {
        
        return MessageUtil.serializeModel(adapter.getAllInstances(), IMessageBus.SERIALIZATION_TURTLE);
        
      } else if (message.equals(adapter.getAdapterInstance().getLocalName()+"/instances/rdfxml")) {
        
        return MessageUtil.serializeModel(adapter.getAllInstances(), IMessageBus.SERIALIZATION_RDFXML);
        
      } else if (message.equals(adapter.getAdapterInstance().getLocalName()+"/instances/ntriple")) {
        
        return MessageUtil.serializeModel(adapter.getAllInstances(), IMessageBus.SERIALIZATION_NTRIPLE);
      }
    }
    return null;
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
