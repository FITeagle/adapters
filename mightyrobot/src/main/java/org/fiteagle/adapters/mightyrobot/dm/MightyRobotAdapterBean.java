package org.fiteagle.adapters.mightyrobot.dm;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class MightyRobotAdapterBean {

	@Inject
    private JMSContext jmsContext;
	 //todo: get from api package

   // @Resource(mappedName = "java:/topic/core")
    @Resource(mappedName = "java:/jms/queue/sparqlModule")
    private Queue queue;
//	private static final Logger LOGGER = Logger.getLogger(MightyRobotAdapterBean.class
   //         .getName());

	public void sendMessage(Message message) throws JMSException {
//		LOGGER.log(Level.INFO, "Submitting request to JMS...");
        System.out.println("Submitting request to JMS...");
		jmsContext.createProducer().send(queue, message);
	}
//
//	public Message createMessage() {
//		if (null == jmsContext)
//			LOGGER.log(Level.SEVERE, "jms context was not injected!");
//		return jmsContext.createMessage();
//	}
//
//	public void sendMessage(String string) throws JMSException {
//		if (null == jmsContext)
//			LOGGER.log(Level.SEVERE, "jms context was not injected!");
//		sendMessage(jmsContext.createTextMessage(string));
//	}
}
