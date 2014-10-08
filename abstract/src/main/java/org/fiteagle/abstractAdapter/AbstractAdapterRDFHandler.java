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

    protected abstract AbstractAdapter getAdapter();
    
    private StmtIterator getResourceInstanceIterator(Model model) {
        return model.listStatements(new SimpleSelector(null, RDF.type, getAdapter().getAdapterManagedResource()));
    }

    public String parseCreateModel(Model modelCreate, String requestID) {

        Model createdInstancesModel = ModelFactory.createDefaultModel();
        getAdapter().setModelPrefixes(createdInstancesModel);

        StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelCreate);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for resources to create...");

        Statement currentResourceInstanceStatement = null;
        while (iteratorResourceInstance.hasNext()) {
            currentResourceInstanceStatement = iteratorResourceInstance.nextStatement();

            String instanceName = currentResourceInstanceStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Creating instance: " + instanceName + " (" + currentResourceInstanceStatement.toString() + ")");

            if (getAdapter().createInstance(instanceName)) {
                // Configure additional parameters directly after creation
                getAdapter().configureInstance(currentResourceInstanceStatement);
                Model createdInstanceValues = createInformRDF(instanceName);
                createdInstancesModel.add(createdInstanceValues);
            }
        }

        if (createdInstancesModel.isEmpty()) {
            return IMessageBus.STATUS_400;
        }

        getAdapter().notifyListeners(createdInstancesModel, requestID);

        return IMessageBus.STATUS_200;
    }

    public String parseReleaseModel(Model modelRelease, String requestID) {
        StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelRelease);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for resources to release...");

        Statement currentResourceInstanceStatement = null;
        while (iteratorResourceInstance.hasNext()) {
            currentResourceInstanceStatement = iteratorResourceInstance.nextStatement();

            String instanceName = currentResourceInstanceStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Releasing instance: " + instanceName + " (" + currentResourceInstanceStatement.toString() + ")");

            if (getAdapter().terminateInstance(instanceName)) {
                getAdapter().notifyListeners(createInformReleaseRDF(instanceName), requestID);
                return IMessageBus.STATUS_200;
            }
        }
        return IMessageBus.STATUS_404;
    }

    public String parseDiscoverModel(Model modelDiscover) {
        StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelDiscover);

        Statement currentInstanceStatement = null;
        while (iteratorResourceInstance.hasNext()) {
            currentInstanceStatement = iteratorResourceInstance.nextStatement();

            String instanceName = currentInstanceStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Discovering instance: " + instanceName + " (" + currentInstanceStatement.toString() + ")");

            String response = getAdapter().monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT);
            if (response.isEmpty()) {
                return IMessageBus.STATUS_404;
            } else {
                return response;
            }
        }
        // No specific instance requested, show all
        return getAdapter().getDiscoverAll(IMessageBus.SERIALIZATION_DEFAULT);
    }

    public String parseConfigureModel(Model modelConfigure, String requestID) {

        Model changedInstancesModel = ModelFactory.createDefaultModel();
        getAdapter().setModelPrefixes(changedInstancesModel);

        StmtIterator iteratorResourceInstance = getResourceInstanceIterator(modelConfigure);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for resources to configure...");

        Statement currentConfigureStatement = null;
        while (iteratorResourceInstance.hasNext()) {
            currentConfigureStatement = iteratorResourceInstance.nextStatement();

            String instanceName = currentConfigureStatement.getSubject().getLocalName();

            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Configuring instance: " + currentConfigureStatement.getSubject().getLocalName() + " (" + currentConfigureStatement.toString() + ")");

            List<String> updatedProperties = getAdapter().configureInstance(currentConfigureStatement);
            Model changedInstanceValues = getAdapter().createInformConfigureRDF(instanceName, updatedProperties);
            changedInstancesModel.add(changedInstanceValues);
        }

        if (changedInstancesModel.isEmpty()) {
            return IMessageBus.STATUS_404;
        }

        getAdapter().notifyListeners(changedInstancesModel, requestID);

        return IMessageBus.STATUS_200;
    }

    public Model createInformRDF(String instanceName) {
        return getAdapter().getSingleInstanceModel(instanceName);
    }

    public Model createInformReleaseRDF(String instanceName) {

        Model modelInstances = ModelFactory.createDefaultModel();

        getAdapter().setModelPrefixes(modelInstances);

        Resource releaseInstance = modelInstances.createResource("http://fiteagleinternal#" + instanceName);
        modelInstances.add(getAdapter().getAdapterInstance(), MessageBusOntologyModel.methodReleases, releaseInstance);

        return modelInstances;
    }

}
