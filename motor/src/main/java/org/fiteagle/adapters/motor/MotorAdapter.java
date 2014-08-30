package org.fiteagle.adapters.motor;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public final class MotorAdapter extends AbstractAdapter {

    private String[] adapterSpecificPrefix = {"motor","http://fiteagle.org/ontology/adapter/motor#"};
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

        Resource motorAdapter = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#MotorGarage");
        motorAdapter.addProperty(RDF.type, OWL.Class);
        motorAdapter.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        
        motorAdapter.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, motorResource);
        motorResource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, motorAdapter);

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

        adapterInstance = modelGeneral.createResource("http://fiteagleinternal#ADeployedMotorAdapter1");
        adapterInstance.addProperty(RDF.type, motorAdapter);
        adapterInstance.addProperty(RDFS.label, modelGeneral.createLiteral("A motor garage 1", "en"));
        adapterInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("A motor garage that can simulate different dynamic motor resources.", "en"));

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
    public String handleConfigureInstance(Model configureModel, String requestID) {
        StringWriter sw = new StringWriter();

        Model changedInstancesModel = ModelFactory.createDefaultModel();
        this.setModelPrefixes(changedInstancesModel);
        
        StmtIterator iter = configureModel.listStatements(new SimpleSelector(null, RDF.type, motorResource));
        while (iter.hasNext()) {
            Resource currentResource = iter.nextStatement().getSubject();
            // sw.write(currentResource.getProperty(RDFS.label).getObject().toString());
            String instanceName = currentResource.getLocalName();
            if (instanceList.containsKey(instanceName)) {
                Motor currentMotor = (Motor) instanceList.get(instanceName);
                
                List<String> updatedProperties = new LinkedList<String>();

                for (Property currentProperty : motorControlProperties) {
                    StmtIterator iter2 = currentResource.listProperties(currentProperty);

                    while (iter2.hasNext()) {
                        // int value = (int) iter2.nextStatement().getObject().asLiteral().getString();
                        String newValue = "";

                        if (currentProperty == motorPropertyRPM) {
                            currentMotor.setRpm((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                            newValue = "" + currentMotor.getRpm();
                        } else if (currentProperty == motorPropertyMaxRPM) {
                            currentMotor.setMaxRpm((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                            newValue = "" + currentMotor.getMaxRpm();
                        } else if (currentProperty == motorPropertyThrottle) {
                            currentMotor.setThrottle((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                            newValue = "" + currentMotor.getThrottle();
                        } else if (currentProperty == motorPropertyIsDynamic) {
                            ((DynamicMotor) currentMotor).setIsDynamic(iter2.nextStatement().getObject().asLiteral().getBoolean(), updatedProperties);
                            newValue = "" + ((DynamicMotor) currentMotor).isDynamic();
                        }

                        sw.write("Changed motor instance " + instanceName + " property " + currentProperty.toString() + " to value: " + newValue + "\n\n");
                    }
                }
                
                changedInstancesModel.add(this.createInformConfigureRDF(instanceName,updatedProperties));
            }
        }
        
        this.notifyListeners(changedInstancesModel, requestID);

        return sw.toString();
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
