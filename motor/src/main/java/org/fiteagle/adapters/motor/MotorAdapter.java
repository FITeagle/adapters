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

    private Model adapterModel;
    private Resource adapterInstance;
    private Resource adapter;
    private Resource resource;
    private String adapterName;
    
    private static MotorAdapter motorAdapterSingleton;

    public static synchronized MotorAdapter getInstance() {
        if (motorAdapterSingleton == null)
            motorAdapterSingleton = new MotorAdapter();
        return motorAdapterSingleton;
    }

    private Property motorPropertyRPM;
    private Property motorPropertyMaxRPM;
    private Property motorPropertyThrottle;
    private Property motorPropertyManufacturer;
    private Property motorPropertyIsDynamic;

    private List<Property> motorControlProperties = new LinkedList<Property>();

    private MotorAdapter() {
        
        adapterName = "ADeployedMotorAdapter1";
        
        adapterModel = ModelFactory.createDefaultModel();

        adapterModel.setNsPrefix("", "http://fiteagleinternal#");
        adapterModel.setNsPrefix("motor", "http://fiteagle.org/ontology/adapter/motor#");
        adapterModel.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        adapterModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        adapterModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        adapterModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        adapterModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        resource = adapterModel.createResource("http://fiteagle.org/ontology/adapter/motor#Motor");
        resource.addProperty(RDF.type, OWL.Class);
        resource.addProperty(RDFS.subClassOf, adapterModel.createResource("http://fiteagle.org/ontology#Resource"));


        adapter = adapterModel.createResource("http://fiteagle.org/ontology/adapter/motor#MotorGarageAdapter");
        adapter.addProperty(RDF.type, OWL.Class);
        adapter.addProperty(RDFS.subClassOf, adapterModel.createResource("http://fiteagle.org/ontology#Adapter"));

        adapter.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, resource);
        adapter.addProperty(RDFS.label, adapterModel.createLiteral("MotorGarageAdapterType ", "en"));

        resource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapter);
        resource.addProperty(RDFS.label, adapterModel.createLiteral("MotorResource", "en"));

        // create the property
        motorPropertyRPM = adapterModel.createProperty("http://fiteagle.org/ontology/adapter/motor#rpm");
        motorPropertyRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyRPM.addProperty(RDFS.domain, resource);
        motorPropertyRPM.addProperty(RDFS.range, XSD.integer);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: RPM", "en");
        motorControlProperties.add(motorPropertyRPM);

        motorPropertyMaxRPM = adapterModel.createProperty("http://fiteagle.org/ontology/adapter/motor#maxRpm");
        motorPropertyMaxRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyMaxRPM.addProperty(RDFS.domain, resource);
        motorPropertyMaxRPM.addProperty(RDFS.range, XSD.integer);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: Max. RPM", "en");
        motorControlProperties.add(motorPropertyMaxRPM);

        motorPropertyThrottle = adapterModel.createProperty("http://fiteagle.org/ontology/adapter/motor#throttle");
        motorPropertyThrottle.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyThrottle.addProperty(RDFS.domain, resource);
        motorPropertyThrottle.addProperty(RDFS.range, XSD.integer);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: Throttle", "en");
        motorControlProperties.add(motorPropertyThrottle);



        motorPropertyIsDynamic = adapterModel.createProperty("http://fiteagle.org/ontology/adapter/motor#isDynamic");
        motorPropertyIsDynamic.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyIsDynamic.addProperty(RDFS.domain, resource);
        motorPropertyIsDynamic.addProperty(RDFS.range, XSD.xboolean);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: isDynamic", "en");
        motorControlProperties.add(motorPropertyIsDynamic);

        motorPropertyManufacturer = adapterModel.createProperty("http://fiteagle.org/ontology/adapter/motor#manufacturer");
        motorPropertyManufacturer.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyManufacturer.addProperty(RDFS.domain, resource);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: Manufacturer", "en");
        motorPropertyManufacturer.addProperty(RDFS.range, XSD.xstring);

        adapterInstance = adapterModel.createResource("http://fiteagleinternal#" + adapterName);
        adapterInstance.addProperty(RDF.type, adapter);
        adapterInstance.addProperty(RDFS.label, adapterModel.createLiteral("A deployed motor garage adapter named: " + adapterName, "en"));
        adapterInstance.addProperty(RDFS.comment, adapterModel.createLiteral("A motor garage adapter that can simulate different dynamic motor resources.", "en"));

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
        motorInstance.addProperty(RDF.type, resource);
        motorInstance.addProperty(RDFS.label, "Motor: " + instanceName);
        motorInstance.addProperty(RDFS.comment, adapterModel.createLiteral("Motor in the garage " + instanceName, "en"));
        motorInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
        motorInstance.addLiteral(motorPropertyMaxRPM, currentMotor.getMaxRpm());
        motorInstance.addLiteral(motorPropertyThrottle, currentMotor.getThrottle());
        motorInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");
        motorInstance.addLiteral(motorPropertyIsDynamic, ((DynamicMotor) currentMotor).isDynamic());
    }

    @Override
    public List<String> configureInstance(Statement configureStatement) {

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
        return adapterSpecificPrefix.clone();
    }

    public Motor getInstance(String instanceName) {
        return (Motor) instanceList.get(instanceName);
    }
    
    @Override
    public Resource getAdapterManagedResource() {
      return resource;
    }

    @Override
    public Resource getAdapterInstance() {
      return adapterInstance;
    }

    @Override
    public Resource getAdapterType() {
      return adapter;
    }

    @Override
    public Model getAdapterDescriptionModel() {
      return adapterModel;
    }
    
    @Override
    public String getAdapterName() {
      return adapterName;
    }

    @Override
    protected void updateInstanceList() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void handleTerminateInstance(String instanceName) {
      // TODO Auto-generated method stub
      
    }
}
