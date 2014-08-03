package org.fiteagle.adapters.motor;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;

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

    private String adapterSpecificPrefix = "http://fiteagle.org/ontology/adapter/motor#";
    private static MotorAdapter motorAdapterSingleton;

    public static synchronized MotorAdapter getInstance() {
        if (motorAdapterSingleton == null)
            motorAdapterSingleton = new MotorAdapter();
        return motorAdapterSingleton;
    }

    private Resource motorResource;
    private Resource MotorAdapter;
    private Property motorPropertyRPM;
    private Property motorPropertyMaxRPM;
    private Property motorPropertyThrottle;
    private Property motorPropertyManufacturer;
    private Property motorPropertyIsDynamic;

    private List<Property> motorControlProperties = new LinkedList<Property>();

    private MotorAdapter() {
        modelGeneral = ModelFactory.createDefaultModel();

        modelGeneral.setNsPrefix("", "http://fiteagle.org/ontology/adapter/motor#");
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // Property instantiatesProperty = new PropertyImpl("fiteagle:instantiates");

        motorResource = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#MotorResource");
        motorResource.addProperty(RDF.type, OWL.Class);
        motorResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

        MotorAdapter = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#MotorAdapter");
        MotorAdapter.addProperty(RDF.type, OWL.Class);
        MotorAdapter.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        MotorAdapter.addProperty(modelGeneral.createProperty("http://fiteagle.org/ontology#instantiates"), motorResource);

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

        Resource individualMotorAdapter1 = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#individualMotorAdapter1");
        individualMotorAdapter1.addProperty(RDF.type, MotorAdapter);
        individualMotorAdapter1.addProperty(RDFS.label, modelGeneral.createLiteral("Motor Adapter 1", "en"));
        individualMotorAdapter1.addProperty(RDFS.comment, modelGeneral.createLiteral("A Motor Adapter 1", "en"));

    }

    @Override
    public Object handleCreateInstance(int instanceID) {
        return new DynamicMotor(this, instanceID);
    }

    @Override
    public Model handleMonitorInstance(int instanceID, Model modelInstances) {
        Motor currentMotor = (Motor) instanceList.get(instanceID);

        Resource motorInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/motor#m" + instanceID);
        addPropertiesToResource(motorInstance, currentMotor, instanceID);

        return modelInstances;
    }

    @Override
    public Model handleGetAllInstances(Model modelInstances) {
        for (Integer key : instanceList.keySet()) {

            Motor currentMotor = (Motor) instanceList.get(key);

            Resource motorInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/motor#m" + key);
            addPropertiesToResource(motorInstance, currentMotor, key);
        }
        return modelInstances;
    }

    public void addPropertiesToResource(Resource motorInstance, Motor currentMotor, int key) {
        motorInstance.addProperty(RDF.type, motorResource);
        motorInstance.addProperty(RDFS.label, "" + key);
        motorInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Motor in the garage " + key, "en"));
        motorInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
        motorInstance.addLiteral(motorPropertyMaxRPM, currentMotor.getMaxRpm());
        motorInstance.addLiteral(motorPropertyThrottle, currentMotor.getThrottle());
        motorInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");
        motorInstance.addLiteral(motorPropertyIsDynamic, ((DynamicMotor) currentMotor).isDynamic());
    }

    @Override
    public String handleControlInstance(Model model2) {
        StringWriter sw = new StringWriter();

        StmtIterator iter = model2.listStatements(new SimpleSelector(null, RDF.type, motorResource));
        while (iter.hasNext()) {
            Resource currentResource = iter.nextStatement().getSubject();
            // sw.write(currentResource.getProperty(RDFS.label).getObject().toString());
            int key = Integer.parseInt(currentResource.getProperty(RDFS.label).getObject().toString());
            if (instanceList.containsKey(key)) {
                Motor currentMotor = (Motor) instanceList.get(key);

                for (Property currentProperty : motorControlProperties) {
                    StmtIterator iter2 = currentResource.listProperties(currentProperty);

                    while (iter2.hasNext()) {
                        // int value = (int) iter2.nextStatement().getObject().asLiteral().getString();
                        String newValue = "";

                        if (currentProperty == motorPropertyRPM) {
                            currentMotor.setRpm((int) iter2.nextStatement().getObject().asLiteral().getLong());
                            newValue = "" + currentMotor.getRpm();
                        } else if (currentProperty == motorPropertyMaxRPM) {
                            currentMotor.setMaxRpm((int) iter2.nextStatement().getObject().asLiteral().getLong());
                            newValue = "" + currentMotor.getMaxRpm();
                        } else if (currentProperty == motorPropertyThrottle) {
                            currentMotor.setThrottle((int) iter2.nextStatement().getObject().asLiteral().getLong());
                            newValue = "" + currentMotor.getThrottle();
                        } else if (currentProperty == motorPropertyIsDynamic) {
                            ((DynamicMotor) currentMotor).setIsDynamic(iter2.nextStatement().getObject().asLiteral().getBoolean());
                            newValue = "" + ((DynamicMotor) currentMotor).isDynamic();
                        }

                        sw.write("Changed motor instance " + key + " property " + currentProperty.toString() + " to value " + newValue + "\n");
                    }
                }
            }
        }

        return sw.toString();
    }

    @Override
    public String getInstanceClassName() {
        return Motor.class.getName();
    }

    @Override
    public String getAdapterSpecificPrefix() {
        return adapterSpecificPrefix;
    }

    public Motor getInstance(int instanceID) {
        return (Motor) instanceList.get(instanceID);
    }

}
