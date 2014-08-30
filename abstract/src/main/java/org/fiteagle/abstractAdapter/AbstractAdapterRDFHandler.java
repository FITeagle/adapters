package org.fiteagle.abstractAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Model;
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
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelCreate);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to create...");

        Statement currentMotorStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentMotorStatement = iteratorMotorResource.nextStatement();

            String instanceName = currentMotorStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Creating instance: " + instanceName + " (" + currentMotorStatement.toString() + ")");

            if (adapter.createInstance(instanceName, requestID)) {
                return "";
            }
        }
        return "No instances to create \n\n";
    }

    public String parseReleaseModel(Model modelRelease, String requestID) {
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelRelease);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to release...");

        Statement currentMotorStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentMotorStatement = iteratorMotorResource.nextStatement();

            String instanceName = currentMotorStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Releasing instance: " + instanceName + " (" + currentMotorStatement.toString() + ")");

            if (adapter.terminateInstance(instanceName, requestID)) {
                return "";
            }
        }
        return "No instances to release \n\n";
    }

    public String parseDiscoverModel(Model modelDiscover) {
        StmtIterator iteratorMotorResource = getResourceInstanceIterator(modelDiscover);

        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Searching for motor resources to discover...");

        Statement currentInstanceStatement = null;
        while (iteratorMotorResource.hasNext()) {
            currentInstanceStatement = iteratorMotorResource.nextStatement();

            String instanceName = currentInstanceStatement.getSubject().getLocalName();
            AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Discovering instance: " + instanceName + " (" + currentInstanceStatement.toString() + ")");

            return adapter.monitorInstance(instanceName, IMessageBus.SERIALIZATION_DEFAULT);
        }
        // No specific instance requested, show all
        return this.adapter.getDiscoverAll(IMessageBus.SERIALIZATION_DEFAULT);
    }

    public String parseConfigureModel(Model modelConfigure, String requestID) {
        AbstractAdapterRDFHandler.LOGGER.log(Level.INFO, "Configuring instance");
        adapter.configureInstance(modelConfigure, requestID);

        return "";
    }

}
