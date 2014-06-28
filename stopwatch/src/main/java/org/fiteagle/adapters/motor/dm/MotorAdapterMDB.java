package org.fiteagle.adapters.motor.dm;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.DependsOn;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.adapters.motor.IMotorAdapter;

//@DependsOn("MotorAdapter")
//@MessageDriven(name = "MotorAdapterMDB", activationConfig = {
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
//		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/core"),  //IMessageBus.TOPIC_CORE),
//		//@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IResourceRepository.MESSAGE_FILTER),
//		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDB implements MessageListener {

//	private final static Logger LOGGER = Logger
//			.getLogger(MotorAdapterMDB.class.toString());
//	private IMotorAdapter motorLogic;
//
//	public MotorAdapterMDB() throws NamingException  {
//	}
//	
//	@PostConstruct
//	public void setup() throws NamingException {
//		this.motorLogic = (IMotorAdapter) new InitialContext().lookup("java:module/MotorAdapter");
//	}
//	
	public void onMessage(final Message rcvMessage) {
//			MotorAdapterMDB.LOGGER.info("Received a message via JMS message bus...");
	}
}
