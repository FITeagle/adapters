/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fiteagle.adapter.docker.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * A simple servlet 3 as client that sends several messages to a queue or a topic.
 * </p>
 * 
 * <p>
 * The servlet is registered and mapped to /HelloWorldMDBServletClient using the {@linkplain WebServlet
 * @HttpServlet}.
 * </p>
 * 
 * @author Serge Pagop (spagop@redhat.com)
 * 
 */
@WebServlet("/debug")
public class DockerAdapterDebugServlet extends HttpServlet {

	private static final long serialVersionUID = -8314035702649252239L;

	private static final int MSG_COUNT = 5;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/topic/adapters")
	private Topic topic;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		Connection connection = null;
		out.write("<h1>Adapter<h1><h2>Docker</h2><p>Example demonstrates the use of <strong>JMS 1.1</strong> and <strong>EJB 3.1 Message-Driven Bean</strong> in WildFly to define an adapter.</p>");
		try {
			Destination destination = topic;
			out.write("<p>Sending messages to <em>" + destination + "</em></p>");
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session
					.createProducer(destination);
			connection.start();

			out.write("<h3>Following messages will be send to the destination:</h3>");
			TextMessage message = session.createTextMessage();
			
			for (int i = 0; i < MSG_COUNT; i++) {
				message.setText("This is message " + (i + 1));
/*				if (i / 2 == 0)
					message.setStringProperty(AdapterMDB.PROPERTY_STATUS, AdapterMDB.STATUS_STARTED);
				else
					message.setStringProperty(AdapterMDB.PROPERTY_STATUS, AdapterMDB.STATUS_STOPPED);
*/				messageProducer.send(message);
//	 	 		out.write("Message (" + i + "): " + AdapterMDB.toDebugString(message)
//						+ "</br>");
			}
			out.write("<p><i>Go to your JBoss Application Server console or Server log to see the result of messages processing</i></p>");

			out.write("<p><i>Searching for adapters...</i></p>");
//			message.setJMSType(AdapterMDB.TYPE_QUERY);
			messageProducer.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
			out.write("<h2>A problem occurred during the delivery of this message</h2>");
			out.write("</br>");
			out.write("<p><i>Go your the JBoss Application Server console or Server log to see the error stack trace</i></p>");
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				out.close();
			}
		}
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
