package org.fiteagle.adapters.motor.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.adapters.motor.IAdapterListener;
import org.fiteagle.adapters.motor.IMotorAdapter;

@MessageDriven(name = "MotorAdapterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/core"),  //IMessageBus.TOPIC_CORE),
		//@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IResourceRepository.MESSAGE_FILTER),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDB implements MessageListener, IAdapterListener {

	private final static Logger LOGGER = Logger
			.getLogger(MotorAdapterMDB.class.toString());
	//@Inject
	//private JMSContext context;
	@Resource(mappedName = "topic/core") //IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;
	private MotorAdapterBean senderBean;
	private IMotorAdapter motorLogic;
	private static final String EJB_NAME = "java:module/MotorAdapterEJB";

	public MotorAdapterMDB() throws JMSException, NamingException {
		this.senderBean = new MotorAdapterBean();
		this.senderBean.sendMessage("started...");
		this.motorLogic = (IMotorAdapter) new InitialContext().lookup(EJB_NAME);
		this.motorLogic.registerForEvents(this);
	}
	
	public void onAdapterMessage(String string) {
		try {
			this.senderBean.sendMessage(string);
		} catch (JMSException e) {
			LOGGER.log(Level.INFO, e.getMessage());
		}
	}
	
	public void onMessage(final Message rcvMessage) {
//		try {
			MotorAdapterMDB.LOGGER.info("Received a message");
//			
//			final String serialization = rcvMessage.getStringProperty(IResourceRepository.PROP_SERIALIZATION);
//			final String result = this.repo.listResources(serialization);
//			final String id = rcvMessage.getJMSCorrelationID();					
//			final Message message = this.context.createMessage();
//			
//			message.setStringProperty(IMessageBus.TYPE_RESPONSE,
//					IResourceRepository.LIST_RESOURCES);
//			message.setStringProperty(IMessageBus.TYPE_RESULT, result);
//			if (null != id)
//				message.setJMSCorrelationID(id);
//
//			this.context.createProducer().send(topic, message);
//		} catch (final JMSException e) {
//			MotorAdapterMDB.LOGGER.log(Level.SEVERE, "Issue with JMS", e);
//		}
	}
}
