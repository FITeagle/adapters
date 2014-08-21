package org.fiteagle.adapters.motor.dm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.apache.jena.riot.RiotException;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractMDBListener;
import org.fiteagle.adapters.motor.MotorAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.IResourceRepository;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

// TODO: Welches topic? Wie nur einen Adapter ansprechen?

@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MotorAdapterMDBListener extends AbstractMDBListener {

    private static Logger LOGGER = Logger.getLogger(MotorAdapterMDBListener.class.toString());

    private MotorAdapter adapter;

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    @PostConstruct
    public void setup() throws NamingException {
        this.adapter = MotorAdapter.getInstance();
    }
    @Override
    public AbstractAdapter getAdapter(){
        return this.adapter;
    }
    @Override
    public Logger getLogger(){
        return LOGGER;
    }

    public String getSerialization(Message message) throws JMSException {
        return message.getStringProperty(IMessageBus.SERIALIZATION);
    }

    public int getInstanceID(Message message) throws JMSException {
        // return Integer.parseInt(message.getStringProperty(IResourceRepository.PROP_INSTANCE_ID));
        return 10;
    }



}
