package org.fiteagle.adapter.fuseco;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.fiteagle.adapter.fuseco.bus.SendingMessage;


@Path("/test")
public class FusecoRest {

	 @Inject
	 private JMSContext context;
	 @Resource(mappedName = "java:/topic/core")
	 private Topic topic;
	  
	public FusecoRest() {
		// TODO Auto-generated constructor stub
	}

	  @GET
	  @Path("")
	  public void test(){
		  System.out.println("GOT TEST CALL");
		  send();
//		  SendingMessage sm = new SendingMessage();
//		  sm.send();
	  }
	  
	  @POST
	  @Path("newInstance")
	  public void createNewInstance(){
		  System.out.println("Creating new instance..");
	  }
	
	  public void send(){
		 Message message = context.createTextMessage("hallo");
		 	 
	//			String filter = "";
	    try {
	    	message.setStringProperty("METHOD_TYPE", "Describe");
	//		      message.setJMSCorrelationID(UUID.randomUUID().toString());
	//		      filter = "JMSCorrelationID='" + message.getJMSCorrelationID() + "'";
	    } catch (JMSException e) {
	    	e.printStackTrace();
	    }
	    context.createProducer().send(topic, message);
		 
	//		    Message rcvMessage = context.createConsumer(topic, filter).receive(10000);
	 }
	  
}

