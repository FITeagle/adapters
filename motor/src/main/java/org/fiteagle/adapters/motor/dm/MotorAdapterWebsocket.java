package org.fiteagle.adapters.motor.dm;

import java.io.IOException;
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

import org.fiteagle.adapters.motor.IMotorAdapter;

@Named
@ServerEndpoint("/api/commander")
public class MotorAdapterWebsocket {

	private static final Logger LOGGER = Logger
			.getLogger(MotorAdapterWebsocket.class.getName());

	private static final String EJB_NAME = "java:module/MotorAdapterEJB";

	private IMotorAdapter motorLogic;

	public MotorAdapterWebsocket() throws NamingException {
		this.motorLogic = (IMotorAdapter) new InitialContext().lookup(MotorAdapterWebsocket.EJB_NAME);
		//this.motorLogic.registerForEvents(this);
	}

	@OnMessage
	public String onMessage(final String command) throws JMSException {
		LOGGER.log(Level.INFO, "Received WebSocket message: " + command);
		
		return this.motorLogic.getAdapterDescription("TURTLE");
	}

	@OnOpen
	public void onOpen(final Session wsSession, final EndpointConfig config)
			throws IOException {
		LOGGER.log(Level.INFO, "Opening WebSocket connection...");
	}
	
	//public void onEvent(String event) {
	// 
	//}
}
