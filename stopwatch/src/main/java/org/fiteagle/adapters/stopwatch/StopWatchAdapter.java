package org.fiteagle.adapters.stopwatch;

import java.util.LinkedList;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public final class StopWatchAdapter extends AbstractAdapter {

    private String[] adapterSpecificPrefix = { "stopwatch", "http://fiteagle.org/ontology/adapter/stopwatch#" };
    private static StopWatchAdapter stopwatchAdapterSingleton;

    public static synchronized StopWatchAdapter getInstance() {
        if (stopwatchAdapterSingleton == null)
            stopwatchAdapterSingleton = new StopWatchAdapter();
        return stopwatchAdapterSingleton;
    }

    private Property stopwatchPropertyRefreshInterval;
    private Property stopwatchPropertyIsRunning;
    private Property stopwatchPropertyCurrentTime;

    private List<Property> stopwatchontrolProperties = new LinkedList<Property>();

    private StopWatchAdapter() {
        
        adapterName = "ADeployedStopwatchAdapter1";
        
        modelGeneral = ModelFactory.createDefaultModel();

        modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
        modelGeneral.setNsPrefix(adapterSpecificPrefix[0], adapterSpecificPrefix[1]);
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        resourceType = modelGeneral.createResource(adapterSpecificPrefix[1] + "Stopwatch");
        resourceType.addProperty(RDF.type, OWL.Class);
        resourceType.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

        
        adapterType = modelGeneral.createResource(adapterSpecificPrefix[1] + "StopwatchAdapter");
        adapterType.addProperty(RDF.type, OWL.Class);
        adapterType.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));

        adapterType.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, resourceType);
        adapterType.addProperty(RDFS.label, modelGeneral.createLiteral("StopwatchAdapterType ", "en"));

        resourceType.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapterType);
        resourceType.addProperty(RDFS.label, modelGeneral.createLiteral("Stopwatch Resource", "en"));

        // create the property
        stopwatchPropertyRefreshInterval = modelGeneral.createProperty(adapterSpecificPrefix[1] + "refreshInterval");
        stopwatchPropertyRefreshInterval.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyRefreshInterval.addProperty(RDFS.domain, resourceType);
        stopwatchPropertyRefreshInterval.addProperty(RDFS.range, XSD.integer);
        stopwatchontrolProperties.add(stopwatchPropertyRefreshInterval);

        stopwatchPropertyCurrentTime = modelGeneral.createProperty(adapterSpecificPrefix[1] + "currentTime");
        stopwatchPropertyCurrentTime.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyCurrentTime.addProperty(RDFS.domain, resourceType);
        stopwatchPropertyCurrentTime.addProperty(RDFS.range, XSD.integer);
        stopwatchontrolProperties.add(stopwatchPropertyCurrentTime);

        stopwatchPropertyIsRunning = modelGeneral.createProperty(adapterSpecificPrefix[1] + "isRunning");
        stopwatchPropertyIsRunning.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyIsRunning.addProperty(RDFS.domain, resourceType);
        stopwatchPropertyIsRunning.addProperty(RDFS.range, XSD.xboolean);
        stopwatchontrolProperties.add(stopwatchPropertyIsRunning);

        adapterInstance = modelGeneral.createResource("http://fiteagleinternal#" + adapterName);
        adapterInstance.addProperty(RDF.type, adapterType);
        adapterInstance.addProperty(RDFS.label, modelGeneral.createLiteral("A deployed stopwatch adapter named: " + adapterName, "en"));
        adapterInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("A stopwatch adapter that can simulate different dynamic stopwatch resources.", "en"));
    }

    @Override
    public Object handleCreateInstance(String instanceName) {
        return new Stopwatch(this, instanceName);
    }

    @Override
    public Model handleMonitorInstance(String instanceName, Model modelInstances) {
        Stopwatch currentResource = (Stopwatch) instanceList.get(instanceName);

        Resource resourceInstance = modelInstances.createResource("http://fiteagleinternal#" + instanceName);
        addPropertiesToResource(resourceInstance, currentResource, instanceName);

        return modelInstances;
    }

    @Override
    public Model handleGetAllInstances(Model modelInstances) {
        for (String key : instanceList.keySet()) {

            Stopwatch currentResource = (Stopwatch) instanceList.get(key);

            Resource resourceInstance = modelInstances.createResource("http://fiteagleinternal#" + key);
            addPropertiesToResource(resourceInstance, currentResource, key);
        }
        return modelInstances;
    }

    public void addPropertiesToResource(Resource resourceInstance, Stopwatch currentStopwatch, String instanceName) {
        resourceInstance.addProperty(RDF.type, resourceType);
        resourceInstance.addProperty(RDFS.label, "Stopwatch: " + instanceName);
        resourceInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("A dynamic stopwatch resource " + instanceName, "en"));
        resourceInstance.addLiteral(stopwatchPropertyCurrentTime, currentStopwatch.getCurrentTime());
        resourceInstance.addLiteral(stopwatchPropertyRefreshInterval, currentStopwatch.getRefreshInterval());
        resourceInstance.addLiteral(stopwatchPropertyIsRunning, currentStopwatch.isRunning());
    }

    @Override
    public List<String> configureInstance(Statement configureStatement) {

        Resource currentResource = configureStatement.getSubject();
        String instanceName = currentResource.getLocalName();

        List<String> updatedProperties = new LinkedList<String>();

        if (instanceList.containsKey(instanceName)) {
            Stopwatch currentResourceInstance = (Stopwatch) instanceList.get(instanceName);

            for (Property currentProperty : stopwatchontrolProperties) {
                StmtIterator iter2 = currentResource.listProperties(currentProperty);

                while (iter2.hasNext()) {

                    if (currentProperty == stopwatchPropertyCurrentTime) {
                        currentResourceInstance.setCurrentTime(iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == stopwatchPropertyRefreshInterval) {
                        currentResourceInstance.setRefreshInterval((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == stopwatchPropertyIsRunning) {
                        currentResourceInstance.setIsRunning(iter2.nextStatement().getObject().asLiteral().getBoolean(), updatedProperties);
                    }
                }
            }

        }

        return updatedProperties;
    }

    @Override
    public String[] getAdapterSpecificPrefix() {
        return adapterSpecificPrefix.clone();
    }

    public Stopwatch getInstance(String instanceName) {
        return (Stopwatch) instanceList.get(instanceName);
    }

}
