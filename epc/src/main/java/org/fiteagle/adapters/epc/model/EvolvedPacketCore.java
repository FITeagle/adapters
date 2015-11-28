package org.fiteagle.adapters.epc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.fiteagle.adapters.epc.EpcAdapter;
import org.fiteagle.adapters.epc.EpcGeneric;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class serves to model Evolved Packet Cores as defined in Deliverable 3.1
 * from the Flex project, where there are two different EPC networks (SiRRAN’s
 * and OpenAirInterface)
 * http://www.flex-project.eu/images/deliverables/FLEX_WP3_D3_1_final.pdf
 * 
 * @author robynloughnane
 *
 */
public class EvolvedPacketCore extends EpcGeneric {

	private List<AccessPointName> accessPointNames;
	private String mmeAddress;
	private PDNGateway pdnGateway;
	private String servingGateway;
	private List<String> subscribers;
	private String vendor;
	private List<ENodeB> eNodeBs;

	private static final Logger LOGGER = Logger
			.getLogger(EvolvedPacketCore.class.toString());

	public EvolvedPacketCore(final EpcAdapter owningAdapter,
			final String instanceName) {
		super(owningAdapter, instanceName);

		this.accessPointNames = new ArrayList<AccessPointName>();
		this.mmeAddress = "";
		this.servingGateway = "";
		this.subscribers = new ArrayList<String>();
		this.eNodeBs = new ArrayList<ENodeB>();
		this.vendor = "";

		String ip = this.getOwningAdapter().parseConfig(
				this.getOwningAdapter().getAdapterABox(), "pgwIp");
		String start = this.getOwningAdapter().parseConfig(
				this.getOwningAdapter().getAdapterABox(), "pgwStart");
		String stop = this.getOwningAdapter().parseConfig(
				this.getOwningAdapter().getAdapterABox(), "pgwStop");

		LOGGER.info("Create PDN Gateway with pgwIp: " + ip);
		this.pdnGateway = new PDNGateway(ip, start, stop);
	}

	@Override
	public void updateInstance(Resource epcResource) {

		if (epcResource.hasProperty(RDFS.label)) {

			String label = epcResource.getProperty(RDFS.label).getObject()
					.asLiteral().getString();

			this.setLabel(label);
		}

		if (epcResource
				.hasProperty(info.openmultinet.ontology.vocabulary.Epc.hasEvolvedPacketCore)) {

			Resource epcDetails = epcResource
					.getProperty(
							info.openmultinet.ontology.vocabulary.Epc.hasEvolvedPacketCore)
					.getObject().asResource();

			super.updateInstance(epcDetails);

			// below assumes that each property only occurs once, really need
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
					.hasProperty(info.openmultinet.ontology.vocabulary.Epc.vendor)) {
				String vendor = epcDetails
						.getProperty(
								info.openmultinet.ontology.vocabulary.Epc.vendor)
						.getLiteral().getString();
				this.setVendor(vendor);
			}

			if (epcDetails
					.hasProperty(info.openmultinet.ontology.vocabulary.Epc.pdnGateway)) {
				Resource pgwResource = epcDetails
						.getProperty(
								info.openmultinet.ontology.vocabulary.Epc.pdnGateway)
						.getObject().asResource();
				this.getPdnGateway().updateInstance(pgwResource);
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
				AccessPointName apn = new AccessPointName();
				apn.updateInstance(apnResource);
				this.addApn(apn);
			}

			StmtIterator eNodeBs = epcDetails
					.listProperties(info.openmultinet.ontology.vocabulary.Epc.hasENodeB);
			while (eNodeBs.hasNext()) {
				Statement eNodeBStatement = eNodeBs.next();
				Resource eNodeBResource = eNodeBStatement.getObject()
						.asResource();
				ENodeB eNodeB = new ENodeB();
				eNodeB.updateInstance(eNodeBResource);

				this.addENodeB(eNodeB);
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

		super.parseToModel(epcDetails);

		resource.addProperty(
				info.openmultinet.ontology.vocabulary.Epc.hasEvolvedPacketCore,
				epcDetails);

		epcDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.mmeAddress,
				this.getMmeAddress());

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

		String pgwUrl = resource.getURI().toString() + "-pgw";
		Resource pgwResource = epcDetails.getModel().createResource(pgwUrl);
		this.getPdnGateway().parseToModel(pgwResource);
		epcDetails.addProperty(
				info.openmultinet.ontology.vocabulary.Epc.pdnGateway,
				pgwResource);

		List<AccessPointName> accessPointNames = this.getApns();
		for (AccessPointName apn : accessPointNames) {

			String uuidApn = "urn:uuid:" + UUID.randomUUID().toString();
			Resource apnResource = epcDetails.getModel()
					.createResource(uuidApn);
			apn.parseToModel(apnResource);
			epcDetails
					.addProperty(
							info.openmultinet.ontology.vocabulary.Epc.hasAccessPointName,
							apnResource);
		}

		List<ENodeB> eNodeBs = this.getENodeBs();
		for (ENodeB eNodeB : eNodeBs) {

			String uuidENodeB = "urn:uuid:" + UUID.randomUUID().toString();
			Resource eNodeBResource = epcDetails.getModel().createResource(
					uuidENodeB);
			eNodeB.parseToModel(eNodeBResource);
			epcDetails.addProperty(
					info.openmultinet.ontology.vocabulary.Epc.hasENodeB,
					eNodeBResource);
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

	public PDNGateway getPdnGateway() {
		return this.pdnGateway;
	}

	public void setPdnGateway(final PDNGateway pdnGateway) {
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
		return this.subscribers;
	}

	public void addSubscriber(String subscriber) {
		LOGGER.info("Adding subscriber: " + subscriber);
		this.getSubscribers().add(subscriber);
	}

	public String getVendor() {
		return this.vendor;
	}

	public void setVendor(String vendor) {
		LOGGER.info("Setting vendor: " + vendor);
		this.vendor = vendor;
	}

	public List<ENodeB> getENodeBs() {
		return this.eNodeBs;
	}

	public void addENodeB(ENodeB eNodeB) {
		LOGGER.info("Adding eNodeB: " + eNodeB);
		this.getENodeBs().add(eNodeB);
	}

}
