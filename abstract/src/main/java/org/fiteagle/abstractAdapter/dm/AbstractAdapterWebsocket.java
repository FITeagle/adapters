package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @ServerEndpoint("/websocket") for this to work!
 */
public abstract class AbstractAdapterWebsocket implements AdapterEventListener {

    // private static final Logger LOGGER = Logger.getLogger(MightyRobotAdapterWebsocket.class.getName());

    private AbstractAdapter abstractAdapter;
    private Session wsSession;

    @PostConstruct
    public void setup() {
        this.abstractAdapter = handleSetup();
        this.abstractAdapter.addChangeListener(this);
    }

    /**
     * Subclasses must return the desired adapter singleton for use in this context
     */
    public abstract AbstractAdapter handleSetup();

    @OnMessage
    public String onMessage(final String message) {
        // LOGGER.log(Level.INFO, "Received a message via Websocket...: " + command);

        if (message.equals("description.ttl")) {

            return abstractAdapter.getAdapterDescription(IMessageBus.SERIALIZATION_TURTLE);

        } else if (message.equals("description.rdf")) {

            return abstractAdapter.getAdapterDescription(IMessageBus.SERIALIZATION_RDFXML);

        } else if (message.equals("description.ntriple")) {

            return abstractAdapter.getAdapterDescription(IMessageBus.SERIALIZATION_NTRIPLE);
        } else if (message.equals("instances.ttl")) {

            return abstractAdapter.getAllInstances(IMessageBus.SERIALIZATION_TURTLE);

        } else if (message.equals("instances.rdf")) {

            return abstractAdapter.getAllInstances(IMessageBus.SERIALIZATION_RDFXML);

        } else if (message.equals("instances.ntriple")) {

            return abstractAdapter.getAllInstances(IMessageBus.SERIALIZATION_NTRIPLE);
        }

        return message;
    }

    @OnOpen
    public void onOpen(final Session wsSession, final EndpointConfig config) throws IOException {
        // LOGGER.log(Level.INFO, "Opening Websocket connection...");
        this.wsSession = wsSession;
    }

    @Override
    public void publishModelUpdate(Model eventRDF, String requestID) {
        if (wsSession != null && wsSession.isOpen()) {
            Set<Session> sessions = wsSession.getOpenSessions();
            
            StringWriter writer = new StringWriter();

            eventRDF.write(writer, "TURTLE");

            String eventString =  writer.toString();

            for (Session client : sessions) {
                client.getAsyncRemote().sendText(eventString);
            }
        }
    }

}
