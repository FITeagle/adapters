package org.fiteagle.abstractAdapter.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterMDBListener implements MessageListener {
  
  private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBListener.class.toString());
  
  @Inject
  private JMSContext context;
  @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
  private Topic topic;
  
  protected abstract AbstractAdapter getAdapter();
  
  public void onMessage(final Message requestMessage) {
    try {
      String methodType = requestMessage.getStringProperty(IMessageBus.METHOD_TYPE);
      String rdfString = MessageUtil.getRDFResult(requestMessage);
      if(rdfString == null ){
        return;
      }
      Model messageModel = MessageUtil.parseSerializedModel(rdfString);
      
      if (methodType != null && messageModel != null) {
        
        AbstractAdapterMDBListener.LOGGER.log(Level.INFO, this.getClass().getSimpleName() + " : Received a " + methodType + " message");
        if (adapterIsRecipient(messageModel)) {
          if (methodType.equals(IMessageBus.TYPE_CREATE)
              && MessageUtil.isMessageType(messageModel, MessageBusOntologyModel.propertyFiteagleCreate)) {
            handleCreateModel(messageModel, requestMessage.getJMSCorrelationID());
            
          } else if (methodType.equals(IMessageBus.TYPE_CONFIGURE)
              && MessageUtil.isMessageType(messageModel, MessageBusOntologyModel.propertyFiteagleConfigure)) {
            handleConfigureModel(messageModel, requestMessage.getJMSCorrelationID());
            
          } else if (methodType.equals(IMessageBus.TYPE_RELEASE)
              && MessageUtil.isMessageType(messageModel, MessageBusOntologyModel.propertyFiteagleRelease)) {
            handleReleaseModel(messageModel, requestMessage.getJMSCorrelationID());
            
          } else if (methodType.equals(IMessageBus.TYPE_INFORM)
              && MessageUtil.isMessageType(messageModel, MessageBusOntologyModel.propertyFiteagleInform)
              && messageModel.contains(null, MessageBusOntologyModel.methodRestores, getAdapter().getAdapterInstance())) {
            // Does this inform message restore this adapter instance (this is the only kind of inform message the adapter is interested in)
            handleCreateModel(messageModel, requestMessage.getJMSCorrelationID());
          }
        }
        
        // DISCOVER message needs not to check for adapterIsRecipient()
        if (methodType.equals(IMessageBus.TYPE_DISCOVER)
            && MessageUtil.isMessageType(messageModel, MessageBusOntologyModel.propertyFiteagleDiscover)) {
            handleDiscoverModel(messageModel, requestMessage.getJMSCorrelationID());
        }
      }
      
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }
  
  private void handleCreateModel(Model modelCreate, String requestID) {
    Model createdInstancesModel = ModelFactory.createDefaultModel();
    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(modelCreate);
    
    LOGGER.log(Level.INFO, "Searching for resources to create...");
    Boolean createdAtLeastOne = false;
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceToCreate = resourceInstanceIterator.next().getSubject();
      
      String instanceName = resourceToCreate.getLocalName();
      if (getAdapter().createInstance(instanceName, modelCreate)) {
        createdAtLeastOne = true;
        LOGGER.log(Level.INFO, "Created instance: " + resourceToCreate);
        Model createdInstanceValues = getAdapter().getSingleInstanceModel(instanceName);
        createdInstancesModel.add(createdInstanceValues);
      }
      
    }
    if (createdAtLeastOne == false) {
      LOGGER.log(Level.INFO, "Could not find any new instances to create");
      sendErrorResponseMessage(Response.Status.CONFLICT.name(), requestID);
      return;
    }
    
    if (createdInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to create");
      sendErrorResponseMessage(Response.Status.BAD_REQUEST.name(), requestID);
      return;
    }
    
    getAdapter().notifyListeners(createdInstancesModel, requestID);
  }
  
  private void handleReleaseModel(Model modelRelease, String requestID) {
    Model releasedInstancesModel = ModelFactory.createDefaultModel();
    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(modelRelease);
    
    LOGGER.log(Level.INFO, "Searching for resources to release...");
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceToRelease = resourceInstanceIterator.next().getSubject();
      
      LOGGER.log(Level.INFO, "Releasing instance: " + resourceToRelease);
      String instanceName = resourceToRelease.getLocalName();
      if (getAdapter().terminateInstance(instanceName)) {
        releasedInstancesModel.add(createInformReleaseModel(instanceName));
      }
    }
    
    if (releasedInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to release");
      sendErrorResponseMessage(Response.Status.NOT_FOUND.name(), requestID);
      return;
    }
    
    getAdapter().notifyListeners(releasedInstancesModel, requestID);
  }
  
  private void handleConfigureModel(Model modelConfigure, String requestID) {
    Model configuredInstancesModel = ModelFactory.createDefaultModel();
    
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(modelConfigure);
    
    LOGGER.log(Level.INFO, "Searching for resources to configure...");
    
    while (resourceInstanceIterator.hasNext()) {
      Resource resourceInstance = resourceInstanceIterator.next().getSubject();
      LOGGER.log(Level.INFO, "Configuring instance: " + resourceInstance);
      
      StmtIterator propertiesIterator = modelConfigure.listStatements(resourceInstance, null, (RDFNode) null);
      while (propertiesIterator.hasNext()) {
        Model changedInstanceValues = getAdapter().configureInstance(propertiesIterator.next());
        configuredInstancesModel.add(changedInstanceValues);
      }
    }
    
    if (configuredInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to configure");
      sendErrorResponseMessage(Response.Status.NOT_FOUND.name(), requestID);
      return;
    }
    
    getAdapter().notifyListeners(configuredInstancesModel, requestID);
  }
  
  private void handleDiscoverModel(Model modelDiscover, String requestID) {
    StmtIterator resourceInstanceIterator = getResourceInstanceIterator(modelDiscover);
    
    while (resourceInstanceIterator.hasNext()) {
      Resource resource = resourceInstanceIterator.next().getSubject();
      
      LOGGER.log(Level.INFO, "Discovering instance: " + resource);
      Model instanceModel = getAdapter().getSingleInstanceModel(resource.getLocalName());
      if (instanceModel == null || instanceModel.isEmpty()) {
        sendErrorResponseMessage(Response.Status.NOT_FOUND.name(), requestID);
        return;
      } else {
        getAdapter().notifyListeners(instanceModel, requestID);
        return;
      }
    }
    // No specific instance requested, show all
    getAdapter().notifyListeners(getAdapter().getAllInstancesModel(), requestID);
  }
  
  private boolean adapterIsRecipient(Model messageModel) {
    return messageModel.contains(getAdapter().getAdapterInstance(), RDF.type, getAdapter().getAdapterType());
  }
  
  private void sendErrorResponseMessage(String result, String requestID) {
    final Message responseMessage = context.createMessage();
    
    try {
      responseMessage.setStringProperty(IMessageBus.TYPE_RESPONSE, IMessageBus.TYPE_INFORM);
      responseMessage.setStringProperty(IMessageBus.TYPE_ERROR, result);
      if (null != requestID) {
        responseMessage.setJMSCorrelationID(requestID);
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    
    this.context.createProducer().send(topic, responseMessage);
  }

  private StmtIterator getResourceInstanceIterator(Model model) {
    return model.listStatements(null, RDF.type, getAdapter().getAdapterManagedResource());
  }
  
  private Model createInformReleaseModel(String instanceName) {
    Model model = ModelFactory.createDefaultModel();
    getAdapter().setModelPrefixes(model);
    Resource releasedInstance = model.createResource(getAdapter().getAdapterInstancePrefix()[1] + instanceName);
    model.add(MessageBusOntologyModel.internalMessage, MessageBusOntologyModel.methodReleases, releasedInstance);
    
    return model;
  }
  
}
