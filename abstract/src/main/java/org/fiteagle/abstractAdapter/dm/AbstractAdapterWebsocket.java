package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @ServerEndpoint("/websocket") for this to work!
 */
public abstract class AbstractAdapterWebsocket implements AdapterEventListener {

    // private static final Logger LOGGER = Logger.getLogger(MightyRobotAdapterWebsocket.class.getName());

    private AbstractAdapter abstractAdapterEJB;
    private Session wsSession;

    @PostConstruct
    public void setup() throws NamingException {
        this.abstractAdapterEJB = handleSetup();
        this.abstractAdapterEJB.addChangeListener(this);
    }

    /**
     * Subclasses must return the desired adapter singleton for use in this context
     */
    public abstract AbstractAdapter handleSetup();

    @OnMessage
    public String onMessage(final String message) throws JMSException {
        // LOGGER.log(Level.INFO, "Received a message via Websocket...: " + command);

        if (message.equals("description.ttl")) {

            return abstractAdapterEJB.getAdapterDescription(AbstractAdapter.PARAM_TURTLE);

        } else if (message.equals("description.rdf")) {

            return abstractAdapterEJB.getAdapterDescription(AbstractAdapter.PARAM_RDFXML);

        } else if (message.equals("description.ntriple")) {

            return abstractAdapterEJB.getAdapterDescription(AbstractAdapter.PARAM_NTRIPLE);
        } else if (message.equals("instances.ttl")) {

            return abstractAdapterEJB.getAllInstances(AbstractAdapter.PARAM_TURTLE);

        } else if (message.equals("instances.rdf")) {

            return abstractAdapterEJB.getAllInstances(AbstractAdapter.PARAM_RDFXML);

        } else if (message.equals("instances.ntriple")) {

            return abstractAdapterEJB.getAllInstances(AbstractAdapter.PARAM_NTRIPLE);
        }

        return message;
    }

    @OnOpen
    public void onOpen(final Session wsSession, final EndpointConfig config) throws IOException {
        // LOGGER.log(Level.INFO, "Opening Websocket connection...");
        this.wsSession = wsSession;
    }

    @Override
    public void rdfChange(Model eventRDF) {
        if (wsSession != null && wsSession.isOpen()) {
            Set<Session> sessions = wsSession.getOpenSessions();
            
            StringWriter writer = new StringWriter();

            eventRDF.write(writer, "TURTLE");

            String eventString =  writer.toString();

            for (Session client : sessions) {
                client.getAsyncRemote().sendText(eventString);
            }
        }
        // else {
        // LOGGER.log(Level.INFO, "No client to talk to");
        // }
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
