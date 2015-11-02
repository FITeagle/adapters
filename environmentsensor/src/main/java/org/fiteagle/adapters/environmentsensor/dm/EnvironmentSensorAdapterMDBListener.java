package org.fiteagle.adapters.environmentsensor.dm;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;

import com.hp.hpl.jena.rdf.model.Model;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.environmentsensor.EnvironmentSensorAdapter;
import org.fiteagle.adapters.environmentsensor.EnvironmentSensorAdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
	@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "(" +IMessageBus.METHOD_TARGET + " = '"
		+ "http://open-multinet.info/ontology/resource/fs20#FS20" + "'" + "AND ("
		+ IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE + "' " + "OR " + IMessageBus.METHOD_TYPE
		+ " = '" + IMessageBus.TYPE_CONFIGURE + "' " + "OR " + IMessageBus.METHOD_TYPE + " = '"
		+ IMessageBus.TYPE_GET + "' " + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE +"'))" +
			" OR "+ IMessageBus.METHOD_TYPE +"= '"+IMessageBus.TYPE_INFORM
		+ "'"),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class EnvironmentSensorAdapterMDBListener extends AbstractAdapterMDBListener {

	private static Logger LOGGER = Logger.getLogger(EnvironmentSensorAdapterMDBListener.class.getName());
    @EJB
    private transient EnvironmentSensorAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }

	@Override
	public void onMessage(final Message message) {
		String messageType = MessageUtil.getMessageType(message);
		String serialization = MessageUtil.getMessageSerialization(message);
		String messageBody = MessageUtil.getStringBody(message);
		LOGGER.log(Level.INFO, "Received an " + messageType + " message");

		if (messageType != null && messageBody != null) {
			if (messageType.equals(IMessageBus.TYPE_INFORM)) {
				Model model = MessageUtil.parseSerializedModel(messageBody,serialization);

				for(AbstractAdapter aadapter : getAdapterInstances()){
					EnvironmentSensorAdapter adapter = (EnvironmentSensorAdapter)aadapter;
					adapter.handleInform(model);
				}

			} else
				super.onMessage(message);
		}
	}

}
