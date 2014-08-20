package org.fiteagle.adapters.mightyrobot.dm;

import org.fiteagle.abstractAdapter.dm.AbstractMDBListener;
import org.fiteagle.api.core.IMessageBus;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;

/**
 * Created by vju on 8/20/14.
 */
@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MightyRobotAdapterMDBListener extends AbstractMDBListener{

    @Override
    public String responseConfigure(Message requestMessage){
         return null;
    }

    @Override
    public String responseDiscover(Message requestMessage) {
        return null;
    }

    @Override
    public String responseCreate(Message requestMessage) {
        return null;
    }

    @Override
    public String responseRelease(Message requestMessage) {
        return null;
    }

    @Override
    public boolean messageBelongsToAdapter(Message requestMessage) {
        System.out.println("Hello From MightyRobotAdapterMDBListener");
        return false;
    }

}
