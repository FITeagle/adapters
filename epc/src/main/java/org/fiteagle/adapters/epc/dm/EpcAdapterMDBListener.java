package org.fiteagle.adapters.epc.dm;

import java.util.Collection;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.epc.EpcAdapterControl;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IMessageBus.METHOD_TARGET
				+ " = '"
				+ "http://open-multinet.info/ontology/resource/epc#EpcAdapter"
				+ "'"
				+ "AND ("
				+ IMessageBus.METHOD_TYPE
				+ " = '"
				+ IMessageBus.TYPE_CREATE
				+ "' "
				+ "OR "
				+ IMessageBus.METHOD_TYPE
				+ " = '"
				+ IMessageBus.TYPE_CONFIGURE
				+ "' "
				+ "OR "
				+ IMessageBus.METHOD_TYPE
				+ " = '"
				+ IMessageBus.TYPE_GET
				+ "' "
				+ "OR "
				+ IMessageBus.METHOD_TYPE
				+ " = '" + IMessageBus.TYPE_DELETE + "')"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class EpcAdapterMDBListener extends AbstractAdapterMDBListener {

	@EJB
	private transient EpcAdapterControl controller;

	@Override
	protected Collection<AbstractAdapter> getAdapterInstances() {
		return this.controller.getAdapterInstances();
	}

}
