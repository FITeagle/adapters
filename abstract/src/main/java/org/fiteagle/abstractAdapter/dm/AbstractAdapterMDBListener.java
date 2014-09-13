package org.fiteagle.abstractAdapter.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class AbstractAdapterMDBListener implements MessageListener {

    private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBListener.class.toString());

    protected AbstractAdapterRDFHandler adapterRDFHandler;

    @Inject
    private JMSContext context;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;
    
    public void onMessage(final Message requestMessage) {
        try {

            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {
                String result = "";
                
                Model modelMessage = MessageBusMsgFactory.getMessageRDFModel(requestMessage);
                
                if(modelMessage != null){
                  if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {
                      AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a discover message");
                      result = responseDiscover(modelMessage);
                      
                  } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CREATE)) {
                      AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a create message");
                      result = responseCreate(modelMessage, requestMessage.getJMSCorrelationID());
  
                  } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_CONFIGURE)) {
                      AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a configure message");
                      result = responseConfigure(modelMessage, requestMessage.getJMSCorrelationID());
  
                  } else if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_RELEASE)) {
                      AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.toString() + " : Received a release message");
                      result = responseRelease(modelMessage, requestMessage.getJMSCorrelationID());
                      
                  }
                }

                if (!result.isEmpty() && !result.equals(IMessageBus.STATUS_200)) {
                    Message responseMessage = generateResponseMessage(requestMessage, result);

                    if (null != requestMessage.getJMSCorrelationID()) {
                        responseMessage.setJMSCorrelationID(requestMessage.getJMSCorrelationID());
                    }

                    this.context.createProducer().send(topic, responseMessage);
                }
            }

        } catch (JMSException e) {
            System.err.println(this.toString() + "JMSException");
        }
    }

    public String responseDiscover(Model modelDiscover) throws JMSException {

        // This is a create message, so do something with it
        if (MessageBusMsgFactory.isMessageType(modelDiscover, MessageBusOntologyModel.propertyFiteagleDiscover)) {
            
            return adapterRDFHandler.parseDiscoverModel(modelDiscover);
        }

        return "Not a valid fiteagle:discover message \n\n";
    }

    public String responseCreate(Model modelCreate, String jmsCorrelationID) throws JMSException {

        // This is a create message, so do something with it
        if (MessageBusMsgFactory.isMessageType(modelCreate, MessageBusOntologyModel.propertyFiteagleCreate)) {            
            return adapterRDFHandler.parseCreateModel(modelCreate, jmsCorrelationID);
        }

        return "Not a valid fiteagle:create message \n\n";
    }

    public String responseConfigure(Model modelConfigure, String jmsCorrelationID) throws JMSException {
        
        // This is a configure message, so do something with it
        if (MessageBusMsgFactory.isMessageType(modelConfigure, MessageBusOntologyModel.propertyFiteagleConfigure)) {
            return adapterRDFHandler.parseConfigureModel(modelConfigure, jmsCorrelationID);
        }
        return "Not a valid fiteagle:configure message \n\n";
    }
    
    public String responseRelease(Model modelRelease, String jmsCorrelationID) throws JMSException {

        // This is a release message, so do something with it
        if (MessageBusMsgFactory.isMessageType(modelRelease, MessageBusOntologyModel.propertyFiteagleRelease)) {
            return adapterRDFHandler.parseReleaseModel(modelRelease, jmsCorrelationID);
        }
        
        return "Not a valid fiteagle:release message \n\n";
    }


    public Message generateResponseMessage(Message requestMessage, String result) throws JMSException {
        final Message responseMessage = this.context.createMessage();

        responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, IMessageBus.TYPE_INFORM);
        responseMessage.setStringProperty(IMessageBus.RDF, result);

        return responseMessage;
    }

}
