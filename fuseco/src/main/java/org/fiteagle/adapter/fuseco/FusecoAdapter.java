package org.fiteagle.adapter.fuseco;

import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.fiteagle.api.core.IMessageBus;


/**
 * 
 * @author alaa.alloush
 *
 */

@MessageDriven(name = "FusecoAdapter", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/core"), // oder IMessageBus.TOPIC_CORE
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = FusecoAdapter.MESSAGE_FILTER),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class FusecoAdapter /*extends ResourceAdapter*/ implements MessageListener{


	
	public static final String MESSAGE_FILTER = "METHOD_TYPE = 'Describe'"; // should be INFORM
	
	public HashMap<Integer, FusecoClient> instancesMap = new HashMap<Integer, FusecoClient>();
	
	public FusecoAdapter() throws JMSException{
		//super();
	}
	
	private static volatile FusecoAdapter instance = null;
	
	private final static Logger LOGGER = Logger.getLogger(FusecoAdapter.class.toString());
	
	public static FusecoAdapter getInstance() throws JMSException {
		if (instance == null) {
			synchronized (FusecoAdapter.class) {
				if (instance == null) {
					instance = new FusecoAdapter();
				}
			}
		}
		return instance;
	}
	
	@Override
	public void onMessage(Message rcvMessage) {
		TextMessage msg = null;
		if (rcvMessage instanceof TextMessage) {
			msg = (TextMessage) rcvMessage;
			try {
					LOGGER.info("The received message is " + msg.getText());
					
				}
			 catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LOGGER.warning("The Docker adapter received a message of wrong type: "
					+ rcvMessage.getClass().getName());
		}	
		
	}
	
	private FusecoClient client;
	private String name;
	private AdapterStatus status;
	
	private static boolean loaded = false;
	
	
	public void start(){	
	}
	
	public void stop(){
	}
	
	public void create(){
	}
	
	public void configure(AdapterConfiguration configuration){
		System.out.println("configuring fuseco client");
	}
	
	public void release(){
		System.out.println("release the resource");
	}
	
	public boolean isLoaded(){
		return this.loaded = loaded;
	}
	
	public void setLoaded(boolean loaded){
		this.loaded = loaded;
	}
	
	public FusecoClient createClient(){
		FusecoClient FusecoInstance = new FusecoClient(this);
		return FusecoInstance;
	}
	
	public void setClient(FusecoClient client){
		this.client= client;
	}
	
	/*public FusecoClient getClient(){
		if(this.client == null){
			this.client = new FusecoClient(this);
		}
		return client;
	}
	*/
	public void create(String name){
		//FusecoAdapter fuseco = new FusecoAdapter();
		// call methods 
		
	}

	public void checkStatus() {
		
	}
	
	public void setStatus(AdapterStatus status){
		this.status = status;
	}
	public AdapterStatus getStatus(){
		return status;
	}

}
