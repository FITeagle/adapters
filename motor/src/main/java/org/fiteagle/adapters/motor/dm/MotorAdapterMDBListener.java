package org.fiteagle.adapters.motor.dm;

import java.util.Collection;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.motor.MotorAdapterControl;
import org.fiteagle.api.core.IMessageBus;

@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
	@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IMessageBus.METHOD_TARGET + " = '"
		+ "http://open-multinet.info/ontology/resource/motorgarage#MotorGarage" + "'" + "AND ("
		+ IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE + "' " + "OR " + IMessageBus.METHOD_TYPE
		+ " = '" + IMessageBus.TYPE_CONFIGURE + "' " + "OR " + IMessageBus.METHOD_TYPE + " = '"
		+ IMessageBus.TYPE_GET + "' " + "OR " + IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_DELETE
		+ "')"),
	@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDBListener extends AbstractAdapterMDBListener {

    @EJB
    private transient MotorAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }

}
