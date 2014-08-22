package org.fiteagle.adapters.motor.dm;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.abstractAdapter.dm.AbstractMDBSender;
import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.adapters.motor.dm.IMotorAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

@Singleton(name = "MotorAdapterMDBSender")
@Startup
@Remote(IMotorAdapterMDBSender.class)
public class MotorAdapterMDBSender extends AbstractMDBSender {

    private static Logger LOGGER = Logger.getLogger(MotorAdapterMDBSender.class.toString());

    @Override
    public AbstractAdapter getAdapter(){
        return  MotorAdapter.getInstance();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
