package org.fiteagle.adapters.motor.dm;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import java.util.Iterator;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageFilters;

@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = MessageFilters.FILTER_ADAPTER),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDBListener extends AbstractAdapterMDBListener {

	private static MotorAdapter adapter;
	
    @Override
    protected AbstractAdapter getAdapter() {
    	if(adapter == null){
        Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
        if(iterator.hasNext()){
        	adapter = MotorAdapter.getInstance(iterator.next());
        }
        }
        return adapter;
    }

}
