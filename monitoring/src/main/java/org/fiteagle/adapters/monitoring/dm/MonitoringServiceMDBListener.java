package org.fiteagle.adapters.monitoring.dm;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Topic;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.monitoring.MonitoringService;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_INFORM+"'"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })

public class MonitoringServiceMDBListener extends AbstractAdapterMDBListener {
	@Inject
	private JMSContext context;
	@javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;
	private MonitoringService adapter = MonitoringService.getInstance() ; 
	
	private static Logger LOGGER = Logger.getLogger(MonitoringServiceMDBListener.class.toString());
	
	@Override
	protected Map<String, AbstractAdapter> getAdapterInstances() {
		// TODO Auto-generated method stub
		return MonitoringService.adapterInstances;
	}
	
	@Override
	public void onMessage(final Message message) {
		String messageType = MessageUtil.getMessageType(message);
        String serialization = MessageUtil.getMessageSerialization(message);
        String messageBody = MessageUtil.getStringBody(message);
        LOGGER.log(Level.INFO, "Received an " + messageType + " message");
        
        if (messageType != null && messageBody != null) {
        	if (messageType.equals(IMessageBus.TYPE_INFORM)) {
        		Model model = MessageUtil.parseSerializedModel(messageBody,serialization);
        		this.adapter.handleInform(model);
        	}
        }
	}
  
}
