package org.fiteagle.adapters.common;

import javax.jms.JMSException;
import javax.jms.Message;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.logging.Logger;

import org.apache.jena.riot.RiotException;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class ListenerAdapter {

	private final static Logger LOGGER = Logger.getLogger(ListenerAdapter.class
			.toString());

	public ListenerAdapter() throws JMSException {
	}

	public Model handleIgnoreMessage(Message rcvMessage) {

		Model model = ModelFactory.createDefaultModel();
		try {
			String rdf = rcvMessage.getStringProperty(IMessageBus.RDF);
			InputStream inputStream = new ByteArrayInputStream(rdf.getBytes());
			model.read(inputStream, null,
					rcvMessage.getStringProperty(IMessageBus.SERIALIZATION));
		} catch (Exception excep) {
		}
		return model;
	}

	public String handleDiscoverAdapter(Model modelDiscover)
			throws JMSException {

		com.hp.hpl.jena.rdf.model.Resource message = modelDiscover
				.createResource("http://fiteagleinternal#Message");
		message.addProperty(RDF.type,
				MessageBusOntologyModel.propertyFiteagleInform);
		modelDiscover.setNsPrefix("", "http://fiteagleinternal#");

		StringWriter writer = new StringWriter();
		modelDiscover.write(writer, "TURTLE");
		System.out.println("finishing handleDiscoveryAdapter");
		return writer.toString();
	}

	public Model handleCreateAdapter(Message rcvMessage) throws JMSException {

		Model model = createModel(rcvMessage);
		if (rcvMessage.getStringProperty(IMessageBus.RDF) != null) {
			try {
				StmtIterator stmtiterator = model
						.listStatements(new SimpleSelector(null, RDF.type,
								MessageBusOntologyModel.propertyFiteagleCreate));

				return handleStatement(model, stmtiterator);
			} catch (RiotException e) {
				System.err.println("Invalid RDF");
			}
		}
		return model;
	}

	public Model handleReleaseAdapter(Message rcvMessage) throws JMSException {

		Model model = createModel(rcvMessage);
		if (rcvMessage.getStringProperty(IMessageBus.RDF) != null) {
			try {
				StmtIterator stmtiterator = model
						.listStatements(new SimpleSelector(null, RDF.type,
								MessageBusOntologyModel.propertyFiteagleRelease));

				return handleStatement(model, stmtiterator);
			} catch (RiotException e) {
				System.err.println("Invalid RDF");
			}
		}
		return model;
	}

	private Model handleStatement(Model model, StmtIterator stmtiterator) {

		Statement statement = null;
		while (stmtiterator.hasNext()) {
			statement = stmtiterator.nextStatement();
		}
		if (statement != null) {
			model.remove(statement);
		}
		return model;
	}

	private Model createModel(Message rcvMessage) throws JMSException {

		Model model = ModelFactory.createDefaultModel();
		try {
			String rdf = rcvMessage.getStringProperty(IMessageBus.RDF);
			InputStream inputstream = new ByteArrayInputStream(rdf.getBytes());
			model.read(inputstream, null,
					rcvMessage.getStringProperty(IMessageBus.SERIALIZATION));
		} catch (RiotException e) {
			System.err.println("Invalid RDF");
		}
		return model;
	}

	public String createResponse(Model model) {
		com.hp.hpl.jena.rdf.model.Resource message = model
				.createResource("http://fiteagleinternal#Message");
		message.addProperty(RDF.type,
				MessageBusOntologyModel.propertyFiteagleInform);
		model.setNsPrefix("", "http://fiteagleinternal#");

		StringWriter writer = new StringWriter();
		model.write(writer, "TURTLE");
		return writer.toString();

	}

}
