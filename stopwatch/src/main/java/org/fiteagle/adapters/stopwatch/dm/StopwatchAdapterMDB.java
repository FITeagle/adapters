package org.fiteagle.adapters.stopwatch.dm;

import javax.jms.Message;
import javax.jms.MessageListener;

//@DependsOn("StopwatchAdapter")
//@MessageDriven(name = "StopwatchAdapterMDB", activationConfig = {
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
//		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/core"),  //IMessageBus.TOPIC_CORE),
//		//@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IResourceRepository.MESSAGE_FILTER),
//		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class StopwatchAdapterMDB implements MessageListener {

//	private final static Logger LOGGER = Logger
//			.getLogger(StopwatchAdapterMDB.class.toString());
//	private IStopwatchAdapter motorLogic;
//
//	public StopwatchAdapterMDB() throws NamingException  {
//	}
//	
//	@PostConstruct
//	public void setup() throws NamingException {
//		this.motorLogic = (IStopwatchAdapter) new InitialContext().lookup("java:module/StopwatchAdapter");
//	}
//	
	public void onMessage(final Message rcvMessage) {
//			StopwatchAdapterMDB.LOGGER.info("Received a message via JMS message bus...");
	}
}
