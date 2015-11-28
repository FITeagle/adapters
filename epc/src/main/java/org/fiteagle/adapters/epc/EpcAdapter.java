package org.fiteagle.adapters.epc;

import info.openmultinet.ontology.vocabulary.Epc;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.adapters.epc.model.AccessNetwork;
import org.fiteagle.adapters.epc.model.EvolvedPacketCore;
import org.fiteagle.adapters.epc.model.UserEquipment;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class EpcAdapter extends AbstractAdapter {

	private static final List<Property> EPC_CTRL_PROPS = new ArrayList<Property>();

	private transient final HashMap<String, EpcGeneric> instanceList = new HashMap<String, EpcGeneric>();
	private static final Logger LOGGER = Logger.getLogger(EpcAdapter.class
			.toString());

	public EpcAdapter(final Model adapterModel, final Resource adapterABox) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.adapterTBox = adapterModel;
		this.adapterABox = adapterABox;
		final Resource adapterType = this.getAdapterClass();
		this.adapterABox.addProperty(RDF.type, adapterType);
		this.adapterABox.addProperty(RDFS.label,
				this.adapterABox.getLocalName());
		this.adapterABox
				.addProperty(RDFS.comment,
						"A epc garage adapter that can simulate different dynamic epc resources.");
		this.adapterABox.addLiteral(MessageBusOntologyModel.maxInstances, 10);

		final Property longitude = this.adapterTBox
				.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
		final Property latitude = this.adapterTBox
				.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
		this.adapterABox.addProperty(latitude, "52.516377");
		this.adapterABox.addProperty(longitude, "13.323732");

		Model implementables = OntologyModelUtil.loadModel(
				"ontologies/epc-adapter.ttl", IMessageBus.SERIALIZATION_TURTLE);
		final NodeIterator resourceIterator = implementables
				.listObjectsOfProperty(Omn_lifecycle.implements_);

		while (resourceIterator.hasNext()) {
			final Resource resource = resourceIterator.next().asResource();
			this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
			this.adapterABox.getModel().add(resource.getModel());
			final ResIterator propIterator = this.adapterTBox
					.listSubjectsWithProperty(RDFS.domain, resource);
			while (propIterator.hasNext()) {
				final Property property = this.adapterTBox
						.getProperty(propIterator.next().getURI());
				// System.out.println(property);
				EpcAdapter.EPC_CTRL_PROPS.add(property);
			}
		}
		// System.out.println("***********Tbox");
		// System.out.println(Parser.toString(adapterTBox));
	}

	@Override
	public Model createInstance(final String instanceURI,
			final Model modelCreate) {
		LOGGER.warning("Creating instance: " + instanceURI);
		Resource resource = modelCreate.getResource(instanceURI);

		if (resource.hasProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.AccessNetwork)) {
			LOGGER.warning("Try to update instance: " + instanceURI);
			final AccessNetwork accessNetwork = new AccessNetwork(this,
					instanceURI);
			this.instanceList.put(instanceURI, accessNetwork);

			System.out.println("#######create instance modelCreate: "
					+ MessageUtil.serializeModel(modelCreate,
							IMessageBus.SERIALIZATION_TURTLE));
			this.updateInstance(instanceURI, modelCreate);

			return this.parseToModel(accessNetwork);
		} else if (resource.hasProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.EvolvedPacketCore)) {
			LOGGER.warning("Try to update instance: " + instanceURI);
			final EvolvedPacketCore epc = new EvolvedPacketCore(this,
					instanceURI);

			// start the PDN Gateway
			epc.getPdnGateway().startInstance();

			this.instanceList.put(instanceURI, epc);
			System.out.println("#######creteinstance modelCreate: "
					+ MessageUtil.serializeModel(modelCreate,
							IMessageBus.SERIALIZATION_TURTLE));
			this.updateInstance(instanceURI, modelCreate);
			return this.parseToModel(epc);
		} else if (resource.hasProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.UserEquipment)) {
			LOGGER.warning("Try to update instance: " + instanceURI);
			final UserEquipment ue = new UserEquipment(this, instanceURI);
			this.instanceList.put(instanceURI, ue);
			System.out.println("#######creteinstance modelCreate: "
					+ MessageUtil.serializeModel(modelCreate,
							IMessageBus.SERIALIZATION_TURTLE));
			this.updateInstance(instanceURI, modelCreate);
			return this.parseToModel(ue);
		}

		LOGGER.warning("Couldn't recognize type, so returning original model.");
		return modelCreate;
	}

	@SuppressWarnings("PMD.GuardLogStatementJavaUtil")
	Model parseToModel(final EpcGeneric epcGeneric) {

		LOGGER.warning("Calling parse to model...");
		final Resource resource = ModelFactory.createDefaultModel()
				.createResource(epcGeneric.getInstanceName());

		if (epcGeneric.getLabel() == null || epcGeneric.getLabel().equals("")) {
			resource.addProperty(RDFS.label, resource.getLocalName());
		} else {
			resource.addProperty(RDFS.label, epcGeneric.getLabel());
		}
		final Property propertyLabel = resource.getModel().createProperty(
				RDFS.label.getNameSpace(), RDFS.label.getLocalName());
		propertyLabel.addProperty(RDF.type, OWL.FunctionalProperty);

		final Property property = resource.getModel().createProperty(
				Omn_lifecycle.hasState.getNameSpace(),
				Omn_lifecycle.hasState.getLocalName());
		property.addProperty(RDF.type, OWL.FunctionalProperty);
		resource.addProperty(property, Omn_lifecycle.Ready);

		if (epcGeneric instanceof AccessNetwork) {
			AccessNetwork an = (AccessNetwork) epcGeneric;
			an.parseToModel(resource);
		} else if (epcGeneric instanceof EvolvedPacketCore) {
			EvolvedPacketCore epc = (EvolvedPacketCore) epcGeneric;
			epc.parseToModel(resource);
		} else if (epcGeneric instanceof UserEquipment) {
			UserEquipment ue = (UserEquipment) epcGeneric;
			ue.parseToModel(resource);
		}
		LOGGER.log(Level.INFO, "CONTENT parse to model: "
				+ resource.getModel().toString());
		return resource.getModel();
	}

	@Override
	public Model updateInstance(final String instanceURI,
			final Model configureModel) {

		LOGGER.info("updateInstance instanceURI: " + instanceURI);
		LOGGER.info("updateInstance configureModel: "
				+ MessageUtil.serializeModel(configureModel,
						IMessageBus.SERIALIZATION_TURTLE));

		// if the instance is in the list of instances in the adapter
		if (this.instanceList.containsKey(instanceURI)) {

			final EpcGeneric currentEpc = this.instanceList.get(instanceURI);
			Resource epcResource = configureModel.getResource(instanceURI);
			currentEpc.updateInstance(epcResource);

			final Model newModel = this.parseToModel(currentEpc);
			LOGGER.info("Returning updated epc: " + newModel);

			return newModel;
		} else {
			LOGGER.info("Instance list does not contain key.");
		}
		LOGGER.info("Creating new instance");
		return ModelFactory.createDefaultModel();
	}

	@Override
	public void deleteInstance(final String instanceURI) {
		final EpcGeneric epcGeneric = this.getInstanceByName(instanceURI);

		// if the item is an EPC, stop the PDN gateway before deleting the
		// instance
		if (epcGeneric instanceof EvolvedPacketCore) {
			EvolvedPacketCore epc = (EvolvedPacketCore) epcGeneric;
			epc.getPdnGateway().stopInstance();
		}

		LOGGER.info("Deleting instance: " + instanceURI);
		this.instanceList.remove(instanceURI);
	}

	public EpcGeneric getInstanceByName(final String instanceURI) {
		return this.instanceList.get(instanceURI);
	}

	@Override
	public Resource getAdapterABox() {
		LOGGER.info("Getting adapter ABox...");
		return this.adapterABox;
	}

	@Override
	public Model getAdapterDescriptionModel() {
		LOGGER.info("Getting adapter description model...");
		return this.adapterTBox;
	}

	@Override
	public void updateAdapterDescription() {
		LOGGER.warning("updateAdapterDescription() is not implemented.");
	}

	public void addInstance(String uri, EpcGeneric epc) {
		this.instanceList.put(uri, epc);
	}

	@Override
	public Model getInstance(final String instanceURI)
			throws InstanceNotFoundException {

		final EpcGeneric epc = this.instanceList.get(instanceURI);
		LOGGER.warning("Get instance: " + instanceURI);

		if (epc == null) {
			throw new InstanceNotFoundException("Instance " + instanceURI
					+ " not found");
		}
		Model model = this.parseToModel(epc);
		LOGGER.warning("Returning this model from get instance method: "
				+ MessageUtil.serializeModel(model,
						IMessageBus.SERIALIZATION_TURTLE));

		return model;
	}

	public EpcGeneric getInstanceObject(final String instanceURI) {

		final EpcGeneric epc = this.instanceList.get(instanceURI);
		LOGGER.warning("Get instance: " + instanceURI);

		return epc;
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException {
		LOGGER.warning("getAllInstances()");
		LOGGER.warning("instanceList size " + instanceList.size());
		final Model model = ModelFactory.createDefaultModel();

		for (final String uri : this.instanceList.keySet()) {
			LOGGER.warning("instance key " + uri);
			model.add(this.getInstance(uri));
		}
		LOGGER.warning("Returning this model from getAllInstances method: "
				+ MessageUtil.serializeModel(model,
						IMessageBus.SERIALIZATION_TURTLE));
		return model;
	}

	@Override
	public void refreshConfig() throws ProcessingException {
		LOGGER.warning("Not implemented.");
	}

	public String parseConfig(Resource resource, String parameter) {
		Model model = ModelFactory.createDefaultModel();
		return resource
				.getProperty(model.createProperty(Epc.getURI(), parameter))
				.getLiteral().getString();
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
	public void configure(final Config configuration) {
		LOGGER.warning("Not implemented. Input: " + configuration);
	}
}
