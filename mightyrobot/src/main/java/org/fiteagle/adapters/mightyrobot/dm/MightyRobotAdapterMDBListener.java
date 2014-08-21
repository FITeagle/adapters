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
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.SimpleSelector;


/**
 * Created by vju on 8/20/14.
 */
@MessageDriven(name = "MightyRobotAdapterMDB", activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class MightyRobotAdapterMDBListener extends AbstractMDBListener {

    private static Logger LOGGER = Logger.getLogger(MightyRobotAdapterMDBListener.class.toString());

    @Inject
    MightyRobotAdapter mightyRobotAdapter;

    @Override
    public AbstractAdapter getAdapter(){
        return mightyRobotAdapter;
    }

    @Override
    public Logger getLogger(){
        return LOGGER;
    }

}
