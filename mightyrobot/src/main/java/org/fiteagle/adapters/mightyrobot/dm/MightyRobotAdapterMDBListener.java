package org.fiteagle.adapters.mightyrobot.dm;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractMDBListener;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;
import org.fiteagle.api.core.IMessageBus;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.hp.hpl.jena.rdf.model.SimpleSelector;


/**
 * Created by vju on 8/20/14.
 */
@MessageDriven(name = "MotorAdapterMDB", activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class MightyRobotAdapterMDBListener extends AbstractMDBListener {

    @Inject
    MightyRobotAdapter mightyRobotAdapter;

    @Override
    public AbstractAdapter getAdapter(){
        return mightyRobotAdapter;
    }

    @Override
    public String responseConfigure(Message requestMessage) {
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

}
