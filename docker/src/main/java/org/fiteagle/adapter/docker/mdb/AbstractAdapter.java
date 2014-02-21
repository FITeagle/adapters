package org.fiteagle.adapter.docker.mdb;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

public abstract class AbstractAdapter {
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/topic/adapters")
	private Topic topic;

	private Session session;
	private MessageProducer messageProducer;

	public AbstractAdapter() throws JMSException {
		Connection connection = connectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		messageProducer = session.createProducer(topic);
		connection.start();
	}

	public void sendMessage(Message message) throws JMSException {
		messageProducer.send(message);
	}

	public Message createNewMessage() throws JMSException {
		return session.createTextMessage();
	}

}
