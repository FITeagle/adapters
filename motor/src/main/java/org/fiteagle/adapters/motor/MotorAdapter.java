package org.fiteagle.adapters.motor;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.*;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;


import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.fiteagle.api.core.MessageBusOntologyModel;

public final class MotorAdapter extends AbstractAdapter {


    private static final List<Property> motorControlProperties = new ArrayList<Property>();

    private final HashMap<String, Motor> instanceList = new HashMap<String, Motor>();


    public MotorAdapter(Model adapterModel, Resource adapterABox) {
        this.uuid = UUID.randomUUID().toString();
        this.adapterTBox = adapterModel;
        this.adapterABox = adapterABox;
        Resource adapterType =getAdapterClass();
        this.adapterABox.addProperty(RDF.type,adapterType);
        this.adapterABox.addProperty(RDFS.label,  this.adapterABox.getLocalName());
        this.adapterABox.addProperty(RDFS.comment, "A motor garage adapter that can simulate different dynamic motor resources.");
        this.adapterABox.addLiteral(MessageBusOntologyModel.maxInstances, 10);

        Property longitude = adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
        Property latitude = adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
        this.adapterABox.addProperty(latitude, "52.516377");
        this.adapterABox.addProperty(longitude, "13.323732");

        NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
        if (resourceIterator.hasNext()) {
            Resource resource = resourceIterator.next().asResource();

            this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
            ResIterator propertiesIterator = adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
            while (propertiesIterator.hasNext()) {
                Property p = adapterTBox.getProperty(propertiesIterator.next().getURI());
                motorControlProperties.add(p);
            }
        }

    }

    private Resource getAdapterClass() {
        ResIterator resIterator = adapterTBox.listResourcesWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
        Resource resourceAdapterClass = null;
        while (resIterator.hasNext()){
            resourceAdapterClass = resIterator.nextResource();
            break;
        }

        return resourceAdapterClass;
    }



    @Override
    public Model createInstance(String instanceURI, Model modelCreate) {
        Motor motor = new Motor(this, instanceURI);
        instanceList.put(instanceURI, motor);
        updateInstance(instanceURI, modelCreate);
        return parseToModel(motor);
    }

    Model parseToModel(Motor motor) {
        Resource resource = ModelFactory.createDefaultModel().createResource(motor.getInstanceName());
        resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
        resource.addProperty(RDF.type, Omn.Resource);
        resource.addProperty(RDFS.label, resource.getLocalName());
        Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),Omn_lifecycle.hasState.getLocalName());
        property.addProperty(RDF.type, OWL.FunctionalProperty);
        resource.addProperty(property, Omn_lifecycle.Ready);
        for (Property p : motorControlProperties) {
            switch (p.getLocalName()) {
                case "rpm":
                    resource.addLiteral(p, motor.getRpm());
                    break;
                case "maxRpm":
                    resource.addLiteral(p, motor.getMaxRpm());
                    break;
                case "manufacturer":
                    resource.addLiteral(p, motor.getManufacturer());
                    break;
                case "throttle":
                    resource.addLiteral(p, motor.getThrottle());
                    break;
                case "isDynamic":
                    resource.addLiteral(p, motor.isDynamic());
                    break;
            }
        }
        return resource.getModel();
    }

    @Override
    public Model updateInstance(String instanceURI, Model configureModel) {
        if (instanceList.containsKey(instanceURI)) {
            Motor currentMotor =  instanceList.get(instanceURI);
            StmtIterator iter = configureModel.listStatements();
            while(iter.hasNext()){
                currentMotor.updateProperty(iter.next());
            }
            return parseToModel(currentMotor);
        }
        return ModelFactory.createDefaultModel();
    }

    @Override
    public void deleteInstance(String instanceURI) {
        Motor motor = getInstanceByName(instanceURI);
        motor.terminate();
        instanceList.remove(instanceURI);
    }

    private Motor getInstanceByName(String instanceURI) {
        return instanceList.get(instanceURI);
    }



    @Override
    public Resource getAdapterABox() {
        return adapterABox;
    }

    @Override
    public Model getAdapterDescriptionModel() {
        return adapterTBox;
    }

    @Override
    public void updateAdapterDescription() {
    }

    @Override
    public Model getInstance(String instanceURI) throws InstanceNotFoundException {
        Motor motor = instanceList.get(instanceURI);
        if(motor == null){
            throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
        }
        return parseToModel(motor);
    }

    @Override
    public Model getAllInstances() throws InstanceNotFoundException {
        Model model = ModelFactory.createDefaultModel();
        for(String uri : instanceList.keySet()){
            model.add(getInstance(uri));
        }
        return model;
    }

    @Override
    public void refreshConfig() throws ProcessingException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getId() {
        return this.uuid;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void configure(Config configuration) {

    }

}
