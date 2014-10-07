package org.fiteagle.adapter.fuseco.bus;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TextMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * 
 * @author alaa.alloush
 *
 */
//public class ReceivingMessage implements MessageListener{

	/*@Resource(mappedName = "java:/ConnectionFactory") // or java: instead of jms
	private ConnectionFactory connectionFactory;
	
	@Resource(mappedName = "java:/topic/adapters")
	private Topic topic;
	
	Connection connection = null;
	
	Destination destination = null;
	
	public void onMessage(Message rcvMessage){
		try {
			destination = (Destination) topic; 
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(destination);
			connection.start();
			while (true) {
				Message msg = consumer.receiveNoWait();
				if (msg != null) {
					if (msg instanceof TextMessage) { 
						TextMessage message = null;
						message = (TextMessage) msg; 
						System.out.println("Reading message: " + message.getText()); 
					}
					else { 
			            break; 
				}
			}
		}
		
	} catch (JMSException e) {
		e.printStackTrace();
	}
	}*/
//}

