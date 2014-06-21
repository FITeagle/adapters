package org.fiteagle.adapters.motor.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.adapters.motor.IAdapterListener;
import org.fiteagle.adapters.motor.IMotorAdapter;

@Singleton
@Startup
public class MotorAdapterListener implements IAdapterListener {

	private IMotorAdapter motorLogic;

	private static Logger LOGGER = Logger
			.getLogger(MotorAdapterListener.class.toString());
	private MotorAdapterBean senderBean;

	@Inject
	public MotorAdapterListener(MotorAdapterBean senderBean)
			throws NamingException, JMSException {
		LOGGER.log(Level.INFO, "Sending a message via JMS message bus...");
		this.senderBean = senderBean;
		this.senderBean.sendMessage("Sent a message to the JMS message bus");
	}

	@Override
	public void onAdapterMessage(String string) {
		LOGGER.log(Level.ALL, "Received a message via adapter listener...");
		try {
			this.senderBean.sendMessage(string);
		} catch (JMSException e) {
			LOGGER.log(Level.INFO, e.getMessage());
		}		
	}

    @PostConstruct
    public void startup() {
		LOGGER.log(Level.ALL, "Servlet has been initialized");
		System.out.println("Servlet has been initialized");
		try {
			this.motorLogic = (IMotorAdapter) new InitialContext().lookup("java:module/MotorAdapterEJB");
			//todo: this ideas doesn't work either
			//this.motorLogic.registerForEvents(this);
		} catch (NamingException e) {
			LOGGER.log(Level.INFO, e.getMessage());
		}
		
	}	
}
