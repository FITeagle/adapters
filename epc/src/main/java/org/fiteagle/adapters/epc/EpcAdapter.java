package org.fiteagle.adapters.epc;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class EpcAdapter extends AbstractAdapter {

	private static final List<Property> EPC_CTRL_PROPS = new ArrayList<Property>();

	private transient final HashMap<String, EpcImplementable> instanceList = new HashMap<String, EpcImplementable>();
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
			this.updateInstance(instanceURI, modelCreate);
			return this.parseToModel(accessNetwork);
		} else if (resource.hasProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.EvolvedPacketCore)) {
			LOGGER.warning("Try to update instance: " + instanceURI);
			final Epc epc = new Epc(this, instanceURI);
			this.instanceList.put(instanceURI, epc);
			this.updateInstance(instanceURI, modelCreate);
			return this.parseToModel(epc);
		} else if (resource.hasProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.UserEquipment)) {
			LOGGER.warning("Try to update instance: " + instanceURI);
			final UserEquipment ue = new UserEquipment(this, instanceURI);
			this.instanceList.put(instanceURI, ue);
			this.updateInstance(instanceURI, modelCreate);
			return this.parseToModel(ue);
		}

		LOGGER.warning("Couldn't recognize type, so returning original model.");
		return modelCreate;
	}

	@SuppressWarnings("PMD.GuardLogStatementJavaUtil")
	Model parseToModel(final EpcImplementable epcImplementable) {

		final Resource resource = ModelFactory.createDefaultModel()
				.createResource(epcImplementable.getInstanceName());

		resource.addProperty(RDFS.label, resource.getLocalName());
		final Property property = resource.getModel().createProperty(
				Omn_lifecycle.hasState.getNameSpace(),
				Omn_lifecycle.hasState.getLocalName());
		// property.addProperty(RDF.type, OWL.FunctionalProperty);
		resource.addProperty(property, Omn_lifecycle.Ready);

		if (epcImplementable instanceof AccessNetwork) {
			AccessNetwork an = (AccessNetwork) epcImplementable;
			an.parseToModel(resource);
		} else if (epcImplementable instanceof Epc) {
			Epc epc = (Epc) epcImplementable;
			epc.parseToModel(resource);
		} else if (epcImplementable instanceof UserEquipment) {
			UserEquipment ue = (UserEquipment) epcImplementable;
			ue.parseToModel(resource);
		}
		return resource.getModel();
	}

	@Override
	public Model updateInstance(final String instanceURI,
			final Model configureModel) {

		// if the instance is in the list of instances in the adapter
		if (this.instanceList.containsKey(instanceURI)) {
			final EpcImplementable currentEpc = this.instanceList
					.get(instanceURI);
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
		final EpcImplementable epc = this.getInstanceByName(instanceURI);
		// epc.terminate();
		LOGGER.info("Deleting instance: " + instanceURI);
		this.instanceList.remove(instanceURI);
	}

	private EpcImplementable getInstanceByName(final String instanceURI) {
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
		LOGGER.warning("Not implemented.");
	}

	@Override
	public Model getInstance(final String instanceURI)
			throws InstanceNotFoundException {
		final EpcImplementable epc = this.instanceList.get(instanceURI);
		if (epc == null) {
			throw new InstanceNotFoundException("Instance " + instanceURI
					+ " not found");
		}
		return this.parseToModel(epc);
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
