package org.fiteagle.adapters.testbed.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusMsgFactory;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Created by vju on 9/18/14.
 */

/**
 * on Discover message, this class sends the testbed ontology as inform message over message bus
 */
@MessageDriven(name = "MotorAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class DescribeListenerMDB implements MessageListener {

    @Inject
    private ModelInformerBean mib;
    @Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    private static Logger LOGGER = Logger.getLogger(DescribeListenerMDB.class.toString());


    public void onMessage(final Message requestMessage) {
        try {
        	
            if (requestMessage.getStringProperty(IMessageBus.METHOD_TYPE) != null) {

                Model modelMessage = MessageBusMsgFactory.getMessageRDFModel(requestMessage);

                if (modelMessage != null &&
                		requestMessage.getStringProperty(IMessageBus.METHOD_TYPE).equals(IMessageBus.TYPE_DISCOVER)) {

                        DescribeListenerMDB.LOGGER.log(Level.INFO, this.getClass().getSimpleName() + " : Received a discover message");
                        mib.sendModel(requestMessage.getJMSCorrelationID());

                }
            }

        } catch (Exception e) {
            return;
        }
    }

}

