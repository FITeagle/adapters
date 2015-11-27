package org.fiteagle.abstractAdapter.dm;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractAdapterMDBListener implements MessageListener {

	@Inject
	protected JMSContext context;
	@javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
	protected Topic topic;

	protected static Logger LOGGER = Logger
			.getLogger(AbstractAdapterMDBListener.class.toString());

	protected abstract Collection<AbstractAdapter> getAdapterInstances();

	public void onMessage(final Message message) {
		String messageType = MessageUtil.getMessageType(message);
		String serialization = MessageUtil.getMessageSerialization(message);
		String rdfString = MessageUtil.getStringBody(message);

		if (messageType != null && rdfString != null) {
			Model messageModel = MessageUtil.parseSerializedModel(rdfString,
					serialization);

			for (AbstractAdapter adapter : getAdapterInstances()) {
				if (adapter.isRecipient(messageModel)) {
					LOGGER.log(Level.INFO, "Received a " + messageType
							+ " message");
					try {
						if (messageType.equals(IMessageBus.TYPE_CREATE)) {
							Model resultModel = adapter
									.createInstances(messageModel);
							adapter.notifyListeners(resultModel,
									MessageUtil.getJMSCorrelationID(message),
									IMessageBus.TYPE_INFORM, null);

						} else if (messageType
								.equals(IMessageBus.TYPE_CONFIGURE)) {

							String modelString = MessageUtil.serializeModel(
									messageModel,
									IMessageBus.SERIALIZATION_TURTLE);
							LOGGER.log(Level.INFO,
									"sendConfigureToResource requestModel: "
											+ modelString);

							Model resultModel = adapter
									.updateInstances(messageModel);
							adapter.notifyListeners(resultModel,
									MessageUtil.getJMSCorrelationID(message),
									IMessageBus.TYPE_INFORM, null);

						} else if (messageType.equals(IMessageBus.TYPE_DELETE)) {
							Model resultModel = adapter
									.deleteInstances(messageModel);
							adapter.notifyListeners(resultModel,
									MessageUtil.getJMSCorrelationID(message),
									IMessageBus.TYPE_INFORM, null);

						} else if (messageType.equals(IMessageBus.TYPE_GET)) {
							Model resultModel = adapter
									.getInstances(messageModel);
							adapter.notifyListeners(resultModel,
									MessageUtil.getJMSCorrelationID(message),
									IMessageBus.TYPE_INFORM, null);
						}
					} catch (ProcessingException | InvalidRequestException
							| InstanceNotFoundException e) {
						Message errorMessage = MessageUtil.createErrorMessage(
								e.getMessage(),
								MessageUtil.getJMSCorrelationID(message),
								context);
						context.createProducer().send(topic, errorMessage);
					}
				}
			}
		}
	}
}
