package org.fiteagle.adapters.motor.dm;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.fiteagle.adapters.motor.IAdapterListener;
import org.fiteagle.adapters.motor.IMotorAdapter;

@Named
@ServerEndpoint("/api/commander")
public class MotorAdapterWebsocket implements IAdapterListener {

	private static final Logger LOGGER = Logger
			.getLogger(MotorAdapterWebsocket.class.getName());

	
	private IMotorAdapter motorLogic;
	private Session wsSession;

	public MotorAdapterWebsocket() throws NamingException {
		this.motorLogic = (IMotorAdapter) new InitialContext().lookup("java:module/MotorAdapterEJB");
		this.motorLogic.registerForEvents(this);
	}

	@OnMessage
	public String onMessage(final String command) throws JMSException {
		LOGGER.log(Level.INFO, "Received a message via Websocket...: " + command);
		return this.motorLogic.getAdapterDescription("TURTLE");
	}

	@OnOpen
	public void onOpen(final Session wsSession, final EndpointConfig config)
			throws IOException {
		LOGGER.log(Level.INFO, "Opening Websocket connection...");
		this.wsSession = wsSession;
	}

	@Override
	public void onAdapterMessage(String message) {
		LOGGER.log(Level.INFO, "Received a message via Adapter listener...");
		if (null != this.wsSession && this.wsSession.isOpen()) {
			Set<Session> sessions = this.wsSession.getOpenSessions();
			for (Session client : sessions) {
				client.getAsyncRemote().sendText(message);
			}
		} else {
			LOGGER.log(Level.INFO, "No client to talk to");
		}			
	}
}
