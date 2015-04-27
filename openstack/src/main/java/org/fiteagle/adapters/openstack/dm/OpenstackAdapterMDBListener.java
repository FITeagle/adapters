package org.fiteagle.adapters.openstack.dm;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import com.hp.hpl.jena.rdf.model.Model;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageFilters;
import org.fiteagle.api.core.MessageUtil;

@MessageDriven(name = "OpenstackAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue =IMessageBus.METHOD_TARGET + " = '" +"http://open-multinet.info/ontology/omn-domain-pc#VMServer"  + "'"
            + "AND ("+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_CREATE+"' "
            + "OR "+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_CONFIGURE+"' "
            + "OR "+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_GET+"' "
            + "OR "+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_DELETE+"')"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OpenstackAdapterMDBListener  implements MessageListener {
  

  protected Map<String, OpenstackAdapter> getAdapterInstances() {
    return OpenstackAdapter.adapterInstances;
  }
    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBListener.class.toString());


    public void onMessage(final Message message) {
        String messageType = MessageUtil.getMessageType(message);
        String serialization = MessageUtil.getMessageSerialization(message);
        String rdfString = MessageUtil.getStringBody(message);

        if (messageType != null && rdfString != null) {
            Model messageModel = MessageUtil.parseSerializedModel(rdfString, serialization);

            for(OpenstackAdapter adapter : getAdapterInstances().values()){
                if (adapter.isRecipient(messageModel)) {
                    LOGGER.log(Level.INFO, "Received a " + messageType + " message");
                    try{
                        if (messageType.equals(IMessageBus.TYPE_CREATE)) {
                            Model resultModel = adapter.createInstances(messageModel);
                            adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);

                        } else if (messageType.equals(IMessageBus.TYPE_CONFIGURE)) {
                            Model resultModel = adapter.updateInstances(messageModel);
                            adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);

                        } else if (messageType.equals(IMessageBus.TYPE_DELETE)) {
                            Model resultModel = adapter.deleteInstances(messageModel);
                            adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);

                        } else if (messageType.equals(IMessageBus.TYPE_GET)) {
                            Model resultModel = adapter.getInstances(messageModel);
                            adapter.notifyListeners(resultModel, MessageUtil.getJMSCorrelationID(message), IMessageBus.TYPE_INFORM, null);
                        }
                    } catch(AbstractAdapter.ProcessingException | AbstractAdapter.InvalidRequestException | AbstractAdapter.InstanceNotFoundException e){
                        Message errorMessage = MessageUtil.createErrorMessage(e.getMessage(), MessageUtil.getJMSCorrelationID(message), context);
                        context.createProducer().send(topic, errorMessage);
                    }
                }
            }
        }
    }

}