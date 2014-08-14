package org.fiteagle.adapter.fuseco;

import java.util.List;
import java.util.Date;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * defines methods must be implemented in every resource adapter
 * @author alaa.alloush
 *
 */
//abstract methods must be implemented by each subclass (extend)
public abstract class ResourceAdapter {

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/topic/adapters")
	private Topic topic;

	private Session session;
	private MessageProducer messageProducer;

	public ResourceAdapter() throws JMSException {
		this.setAdapterStatus(AdapterStatus.Available);
		
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
	
	private boolean available = true;
	private AdapterStatus status;
	private Date expirationTime;

	public abstract void start();
	
	public abstract void stop();
	
	public abstract void create();
	
	public abstract void configure(AdapterConfiguration configuration);
	
	public abstract void release();
	
	public abstract void checkStatus();
	
	public abstract boolean isLoaded();
	
	public abstract void setLoaded(boolean loaded);
	
	public static List<ResourceAdapter> getJavaInstances() throws IllegalAccessException{
		throw new IllegalAccessException();
	}
	
	public AdapterStatus getAdapterStatus(){
		return status;
	}
	
	public void setAdapterStatus(AdapterStatus status){
		this.status = status;
	}
	
	
	public boolean getAvailable(){
		return available;
	}
	
	public void setAvailable(boolean available){
		this.available = available;
	}
	
	public Date getExpirationTime(){
		return expirationTime;
	}
	
	public void setExpirationTime(Date expirationTime){
		this.expirationTime = expirationTime;
	}

}
