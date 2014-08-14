package org.fiteagle.adapter.fuseco.bus;

import org.fiteagle.adapter.fuseco.ResourceInformation;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
/**
 * 
 * @author alaa.alloush
 *
 */
//@WebServlet("/debug")
public class SendingMessage /*extends HttpServlet*/ {

//von mitja
 @Inject
 private JMSContext context;
 @Resource(mappedName = "java:/topic/core")
 private Topic topic;
  

 public void send(){
	 Message message = context.createTextMessage("hallo alaa");
	 	 
//	String filter = "";
    try {
    	message.setStringProperty("METHOD_TYPE", "Describe"); // should be INFORM
//      message.setJMSCorrelationID(UUID.randomUUID().toString());
//      filter = "JMSCorrelationID='" + message.getJMSCorrelationID() + "'";
    } catch (JMSException e) {
    	e.printStackTrace();
    }
    context.createProducer().send(topic, message);
	 
//    Message rcvMessage = context.createConsumer(topic, filter).receive(10000);
 }
	
//	private static final long serialVersionUID = -8314035702649252239L;

	//private static final int MSG_COUNT = 5;

//	@Resource(mappedName = "java:/ConnectionFactory")
//	private ConnectionFactory connectionFactory;

	//@Resource(mappedName = "java:/topic/adapters")
	//private Topic topic;

//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		resp.setContentType("text/html");
//		PrintWriter out = resp.getWriter();
//		Connection connection = null;
//		out.write("<h1>Adapter<h1><h2>Docker</h2><p>Example demonstrates the use of <strong>JMS 1.1</strong> and <strong>EJB 3.1 Message-Driven Bean</strong> in WildFly to define an adapter.</p>");
//		try {
//			Destination destination = topic;
//			out.write("<p>Sending messages to <em>" + destination + "</em></p>");
//			connection = connectionFactory.createConnection();
//			Session session = connection.createSession(false,
//					Session.AUTO_ACKNOWLEDGE);
//			MessageProducer messageProducer = session
//					.createProducer(destination);
//			connection.start();
//
//			out.write("<h3>Following messages will be send to the destination:</h3>");
//			TextMessage message = session.createTextMessage();
//			
//			//for (int i = 0; i < MSG_COUNT; i++) {
//				//message.setText("This is my message ");
//				ResourceInformation resourceInformation = new ResourceInformation();
//				message.setText(resourceInformation.toString());
//			//	message.setStringProperty(arg0, arg1);
///*				if (i / 2 == 0)
//					message.setStringProperty(AdapterMDB.PROPERTY_STATUS, AdapterMDB.STATUS_STARTED);
//				else
//					message.setStringProperty(AdapterMDB.PROPERTY_STATUS, AdapterMDB.STATUS_STOPPED);
//*/				messageProducer.send(message);
////	 	 		out.write("Message (" + i + "): " + AdapterMDB.toDebugString(message)
////						+ "</br>");
//		//	}
//			out.write("<p><i>Go to your JBoss Application Server console or Server log to see the result of messages processing</i></p>");
//
//			out.write("<p><i>Searching for adapters...</i></p>");
////			message.setJMSType(AdapterMDB.TYPE_QUERY);
//			messageProducer.send(message);
//		} catch (JMSException e) {
//			e.printStackTrace();
//			out.write("<h2>A problem occurred during the delivery of this message</h2>");
//			out.write("</br>");
//			out.write("<p><i>Go your the JBoss Application Server console or Server log to see the error stack trace</i></p>");
//		} finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (JMSException e) {
//					e.printStackTrace();
//				}
//			}
//			if (out != null) {
//				out.close();
//			}
//		}
//	}


//	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		doGet(req, resp);
//	}
//
//}


/*public class SendingMessage {

	@Resource(mappedName = "java:/ConnectionFactory") // or java: instead of jms
	private ConnectionFactory connectionFactory;
	
	@Resource(mappedName = "java:/topic/adapters")
	private Topic topic;
	
	Connection connection = null;
	
	Destination destination = null;
	
	public void sending(String str){
		
		try {
			destination = (Destination) topic; 
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(destination);
			connection.start(); // maybe not required.
			TextMessage message = session.createTextMessage();
			message.setText("Hello"); // it should be ResourceInformation
			//message.setStringProperty(METHOD_TYPE, TYPE);
			message.setStringProperty(,);
			messageProducer.send(message);
		}
		catch (JMSException e) {
			e.printStackTrace();
		}
		finally { 
			if (connection != null) {
				 try { 
					 connection.close(); 
					 } 
				 catch (JMSException e) {
					 e.printStackTrace();
				 }
			}
		}
		
	}
}*/
}
