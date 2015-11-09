package org.fiteagle.adapters.epc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class Epc extends EpcImplementable {

	private List<AccessPointName> accessPointNames;
	private String mmeAddress;
	private String pdnGateway;
	private String servingGateway;
	private List<String> subscribers;
	private String vendor;
	// private List<ENodeB> eNodeB;

	// private final transient EpcAdapter owningAdapter;
	// private final String instanceName;
	//
	// @SuppressWarnings("PMD.DoNotUseThreads")
	// private transient Thread thread;
	// private final static String THREAD_FACTORY =
	// "java:jboss/ee/concurrency/factory/default";
	// private transient ManagedThreadFactory threadFactory;

	private static final Logger LOGGER = Logger.getLogger(Epc.class.toString());

	public Epc(final EpcAdapter owningAdapter, final String instanceName) {
		super(owningAdapter, instanceName);

		this.accessPointNames = new ArrayList<AccessPointName>();
		this.mmeAddress = "";
		this.pdnGateway = "";
		this.servingGateway = "";
		this.subscribers = new ArrayList<String>();
		this.vendor = "";
	}

	@Override
	public void updateInstance(Resource epcResource) {

		if (epcResource
				.hasProperty(info.openmultinet.ontology.vocabulary.Epc.hasEvolvedPacketCore)) {
			Resource epcDetails = epcResource
					.getProperty(
							info.openmultinet.ontology.vocabulary.Epc.hasEvolvedPacketCore)
					.getObject().asResource();

			// below assumes that each propertie only occurs once, really need
			// an interator or something to catch all properties
			// StmtIterator properties = epcDetails.listProperties();
			// while (properties.hasNext()) {
			// Statement next = properties.next();
			// }

			if (epcDetails
					.hasProperty(info.openmultinet.ontology.vocabulary.Epc.mmeAddress)) {
				this.setMmeAddress(epcDetails
						.getProperty(
								info.openmultinet.ontology.vocabulary.Epc.mmeAddress)
						.getObject().asLiteral().getString());
			}

			if (epcDetails
					.hasProperty(info.openmultinet.ontology.vocabulary.Epc.pdnGateway)) {
				this.setPdnGateway(epcDetails
						.getProperty(
								info.openmultinet.ontology.vocabulary.Epc.pdnGateway)
						.getObject().asLiteral().getString());
			}

			if (epcDetails
					.hasProperty(info.openmultinet.ontology.vocabulary.Epc.servingGateway)) {
				this.setServingGateway(epcDetails
						.getProperty(
								info.openmultinet.ontology.vocabulary.Epc.servingGateway)
						.getObject().asLiteral().getString());
			}

			StmtIterator subscriberProperties = epcDetails
					.listProperties(info.openmultinet.ontology.vocabulary.Epc.subscriber);
			while (subscriberProperties.hasNext()) {
				Statement subscriberProperty = subscriberProperties.next();
				this.addSubscriber(subscriberProperty.getObject().asLiteral()
						.getString());
			}

			StmtIterator apns = epcDetails
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
				info.openmultinet.ontology.vocabulary.Epc.EvolvedPacketCore);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);
		// String uuid = "urn:uuid:" + UUID.randomUUID().toString();
		String resourceUri = resource.getURI().toString() + "-details";
		final Resource epcDetails = resource.getModel().createResource(
				resourceUri);
		epcDetails
				.addProperty(
						RDF.type,
						info.openmultinet.ontology.vocabulary.Epc.EvolvedPacketCoreDetails);
		resource.addProperty(
				info.openmultinet.ontology.vocabulary.Epc.hasEvolvedPacketCore,
				epcDetails);

		epcDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.mmeAddress,
				this.getMmeAddress());

		epcDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.pdnGateway,
				this.getPdnGateway());

		epcDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.servingGateway,
				this.getServingGateway());

		epcDetails.addLiteral(info.openmultinet.ontology.vocabulary.Epc.vendor,
				this.getVendor());

		List<String> subscribers = this.getSubscribers();
		for (String subscriber : subscribers) {
			epcDetails.addLiteral(
					info.openmultinet.ontology.vocabulary.Epc.subscriber,
					subscriber);
		}

		List<AccessPointName> accessPointNames = this.getApns();
		for (AccessPointName apn : accessPointNames) {

			String uuidApn = "urn:uuid:" + UUID.randomUUID().toString();
			Resource apnResource = epcDetails.getModel()
					.createResource(uuidApn);
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
			epcDetails
					.addProperty(
							info.openmultinet.ontology.vocabulary.Epc.hasAccessPointName,
							apnResource);
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

	public String getMmeAddress() {
		return this.mmeAddress;
	}

	public void setMmeAddress(final String mmeAddress) {
		LOGGER.info("Setting MME address: " + mmeAddress);
		this.mmeAddress = mmeAddress;
	}

	public String getPdnGateway() {
		return this.pdnGateway;
	}

	public void setPdnGateway(final String pdnGateway) {
		LOGGER.info("Setting PDN gateway: " + pdnGateway);
		this.pdnGateway = pdnGateway;
	}

	public String getServingGateway() {
		return this.servingGateway;
	}

	public void setServingGateway(final String servingGateway) {
		LOGGER.info("Setting PDN gateway: " + servingGateway);
		this.servingGateway = servingGateway;
	}

	public List<String> getSubscribers() {
		return subscribers;
	}

	public void addSubscriber(String subscriber) {
		LOGGER.info("Adding subscriber: " + subscriber);
		this.subscribers.add(subscriber);
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		LOGGER.info("Setting vendor: " + vendor);
		this.vendor = vendor;
	}

}
