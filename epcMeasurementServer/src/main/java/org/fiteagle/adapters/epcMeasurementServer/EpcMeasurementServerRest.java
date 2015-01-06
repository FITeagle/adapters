package org.fiteagle.adapters.epcMeasurementServer;

import java.io.StringWriter;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
//import org.fiteagle.north.proprietary.rest.NorthboundAPI;

import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

@Path("/testEpcMeasurementServer")
public class EpcMeasurementServerRest {

	@Inject
	private JMSContext context;
	@Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;

	public EpcMeasurementServerRest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sending Create message 
	 * curl -i -X PUT -d @addUser.ttl http://localhost:8080/epcMeasurementServer/testEpcMeasurementServer/add_user_epcServer/
	 * 
	 */
	@PUT
	@Path("add_user_epcServer/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html")
	public void addUser(String rdfInput) {
		String serialization = "TURTLE";

		Model createModel = putNsPrefix(createModel(readModel(rdfInput,
				serialization, createModel())));
		createModel.setNsPrefix("epcMeasurementServer",
				"http://fiteagle.org/ontology/adapter/epcMeasurementServer#");

		String response = "";
		try {
			Message message = this.createRequest(
					modelToString(createModel, serialization), serialization,
					IMessageBus.TYPE_CREATE);
			this.context.createProducer().send(this.topic, message);
			System.out.println("sending creation message ");

			Message rcvMessage = waitForResponse(message.getJMSCorrelationID());
			response = MessageUtil.getRDFResult(rcvMessage);
			System.out.println("a new user added " + response);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sending Release message 
	 * curl -i -X DELETE -d @deleteUser.ttl http://localhost:8080/epcMeasurementServer/testEpcMeasurementServer/delete_user_epcServer/
	 * 
	 * @param rdfInput
	 */
	@DELETE
	@Path("delete_user_epcServer/")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html")
	public void deleteUserEpcServer(String rdfInput) {
		String serialization = "TURTLE";

		Model releaseModel = putNsPrefix(releaseModel(readModel(rdfInput,
				serialization, createModel())));
		releaseModel.setNsPrefix("epcMeasurementServer",
				"http://fiteagle.org/ontology/adapter/epcMeasurementServer#");

		String response = "";
		try {
			Message message = this.createRequest(
					modelToString(releaseModel, serialization), serialization,
					IMessageBus.TYPE_DELETE);
			this.context.createProducer().send(this.topic, message);
			System.out.println("sending delete message ");

			Message rcvMessage = waitForResponse(message.getJMSCorrelationID());
			response = MessageUtil.getRDFResult(rcvMessage);
			System.out.println("user deleted " + response);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private Model createModel() {
		Model rdfModel = ModelFactory.createDefaultModel();
		return rdfModel;
	}

	private Model readModel(String modelString, String serialization,
			Model rdfModel) {
		InputStream is = new ByteArrayInputStream(modelString.getBytes());
		rdfModel.read(is, null, serialization);
		return rdfModel;
	}

	private Model createModel(Model rdfModel) {

		com.hp.hpl.jena.rdf.model.Resource ResourceType = rdfModel
				.createResource("http://fiteagle.org/ontology/adapter/epcMeasurementServer#EpcMeasurementServer");
		com.hp.hpl.jena.rdf.model.Resource fuseco = rdfModel
				.createResource("http://fiteagleinternal#");
		fuseco.addProperty(RDF.type, ResourceType);

		return rdfModel;
	}

	private Model releaseModel(Model rdfModel) {

		com.hp.hpl.jena.rdf.model.Resource ResourceType = rdfModel
				.createResource("http://fiteagle.org/ontology/adapter/epcMeasurementServer#EpcMeasurementServer");
		com.hp.hpl.jena.rdf.model.Resource fuseco = rdfModel
				.createResource("http://fiteagleinternal#");
		fuseco.addProperty(RDF.type, ResourceType);

		return rdfModel;
	}

	private Model putNsPrefix(Model rdfModel) {
		rdfModel.setNsPrefix("", "http://fiteagleinternal#");
		rdfModel.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
		return rdfModel;
	}

	private Message createRequest(final String rdfInput,
			final String serialization, String methodType) throws JMSException {
		final Message message = this.context.createTextMessage(rdfInput);

		message.setStringProperty(IMessageBus.METHOD_TYPE, methodType);
		message.setStringProperty(IMessageBus.SERIALIZATION, serialization);
		message.setJMSCorrelationID(UUID.randomUUID().toString());

		return message;
	}

	private String modelToString(Model model, String serialization) {
		StringWriter writer = new StringWriter();
		model.write(writer, serialization);
		return writer.toString();
	}

	private Message waitForResponse(final String correlationID)
			throws JMSException {

		final String filter = "JMSCorrelationID='" + correlationID + "'";
		final Message response = this.context
				.createConsumer(this.topic, filter).receive(5000);
		return response;
	}
}
