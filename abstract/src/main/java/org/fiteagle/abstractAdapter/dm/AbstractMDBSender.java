package org.fiteagle.abstractAdapter.dm;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import java.io.StringWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vju on 8/22/14.
 */
@Singleton(name = "AbstractMDBSender")
@Startup
@Remote(IAbstractMDBSender.class)
public abstract class AbstractMDBSender implements IAbstractMDBSender {

    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    private AbstractAdapter adapter;
    //private Property propertyInform;

    @SuppressWarnings("unused")
    @PostConstruct
    private void startup() {
        adapter = getAdapter();
        Logger LOGGER = getLogger();
        LOGGER.log(Level.INFO, "Created AdapterMDBSender");
        adapter.addChangeListener(new AdapterEventListener() {
            @Override
            public void rdfChange(Model eventRDF) {
                sendInformMessage(eventRDF);

            }
        });

    }

    public void registerAdapter() {
        sendInformMessage(adapter.getAdapterDescriptionModel("TURTLE"));
    }

    // TODO: What message to send for releasing the adapter?
    public void unregisterAdapter() {
        //sendInformMessage("Unregister Motor Adapter");
    }

    private String getSerializedRdfFromEventModel(Model eventRDF){
        //Model messageRDF = ModelFactory.createDefaultModel();

        com.hp.hpl.jena.rdf.model.Resource message = eventRDF.createResource("http://fiteagleinternal#Message");
        message.addProperty(RDF.type, MessageBusOntologyModel.propertyFiteagleInform);

        eventRDF.setNsPrefix("", "http://fiteagleinternal#");

        StringWriter writer = new StringWriter();

        eventRDF.write(writer, "TURTLE");

        return writer.toString();
    }

    public void sendInformMessage(Model eventRDF) {
        try {

            final Message eventMessage = this.context.createMessage();

            String serializedRDF = getSerializedRdfFromEventModel(eventRDF);

            eventMessage.setJMSCorrelationID(UUID.randomUUID().toString());
            eventMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_INFORM);
            eventMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            eventMessage.setStringProperty(IMessageBus.SERIALIZATION, "TURTLE");

            this.context.createProducer().send(topic, eventMessage);
        } catch (JMSException e) {
            System.err.println("JMSException");
        }
    }

    public abstract AbstractAdapter getAdapter();
    public abstract Logger getLogger();

}
