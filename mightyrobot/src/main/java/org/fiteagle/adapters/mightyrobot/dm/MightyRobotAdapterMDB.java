package org.fiteagle.adapters.mightyrobot.dm;

import javax.jms.Message;
import javax.jms.MessageListener;

//@DependsOn("MightyRobotAdapter")
//@MessageDriven(name = "MightyRobotAdapterMDB", activationConfig = {
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
//		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/core"),  //IMessageBus.TOPIC_CORE),
//		//@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IResourceRepository.MESSAGE_FILTER),
//		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MightyRobotAdapterMDB implements MessageListener {

//	private final static Logger LOGGER = Logger
//			.getLogger(MightyRobotAdapterMDB.class.toString());
//	private IMightyRobotAdapter motorLogic;
//
//	public MightyRobotAdapterMDB() throws NamingException  {
//	}
//	
//	@PostConstruct
//	public void setup() throws NamingException {
//		this.motorLogic = (IMightyRobotAdapter) new InitialContext().lookup("java:module/MightyRobotAdapter");
//	}
//	
	public void onMessage(final Message rcvMessage) {
//			MightyRobotAdapterMDB.LOGGER.info("Received a message via JMS message bus...");
	}
}
