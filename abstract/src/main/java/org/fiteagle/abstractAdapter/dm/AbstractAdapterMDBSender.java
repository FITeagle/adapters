package org.fiteagle.abstractAdapter.dm;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterEventListener;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterMDBSender {
    
    private AbstractAdapter adapter;
    
    private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBSender.class.toString());
    
    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    protected void startup(AbstractAdapter adapter) {
      this.adapter = adapter;

      adapter.addChangeListener(new AdapterEventListener() {

        @Override
        public void rdfChange(Model eventRDF, String requestID) {
            sendInformMessage(eventRDF, requestID);                
        }
      });
    }

    protected abstract void startup();
    
    public void sendInformMessage(Model eventRDF, String requestID) {
        try {
            
            Model messageModel = MessageBusMsgFactory.createMsgInform(eventRDF);
            messageModel.add(adapter.getAdapterInstance(), RDF.type, adapter.getAdapterType());

            String serializedRDF = MessageBusMsgFactory.serializeModel(messageModel);

            String correlationID = "";
            if(requestID == null || requestID.isEmpty()){
                correlationID = UUID.randomUUID().toString();
            } else {
                correlationID = requestID;
            }
            
            final Message eventMessage = this.context.createMessage();
            eventMessage.setJMSCorrelationID(correlationID);
            eventMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_INFORM);
            eventMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            eventMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
            
            this.context.createProducer().send(topic, eventMessage);
        } catch (JMSException e) {
            System.err.println("JMSException in AbstractAdapterMDBSender");
        }
    }
    

}




