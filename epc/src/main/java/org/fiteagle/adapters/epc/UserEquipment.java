package org.fiteagle.adapters.epc;

import info.openmultinet.ontology.vocabulary.Epc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class UserEquipment extends EpcGeneric {

	private List<AccessPointName> accessPointNames;
	private boolean lteSupport;
	// private List<HardwareType> hardwareTypes;
	// private List<DiskImage> diskImages;
	// private IPAddress controlAddress;

	private static final Logger LOGGER = Logger.getLogger(UserEquipment.class
			.toString());

	public UserEquipment(final EpcAdapter owningAdapter,
			final String instanceName) {

		super(owningAdapter, instanceName);

		this.accessPointNames = new ArrayList<AccessPointName>();
		this.setLteSupport(false);
	}

	@Override
	public void updateInstance(Resource epcResource) {

		if (epcResource.hasProperty(RDFS.label)) {

			String label = epcResource.getProperty(RDFS.label).getObject()
					.asLiteral().getString();

			this.setLabel(label);
		}

		if (epcResource.hasProperty(Epc.hasUserEquipment)) {
			Resource ueDetails = epcResource.getProperty(Epc.hasUserEquipment)
					.getObject().asResource();

			super.updateInstance(ueDetails);

			if (ueDetails.hasProperty(Epc.lteSupport)) {
				this.setLteSupport(ueDetails.getProperty(Epc.lteSupport)
						.getObject().asLiteral().getBoolean());
			}

			StmtIterator apns = ueDetails
					.listProperties(info.openmultinet.ontology.vocabulary.Epc.hasAccessPointName);
			while (apns.hasNext()) {
				Statement apnStatement = apns.next();
				Resource apnResource = apnStatement.getObject().asResource();

				String networkIdentifier = null;
				if (apnResource
						.hasProperty(info.openmultinet.ontology.vocabulary.Epc.networkIdentifier)) {
					networkIdentifier = apnResource
							.getProperty(
									info.openmultinet.ontology.vocabulary.Epc.networkIdentifier)
							.getObject().asLiteral().getString();
				}

				String operatorIdentifier = null;
				if (apnResource
						.hasProperty(info.openmultinet.ontology.vocabulary.Epc.operatorIdentifier)) {
					operatorIdentifier = apnResource
							.getProperty(
									info.openmultinet.ontology.vocabulary.Epc.operatorIdentifier)
							.getObject().asLiteral().getString();
				}
				AccessPointName apn = new AccessPointName(networkIdentifier,
						operatorIdentifier);
				this.addApn(apn);
			}

		}

	}

	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.UserEquipment);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);

		// String uuid = "urn:uuid:" + UUID.randomUUID().toString();
		String resourceUri = resource.getURI().toString() + "-details";
		final Resource ueDetails = resource.getModel().createResource(
				resourceUri);
		ueDetails.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.UserEquipmentDetails);

		super.parseToModel(ueDetails);

		resource.addProperty(
				info.openmultinet.ontology.vocabulary.Epc.hasUserEquipment,
				ueDetails);

		ueDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.lteSupport,
				this.isLteSupport());

		List<AccessPointName> accessPointNames = this.getApns();
		for (AccessPointName apn : accessPointNames) {

			String uuidApn = "urn:uuid:" + UUID.randomUUID().toString();
			Resource apnResource = ueDetails.getModel().createResource(uuidApn);
			apnResource.addProperty(RDF.type,
					info.openmultinet.ontology.vocabulary.Epc.AccessPointName);
			apnResource
					.addLiteral(
							info.openmultinet.ontology.vocabulary.Epc.networkIdentifier,
							apn.getNetworkIdentifier());
			apnResource
					.addLiteral(
							info.openmultinet.ontology.vocabulary.Epc.operatorIdentifier,
							apn.getOperatorIdentifier());
			ueDetails
					.addProperty(
							info.openmultinet.ontology.vocabulary.Epc.hasAccessPointName,
							apnResource);
		}
	}

	public void updateProperty(final Statement configureStatement) {
		if (configureStatement.getSubject().getURI()
				.equals(this.getInstanceName())) {
			Property predicate = configureStatement.getPredicate();

			if (predicate.equals(Epc.lteSupport)) {
				this.setLteSupport(configureStatement.getObject().asLiteral()
						.getBoolean());
			} else {
				LOGGER.warning("Unknown predicate: " + predicate);
			}
		} else {
			LOGGER.warning("Unknown URI: "
					+ configureStatement.getSubject().getURI());
			LOGGER.warning("Expected URI: " + this.getInstanceName());
		}
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public List<AccessPointName> getApns() {
		return accessPointNames;
	}

	public void addApn(AccessPointName apn) {
		LOGGER.info("Adding access point name: " + apn);
		this.accessPointNames.add(apn);
	}

	public boolean isLteSupport() {
		return this.lteSupport;
	}

	public void setLteSupport(final boolean lteSupport) {
		LOGGER.info("Setting LTE Suport: " + lteSupport);
		this.lteSupport = lteSupport;
	}
}
