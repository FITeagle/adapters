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
      String serialization = requestMessage.getStringProperty(IMessageBus.SERIALIZATION);
      String rdfString = MessageUtil.getStringBody(requestMessage);
      
      if (methodType != null && rdfString != null) {
        Model messageModel = MessageUtil.parseSerializedModel(rdfString, serialization);
        
        if (adapterIsRecipient(messageModel)) {
          if (methodType.equals(IMessageBus.TYPE_CREATE)) {
            LOGGER.log(Level.INFO, this.getClass().getSimpleName() + " : Received a " + methodType + " message");
            handleCreateModel(messageModel, requestMessage.getJMSCorrelationID());
            
          } else if (methodType.equals(IMessageBus.TYPE_CONFIGURE)) {
            LOGGER.log(Level.INFO, this.getClass().getSimpleName() + " : Received a " + methodType + " message");
            handleConfigureModel(messageModel, requestMessage.getJMSCorrelationID());
            
          } else if (methodType.equals(IMessageBus.TYPE_DELETE)) {
            LOGGER.log(Level.INFO, this.getClass().getSimpleName() + " : Received a " + methodType + " message");
            handleDeleteModel(messageModel, requestMessage.getJMSCorrelationID());
          }
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
      Message errorMessage = MessageUtil.createErrorMessage(Response.Status.CONFLICT.name(), requestID, context);
      context.createProducer().send(topic, errorMessage);
      return;
    }
    
    if (createdInstancesModel.isEmpty()) {
      LOGGER.log(Level.INFO, "Could not find any instances to create");
      Message errorMessage = MessageUtil.createErrorMessage(Response.Status.BAD_REQUEST.name(), requestID, context);
      context.createProducer().send(topic, errorMessage);
      return;
    }
    
    getAdapter().notifyListeners(createdInstancesModel, requestID, IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);
  }
  
  private void handleDeleteModel(Model modelRelease, String requestID) {
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
      Message errorMessage = MessageUtil.createErrorMessage(Response.Status.NOT_FOUND.name(), requestID, context);
      context.createProducer().send(topic, errorMessage);
      return;
    }
    
    getAdapter().notifyListeners(releasedInstancesModel, requestID, IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);
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
      Message errorMessage = MessageUtil.createErrorMessage(Response.Status.NOT_FOUND.name(), requestID, context);
      context.createProducer().send(topic, errorMessage);
      return;
    }
    
    getAdapter().notifyListeners(configuredInstancesModel, requestID, IMessageBus.TYPE_INFORM,  IMessageBus.TARGET_ORCHESTRATOR);
  }
  
  private boolean adapterIsRecipient(Model messageModel) {
    return messageModel.containsResource(getAdapter().getAdapterInstance());
  }
  
  private StmtIterator getResourceInstanceIterator(Model model) {
    return model.listStatements(null, RDF.type, getAdapter().getAdapterManagedResource());
  }
  
  private Model createInformReleaseModel(String instanceName) {
    Model model = ModelFactory.createDefaultModel();
    getAdapter().setModelPrefixes(model);
    Resource resource = getAdapter().getAdapterManagedResource();
    model.add(resource, RDF.type, getAdapter().getAdapterManagedResource());
    return model;
  }
  
}
