package org.fiteagle.adapters.motor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

public final class MotorAdapter extends AbstractAdapter {

    private static final List<Property> MOTOR_CTRL_PROPS = new ArrayList<Property>();

    private transient final HashMap<String, Motor> instanceList = new HashMap<String, Motor>();
    private static final Logger LOGGER = Logger.getLogger(MotorAdapter.class.toString());

    public MotorAdapter(final Model adapterModel, final Resource adapterABox) {
	super();
	this.uuid = UUID.randomUUID().toString();
	this.adapterTBox = adapterModel;
	this.adapterABox = adapterABox;
	final Resource adapterType = this.getAdapterClass();
	this.adapterABox.addProperty(RDF.type, adapterType);
	this.adapterABox.addProperty(RDFS.label, this.adapterABox.getLocalName());
	this.adapterABox.addProperty(RDFS.comment,
		"A motor garage adapter that can simulate different dynamic motor resources.");
	this.adapterABox.addLiteral(MessageBusOntologyModel.maxInstances, 10);

	final Property longitude = this.adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
	final Property latitude = this.adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
	this.adapterABox.addProperty(latitude, "52.516377");
	this.adapterABox.addProperty(longitude, "13.323732");

	final NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
	if (resourceIterator.hasNext()) {
	    final Resource resource = resourceIterator.next().asResource();

	    this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
	    this.adapterABox.getModel().add(resource.getModel());
	    final ResIterator propIterator = this.adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
	    while (propIterator.hasNext()) {
		final Property property = this.adapterTBox.getProperty(propIterator.next().getURI());
		MotorAdapter.MOTOR_CTRL_PROPS.add(property);
	    }
	}

    }

    @Override
    public Model createInstance(final String instanceURI, final Model modelCreate) {
	final Motor motor = new Motor(this, instanceURI);
	this.instanceList.put(instanceURI, motor);
	this.updateInstance(instanceURI, modelCreate);
	return this.parseToModel(motor);
    }

    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    Model parseToModel(final Motor motor) {
	final Resource resource = ModelFactory.createDefaultModel().createResource(motor.getInstanceName());
	resource.addProperty(RDF.type, this.getAdapterManagedResources().get(0));
	resource.addProperty(RDF.type, Omn.Resource);
	resource.addProperty(RDFS.label, resource.getLocalName());
	final Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),
		Omn_lifecycle.hasState.getLocalName());
	property.addProperty(RDF.type, OWL.FunctionalProperty);
	resource.addProperty(property, Omn_lifecycle.Ready);
	for (final Property prop : MotorAdapter.MOTOR_CTRL_PROPS) {
	    switch (prop.getLocalName()) {
	    case "rpm":
		resource.addLiteral(prop, motor.getRpm());
		break;
	    case "maxRpm":
		resource.addLiteral(prop, motor.getMaxRpm());
		break;
	    case "manufacturer":
		resource.addLiteral(prop, motor.getManufacturer());
		break;
	    case "throttle":
		resource.addLiteral(prop, motor.getThrottle());
		break;
	    case "isDynamic":
		resource.addLiteral(prop, motor.isDynamic());
		break;
	    default:
		LOGGER.warning("Unkown: " + prop.getLocalName());
		break;
	    }
	}
	return resource.getModel();
    }

    @Override
    public Model updateInstance(final String instanceURI, final Model configureModel) {
	if (this.instanceList.containsKey(instanceURI)) {
	    final Motor currentMotor = this.instanceList.get(instanceURI);
	    final StmtIterator iter = configureModel.listStatements();
	    while (iter.hasNext()) {
		currentMotor.updateProperty(iter.next());
	    }
	    return this.parseToModel(currentMotor);
	}
	return ModelFactory.createDefaultModel();
    }

    @Override
    public void deleteInstance(final String instanceURI) {
	final Motor motor = this.getInstanceByName(instanceURI);
	motor.terminate();
	this.instanceList.remove(instanceURI);
    }

    private Motor getInstanceByName(final String instanceURI) {
	return this.instanceList.get(instanceURI);
    }

    @Override
    public Resource getAdapterABox() {
	return this.adapterABox;
    }

    @Override
    public Model getAdapterDescriptionModel() {
	return this.adapterTBox;
    }

    @Override
    public void updateAdapterDescription() {
	LOGGER.warning("Not implemented.");
    }

    @Override
    public Model getInstance(final String instanceURI) throws InstanceNotFoundException {
	final Motor motor = this.instanceList.get(instanceURI);
	if (motor == null) {
	    throw new InstanceNotFoundException("Instance " + instanceURI + " not found");
	}
	return this.parseToModel(motor);
    }

    @Override
    public Model getAllInstances() throws InstanceNotFoundException {
	final Model model = ModelFactory.createDefaultModel();
	for (final String uri : this.instanceList.keySet()) {
	    model.add(this.getInstance(uri));
	}
	return model;
    }

    @Override
    public void refreshConfig() throws ProcessingException {
	LOGGER.warning("Not implemented.");
    }

    @Override
    public String getId() {
	return this.uuid;
    }

    @Override
    public void shutdown() {
	LOGGER.warning("Not implemented.");
    }

    @Override
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public void configure(final Config configuration) {
	LOGGER.warning("Not implemented. Input: " + configuration);
    }

}
