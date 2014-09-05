package org.fiteagle.abstractAdapter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class AbstractAdapterRDFHandler {

    private static Logger LOGGER = Logger.getLogger(AbstractAdapterRDFHandler.class.toString());

    protected AbstractAdapter adapter;

    private StmtIterator getResourceInstanceIterator(Model model) {
        return model.listStatements(new SimpleSelector(null, RDF.type, adapter.getAdapterManagedResource()));
    }

    public String parseCreateModel(Model modelCreate, String requestID) {
        
        Model createdInstancesModel = ModelFactory.createDefaultModel();
        adapter.setModelPrefixes(createdInstancesModel);
        
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelCreate);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to create...");

        Statement currentMotorStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentMotorStatement = iteratorMotorResource.nextStatement();

            String instanceName = currentMotorStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Creating instance: " + instanceName + " (" + currentMotorStatement.toString() + ")");

            if(adapter.createInstance(instanceName)){
                Model createdInstanceValues = createInformRDF(instanceName);
                createdInstancesModel.add(createdInstanceValues);       
            }            
        }           
        
        if(createdInstancesModel.isEmpty()){
            return IMessageBus.STATUS_400;
        }
        
        adapter.notifyListeners(createdInstancesModel, requestID);

        return IMessageBus.STATUS_200;
    }

    public String parseReleaseModel(Model modelRelease, String requestID) {
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelRelease);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to release...");

        Statement currentMotorStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentMotorStatement = iteratorMotorResource.nextStatement();

            String instanceName = currentMotorStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Releasing instance: " + instanceName + " (" + currentMotorStatement.toString() + ")");

            if (adapter.terminateInstance(instanceName)) {
                adapter.notifyListeners(createInformReleaseRDF(instanceName), requestID);
                return IMessageBus.STATUS_200;
            }
        }
        return IMessageBus.STATUS_404;
    }

    public String parseDiscoverModel(Model modelDiscover) {
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelDiscover);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to discover...");

        Statement currentInstanceStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentInstanceStatement = iteratorMotorResource.nextStatement();

            String instanceName = currentInstanceStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Discovering instance: " + instanceName + " (" + currentInstanceStatement.toString() + ")");

            String response = adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT);
            if(response.isEmpty()){
                return IMessageBus.STATUS_404;
            } else {
                return response;
            }            
        }
        // No specific instance requested, show all
        return this.adapter.getDiscoverAll(IMessageBus.SERIALIZATION_DEFAULT);
    }

    public String parseConfigureModel(Model modelConfigure, String requestID) {

        Model changedInstancesModel = ModelFactory.createDefaultModel();
        adapter.setModelPrefixes(changedInstancesModel);
        
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelConfigure);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to configure...");

        Statement currentConfigureStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentConfigureStatement = iteratorMotorResource.nextStatement();
            
            String instanceName = currentConfigureStatement.getSubject().getLocalName();
            
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Configuring instance: " + currentConfigureStatement.getSubject().getLocalName() + " (" + currentConfigureStatement.toString() + ")");
            
            List<String> updatedProperties = adapter.configureInstance(currentConfigureStatement);
            Model changedInstanceValues = adapter.createInformConfigureRDF(instanceName,updatedProperties);            
            changedInstancesModel.add(changedInstanceValues);
        }        
        
        if(changedInstancesModel.isEmpty()){
            return IMessageBus.STATUS_404;
        }
        
        adapter.notifyListeners(changedInstancesModel, requestID);
        
        return IMessageBus.STATUS_200;
    }
    
    public Model createInformRDF(String instanceName) {
        return adapter.getSingleInstanceModel(instanceName);
    }
    
    public Model createInformReleaseRDF(String instanceName) {
        
        Model modelInstances = ModelFactory.createDefaultModel();

        adapter.setModelPrefixes(modelInstances);
        
        Resource releaseInstance = modelInstances.createResource("http://fiteagleinternal#" + instanceName);
        modelInstances.add(adapter.getAdapterInstance(), MessageBusOntologyModel.methodReleases, releaseInstance);
        
        return modelInstances;
    }

}
