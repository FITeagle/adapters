package org.fiteagle.adapters.stopwatch.dm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.adapters.stopwatch.IStopwatchAdapter;
import org.fiteagle.adapters.stopwatch.StopwatchAdapter;

//@Named
@ServerEndpoint("/websocket")
//@DependsOn("StopwatchAdapter")
public class StopwatchAdapterWebsocket implements PropertyChangeListener {

    // private static final Logger LOGGER = Logger.getLogger(StopwatchAdapterWebsocket.class.getName());

    private IStopwatchAdapter motorAdapterEJB;
    private Session wsSession;

//    @PostConstruct
//    public void setup() throws NamingException {
//        motorAdapterEJB = (IStopwatchAdapter) new InitialContext().lookup("java:module/StopwatchAdapter");
//        motorAdapterEJB.addChangeListener(this);
//        // this.motorLogic.registerForEvents(this);
//    }
    
    @PostConstruct
    public void setup() throws NamingException {
  //    this.motorAdapterEJB = (IStopwatchAdapter) new InitialContext().lookup("java:module/StopwatchAdapter");
      this.motorAdapterEJB = StopwatchAdapter.getInstance();
      this.motorAdapterEJB.addChangeListener(this);
      
      
      
//      21:03:46,385 ERROR [io.undertow.request] (default task-15) Blocking request failed HttpServerExchange{ GET /AdapterMotor/websocket}: org.jboss.weld.exceptions.WeldException: WELD-000049: Unable to invoke public void org.fiteagle.adapters.stopwatch.dm.StopwatchAdapterWebsocket.setup() throws javax.naming.NamingException on org.fiteagle.adapters.stopwatch.dm.StopwatchAdapterWebsocket@158517f
//      at org.jboss.weld.injection.producer.DefaultLifecycleCallbackInvoker.invokeMethods(DefaultLifecycleCallbackInvoker.java:91)
//      at org.jboss.weld.injection.producer.DefaultLifecycleCallbackInvoker.postConstruct(DefaultLifecycleCallbackInvoker.java:72)
//      at org.jboss.weld.injection.producer.BasicInjectionTarget.postConstruct(BasicInjectionTarget.java:95)
//      at org.jboss.as.weld.deployment.WeldClassIntrospector$1.getReference(WeldClassIntrospector.java:59)

    }

    @OnMessage
    public String onMessage(final String message) throws JMSException {
        // LOGGER.log(Level.INFO, "Received a message via Websocket...: " + command);
        
        if(message.equals("description.ttl")){
            
            return motorAdapterEJB.getAdapterDescription(StopwatchAdapter.PARAM_TURTLE);
            
        } else if(message.equals("description.rdf")){            
            
            return motorAdapterEJB.getAdapterDescription(StopwatchAdapter.PARAM_RDFXML);
            
        }  else if(message.equals("description.ntriple")){            
            
            return motorAdapterEJB.getAdapterDescription(StopwatchAdapter.PARAM_NTRIPLE);
        } else if(message.equals("instances.ttl")){
            
            return motorAdapterEJB.getAllInstances(StopwatchAdapter.PARAM_TURTLE);
            
        } else if(message.equals("instances.rdf")){            
            
            return motorAdapterEJB.getAllInstances(StopwatchAdapter.PARAM_RDFXML);
            
        }  else if(message.equals("instances.ntriple")){            
            
            return motorAdapterEJB.getAllInstances(StopwatchAdapter.PARAM_NTRIPLE);
        }


        return message;
    }

    @OnOpen
    public void onOpen(final Session wsSession, final EndpointConfig config) throws IOException {
        // LOGGER.log(Level.INFO, "Opening Websocket connection...");
        this.wsSession = wsSession;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (wsSession != null && wsSession.isOpen()) {
            Set<Session> sessions = wsSession.getOpenSessions();
            
            String message = "Event Notification: " + event.getSource().toString() + " " + event.getPropertyName() + " [old -> " + event.getOldValue() + "] | [new -> " + event.getNewValue() + "]";
            for (Session client : sessions) {
                client.getAsyncRemote().sendText(message);
            }
        }
//        else {
//            LOGGER.log(Level.INFO, "No client to talk to");
//        }
    }

    // @Override
    // public void onAdapterMessage(String message) {
    // LOGGER.log(Level.INFO, "Received a message via Adapter listener...");
    // if (null != this.wsSession && this.wsSession.isOpen()) {
    // Set<Session> sessions = this.wsSession.getOpenSessions();
    // for (Session client : sessions) {
    // client.getAsyncRemote().sendText(message);
    // }
    // } else {
    // LOGGER.log(Level.INFO, "No client to talk to");
    // }
    // }
}
