package org.fiteagle.adapters.motor;

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

public final class MotorAdapter extends AbstractAdapter {

    private String[] adapterSpecificPrefix = { "motor", "http://fiteagle.org/ontology/adapter/motor#" };
    private static MotorAdapter motorAdapterSingleton;

    public static synchronized MotorAdapter getInstance() {
        if (motorAdapterSingleton == null)
            motorAdapterSingleton = new MotorAdapter();
        return motorAdapterSingleton;
    }

    private Resource motorResource;
    private Property motorPropertyRPM;
    private Property motorPropertyMaxRPM;
    private Property motorPropertyThrottle;
    private Property motorPropertyManufacturer;
    private Property motorPropertyIsDynamic;

    private List<Property> motorControlProperties = new LinkedList<Property>();

    private MotorAdapter() {
        
        adapterName = "ADeployedMotorAdapter1";
        
        modelGeneral = ModelFactory.createDefaultModel();

        modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
        modelGeneral.setNsPrefix("motor", "http://fiteagle.org/ontology/adapter/motor#");
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        motorResource = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#Motor");
        motorResource.addProperty(RDF.type, OWL.Class);
        motorResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));




        adapterType = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#MotorGarageAdapter");
        adapterType.addProperty(RDF.type, OWL.Class);
        adapterType.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));

        adapterType.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, motorResource);
        adapterType.addProperty(RDFS.label, modelGeneral.createLiteral("MotorGarageAdapterType ", "en"));

        motorResource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapterType);
        motorResource.addProperty(RDFS.label, modelGeneral.createLiteral("MotorResource", "en"));

        // create the property
        motorPropertyRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#rpm");
        motorPropertyRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyRPM.addProperty(RDFS.domain, motorResource);
        motorPropertyRPM.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyRPM);

        motorPropertyMaxRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#maxRpm");
        motorPropertyMaxRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyMaxRPM.addProperty(RDFS.domain, motorResource);
        motorPropertyMaxRPM.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyMaxRPM);

        motorPropertyThrottle = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#throttle");
        motorPropertyThrottle.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyThrottle.addProperty(RDFS.domain, motorResource);
        motorPropertyThrottle.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyThrottle);



        motorPropertyIsDynamic = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#isDynamic");
        motorPropertyIsDynamic.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyIsDynamic.addProperty(RDFS.domain, motorResource);
        motorPropertyIsDynamic.addProperty(RDFS.range, XSD.xboolean);
        motorControlProperties.add(motorPropertyIsDynamic);

        motorPropertyManufacturer = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#manufacturer");
        motorPropertyManufacturer.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyManufacturer.addProperty(RDFS.domain, motorResource);
        motorPropertyManufacturer.addProperty(RDFS.range, XSD.xstring);

        adapterInstance = modelGeneral.createResource("http://fiteagleinternal#" + adapterName);
        adapterInstance.addProperty(RDF.type, adapterType);
        adapterInstance.addProperty(RDFS.label, modelGeneral.createLiteral("A deployed motor garage adapter named: " + adapterName, "en"));
        adapterInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("A motor garage adapter that can simulate different dynamic motor resources.", "en"));

        adapterInstance.addProperty(modelGeneral.createProperty("http://fiteagleinternal#isAdapterIn"), modelGeneral.createResource("http://fiteagleinternal#FITEAGLE_Testbed"));

    }

    @Override
    public Object handleCreateInstance(String instanceName) {
        return new DynamicMotor(this, instanceName);
    }

    @Override
    public Model handleMonitorInstance(String instanceName, Model modelInstances) {
        Motor currentMotor = (Motor) instanceList.get(instanceName);

        Resource motorInstance = modelInstances.createResource("http://fiteagleinternal#" + instanceName);
        addPropertiesToResource(motorInstance, currentMotor, instanceName);

        return modelInstances;
    }

    @Override
    public Model handleGetAllInstances(Model modelInstances) {
        for (String key : instanceList.keySet()) {

            Motor currentMotor = (Motor) instanceList.get(key);

            Resource motorInstance = modelInstances.createResource("http://fiteagleinternal#" + key);
            addPropertiesToResource(motorInstance, currentMotor, key);
        }
        return modelInstances;
    }

    public void addPropertiesToResource(Resource motorInstance, Motor currentMotor, String instanceName) {
        motorInstance.addProperty(RDF.type, motorResource);
        motorInstance.addProperty(RDFS.label, "Motor: " + instanceName);
        motorInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Motor in the garage " + instanceName, "en"));
        motorInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
        motorInstance.addLiteral(motorPropertyMaxRPM, currentMotor.getMaxRpm());
        motorInstance.addLiteral(motorPropertyThrottle, currentMotor.getThrottle());
        motorInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");
        motorInstance.addLiteral(motorPropertyIsDynamic, ((DynamicMotor) currentMotor).isDynamic());
    }

    @Override
    public List<String> handleConfigureInstance(Statement configureStatement) {

        Resource currentResource = configureStatement.getSubject();
        String instanceName = currentResource.getLocalName();

        List<String> updatedProperties = new LinkedList<String>();

        if (instanceList.containsKey(instanceName)) {
            Motor currentMotor = (Motor) instanceList.get(instanceName);

            for (Property currentProperty : motorControlProperties) {
                StmtIterator iter2 = currentResource.listProperties(currentProperty);

                while (iter2.hasNext()) {

                    if (currentProperty == motorPropertyRPM) {
                        currentMotor.setRpm((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == motorPropertyMaxRPM) {
                        currentMotor.setMaxRpm((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == motorPropertyThrottle) {
                        currentMotor.setThrottle((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == motorPropertyIsDynamic) {
                        ((DynamicMotor) currentMotor).setIsDynamic(iter2.nextStatement().getObject().asLiteral().getBoolean(), updatedProperties);
                    }
                }
            }

        }

        return updatedProperties;
    }

    @Override
    public String[] getAdapterSpecificPrefix() {
        return adapterSpecificPrefix;
    }

    public Motor getInstance(String instanceName) {
        return (Motor) instanceList.get(instanceName);
    }

    @Override
    public Resource getAdapterManagedResource() {
        return motorResource;
    }

}
