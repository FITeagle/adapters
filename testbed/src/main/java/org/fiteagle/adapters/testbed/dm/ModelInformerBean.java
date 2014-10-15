package org.fiteagle.adapters.testbed.dm;

import com.hp.hpl.jena.rdf.model.Model;

import org.fiteagle.adapters.testbed.TestbedAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class ModelInformerBean {
    
    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    private static Logger LOGGER = Logger.getLogger(ModelInformerBean.class.toString());
    
    @PostConstruct
    public void sendModel() {
        sendModel("");
    }
    
    public void sendModel(String correlationID) {
        try{
            Model testbedModel = TestbedAdapter.getTestbedModel();
            Model messageModel = MessageBusMsgFactory.createMsgInform(testbedModel);
            String serializedRDF = MessageBusMsgFactory.serializeModel(messageModel);

            final Message eventMessage = this.context.createMessage();

            eventMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_INFORM);
            eventMessage.setStringProperty(IMessageBus.RDF, serializedRDF);
            eventMessage.setStringProperty(IMessageBus.SERIALIZATION, IMessageBus.SERIALIZATION_DEFAULT);
            eventMessage.setJMSCorrelationID(correlationID);
            LOGGER.log(Level.INFO, "Sending Testbed Model as Inform Message");
            this.context.createProducer().send(topic, eventMessage);
            
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


}
