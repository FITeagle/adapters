package org.fiteagle.adapter.docker.mdb;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * A simple Message Driven Bean that asynchronously receives and processes the
 * messages that are sent to the topic.
 * 
 * @author Alexander Willner (alexander.willner@tu-berlin.de)
 * 
 */
@MessageDriven(name = "DockerAdapterMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "topic/adapters"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class DockerAdapter extends AbstractAdapter implements MessageListener {

	private DockerAdapter() throws JMSException {
		super();
	}

	private final static Logger LOGGER = Logger.getLogger(DockerAdapter.class
			.toString());

	private static volatile DockerAdapter instance = null;

	public static DockerAdapter getInstance() throws JMSException {
		if (instance == null) {
			synchronized (DockerAdapter.class) {
				if (instance == null) {
					instance = new DockerAdapter();
				}
			}
		}
		return instance;
	}

	public void onMessage(Message rcvMessage) {
//			if (AdapterMDB.isQuery(rcvMessage)) {
//				advertiseAdapter();
//			}

			TextMessage msg = null;
			if (rcvMessage instanceof TextMessage) {
				msg = (TextMessage) rcvMessage;
				LOGGER.info("The Docker adapter received a message from adapter topic: ");
//						+ AdapterMDB.toDebugString(msg));
			} else {
				LOGGER.warning("The Docker adapter received a message of wrong type: "
						+ rcvMessage.getClass().getName());
			}	
	}
}
