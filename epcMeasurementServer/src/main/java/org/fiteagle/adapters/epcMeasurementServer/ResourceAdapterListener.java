package org.fiteagle.adapters.epcMeasurementServer;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import javax.jms.Topic;
import javax.naming.NamingException;

import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import org.fiteagle.adapters.common.MessageFilter;
import org.fiteagle.adapters.common.ListenerAdapter;

@MessageDriven(name = "ResourceAdapterListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = MessageFilter.MESSAGE_FILTER),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ResourceAdapterListener implements MessageListener {

	private final static Logger LOGGER = Logger
			.getLogger(ResourceAdapterListener.class.toString());

	private ListenerAdapter listenerAdapter = new ListenerAdapter();
	private EpcMeasurementServerAdapter adapter;

	@Inject
	private JMSContext context;
	@Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	private Topic topic;

	@PostConstruct
	public void setup() throws NamingException {
		this.adapter = EpcMeasurementServerAdapter.getInstance();
	}

	public ResourceAdapterListener() throws JMSException {
	}

	@Override
	public void onMessage(Message rcvMessage) {

		try {
			if (rcvMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
				String response = null;

				if (rcvMessage.getStringProperty(IMessageBus.METHOD_TYPE)
						.equals(IMessageBus.TYPE_DISCOVER.toString())) {
					LOGGER.info("epc-measurement-server adapter received a message from type DISCOVER");
					response = handleDiscover(rcvMessage);
				} else if (ignoreMessage(rcvMessage)) {
					return;
				}
				;
				if (rcvMessage.getStringProperty(IMessageBus.METHOD_TYPE)
						.equals(IMessageBus.TYPE_CREATE.toString())) {
					LOGGER.info("epc-measurement-server adapter received a message from type CREATE");
					response = handleCreate(rcvMessage);
				} else if (rcvMessage
						.getStringProperty(IMessageBus.METHOD_TYPE).equals(
								IMessageBus.TYPE_RELEASE.toString())) {
					LOGGER.info("epc-measurement-server adapter received a message from type RELEASE");
					response = handleRelease(rcvMessage);
				}
				if (response != null) {
					sendResponseMessage(rcvMessage, response);
				}
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean ignoreMessage(Message rcvMessage) {
		boolean hasToIgnore = true;
		try {
			Model model = listenerAdapter.handleIgnoreMessage(rcvMessage);
			StmtIterator stmtiterator = model
					.listStatements(new SimpleSelector(null, RDF.type,
							this.adapter.getResource()));
			while (stmtiterator.hasNext()) {
				hasToIgnore = false;
				break;
			}
		} catch (Exception excep) {
		}
		return hasToIgnore;
	}

	public String handleDiscover(Message rcvMessage) throws JMSException {

		String serialization = rcvMessage
				.getStringProperty(IMessageBus.SERIALIZATION);

		Model modelDiscover = this.adapter.discover(serialization);

		String str = listenerAdapter.handleDiscoverAdapter(modelDiscover);
		return str;
	}

	public String handleCreate(Message rcvMessage) throws JMSException {

		Model model = listenerAdapter.handleCreateAdapter(rcvMessage);
		Model modelCreate = this.adapter.create(model);
		String response = listenerAdapter.createResponse(modelCreate);
		return response;
	}

	public String handleRelease(Message rcvMessage) throws JMSException {

		Model model = listenerAdapter.handleReleaseAdapter(rcvMessage);
		Model modelCreate = this.adapter.release(model);
		String response = listenerAdapter.createResponse(modelCreate);
		return response;
	}

	public void sendResponseMessage(Message rcvMessage, String result)
			throws JMSException {

		final Message responseMessage = this.context.createMessage();

		responseMessage.setJMSCorrelationID(rcvMessage.getJMSCorrelationID());
		responseMessage.setStringProperty(IMessageBus.SERIALIZATION, "TURTLE");

		responseMessage.setStringProperty(IMessageBus.METHOD_TYPE,
				IMessageBus.TYPE_INFORM);

		responseMessage.setStringProperty(IMessageBus.RDF, result);
		this.context.createProducer().send(topic, responseMessage);
	}

}
