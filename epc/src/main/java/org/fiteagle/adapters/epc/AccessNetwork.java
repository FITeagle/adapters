package org.fiteagle.adapters.epc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;

import info.openmultinet.ontology.vocabulary.Epc;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class AccessNetwork extends EpcImplementable {

	private List<AccessPointName> accessPointNames;
	private int band;
	private String baseModel;
	private String eNodeBId;
	private String epcAddress;
	private List<IpAddress> ipAddresses;
	private String mode;
	private String publicLandMobileNetworkId;
	private String vendor;

	private static final Logger LOGGER = Logger.getLogger(AccessNetwork.class
			.toString());

	public AccessNetwork(final EpcAdapter owningAdapter,
			final String instanceName) {

		super(owningAdapter, instanceName);

		this.accessPointNames = new ArrayList<AccessPointName>();
		this.setBand(0);
		this.setBaseModel("");
		this.ipAddresses = new ArrayList<IpAddress>();
		this.seteNodeBId("");
		this.setEpcAddress("");
		this.setMode("");
		this.setPublicLandMobileNetworkId("");
		this.setVendor("");
	}

	@Override
	public void updateInstance(Resource epcResource) {

		if (epcResource.hasProperty(Epc.hasAccessNetwork)) {
			Resource accessNetworkDetails = epcResource
					.getProperty(Epc.hasAccessNetwork).getObject().asResource();

			if (accessNetworkDetails.hasProperty(Epc.band)) {
				this.setBand(accessNetworkDetails.getProperty(Epc.band)
						.getObject().asLiteral().getInt());
			}

			if (accessNetworkDetails.hasProperty(Epc.baseModel)) {
				this.setBaseModel(accessNetworkDetails
						.getProperty(Epc.baseModel).getObject().asLiteral()
						.getString());
			}

			if (accessNetworkDetails.hasProperty(Epc.eNodeBId)) {
				this.seteNodeBId(accessNetworkDetails.getProperty(Epc.eNodeBId)
						.getObject().asLiteral().getString());
			}

			if (accessNetworkDetails.hasProperty(Epc.evolvedPacketCoreAddress)) {
				this.setEpcAddress(accessNetworkDetails
						.getProperty(Epc.evolvedPacketCoreAddress).getObject()
						.asLiteral().getString());
			}

			if (accessNetworkDetails.hasProperty(Epc.mode)) {
				this.setMode(accessNetworkDetails.getProperty(Epc.mode)
						.getObject().asLiteral().getString());
			}

			if (accessNetworkDetails.hasProperty(Epc.publicLandMobileNetworkId)) {
				this.setPublicLandMobileNetworkId(accessNetworkDetails
						.getProperty(Epc.publicLandMobileNetworkId).getObject()
						.asLiteral().getString());
			}

			if (accessNetworkDetails.hasProperty(Epc.vendor)) {
				this.setVendor(accessNetworkDetails.getProperty(Epc.vendor)
						.getObject().asLiteral().getString());
			}

			StmtIterator apns = accessNetworkDetails
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

			StmtIterator ipAddresses = accessNetworkDetails
					.listProperties(info.openmultinet.ontology.vocabulary.Omn_resource.hasIPAddress);
			while (ipAddresses.hasNext()) {
				Statement ipStatement = ipAddresses.next();
				Resource ipResource = ipStatement.getObject().asResource();

				String address = null;
				if (ipResource
						.hasProperty(info.openmultinet.ontology.vocabulary.Omn_resource.address)) {
					address = ipResource
							.getProperty(
									info.openmultinet.ontology.vocabulary.Omn_resource.address)
							.getObject().asLiteral().getString();
				}

				String netmask = null;
				if (ipResource
						.hasProperty(info.openmultinet.ontology.vocabulary.Omn_resource.netmask)) {
					netmask = ipResource
							.getProperty(
									info.openmultinet.ontology.vocabulary.Omn_resource.netmask)
							.getObject().asLiteral().getString();
				}

				String type = null;
				if (ipResource
						.hasProperty(info.openmultinet.ontology.vocabulary.Omn_resource.type)) {
					type = ipResource
							.getProperty(
									info.openmultinet.ontology.vocabulary.Omn_resource.type)
							.getObject().asLiteral().getString();
				}

				IpAddress ip = new IpAddress(address, netmask, type);
				this.addIpAddress(ip);
			}
		}
	}

	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.AccessNetwork);

		String uuid = "urn:uuid:" + UUID.randomUUID().toString();
		final Resource anDetails = resource.getModel().createResource(uuid);
		anDetails.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.AccessNetworkDetails);
		resource.addProperty(
				info.openmultinet.ontology.vocabulary.Epc.hasAccessNetwork,
				anDetails);

		anDetails.addLiteral(info.openmultinet.ontology.vocabulary.Epc.band,
				this.getBand());
		anDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.baseModel,
				this.getBaseModel());
		anDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.eNodeBId,
				this.geteNodeBId());
		anDetails
				.addLiteral(
						info.openmultinet.ontology.vocabulary.Epc.evolvedPacketCoreAddress,
						this.getEpcAddress());
		anDetails.addLiteral(info.openmultinet.ontology.vocabulary.Epc.mode,
				this.getMode());
		anDetails
				.addLiteral(
						info.openmultinet.ontology.vocabulary.Epc.publicLandMobileNetworkId,
						this.getPublicLandMobileNetworkId());
		anDetails.addLiteral(info.openmultinet.ontology.vocabulary.Epc.vendor,
				this.getVendor());

		List<AccessPointName> accessPointNames = this.getApns();
		for (AccessPointName apn : accessPointNames) {

			String uuidApn = "urn:uuid:" + UUID.randomUUID().toString();
			Resource apnResource = anDetails.getModel().createResource(uuidApn);
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
			anDetails
					.addProperty(
							info.openmultinet.ontology.vocabulary.Epc.hasAccessPointName,
							apnResource);
		}

		List<IpAddress> ipAddresses = this.getIpAddresses();
		for (IpAddress ip : ipAddresses) {

			String uuidIp = "urn:uuid:" + UUID.randomUUID().toString();
			Resource ipResource = anDetails.getModel().createResource(uuidIp);
			ipResource
					.addProperty(
							RDF.type,
							info.openmultinet.ontology.vocabulary.Omn_resource.IPAddress);
			ipResource.addLiteral(
					info.openmultinet.ontology.vocabulary.Omn_resource.address,
					ip.getAddress());
			ipResource.addLiteral(
					info.openmultinet.ontology.vocabulary.Omn_resource.netmask,
					ip.getNetmask());
			ipResource.addLiteral(
					info.openmultinet.ontology.vocabulary.Omn_resource.type,
					ip.getType());
			anDetails
					.addProperty(
							info.openmultinet.ontology.vocabulary.Omn_resource.hasIPAddress,
							ipResource);
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

	public int getBand() {
		return band;
	}

	public void setBand(int band) {
		this.band = band;
	}

	public String getBaseModel() {
		return baseModel;
	}

	public void setBaseModel(String baseModel) {
		this.baseModel = baseModel;
	}

	public List<IpAddress> getIpAddresses() {
		return ipAddresses;
	}

	public void addIpAddress(IpAddress ip) {
		LOGGER.info("Adding access point name: " + ip);
		this.ipAddresses.add(ip);
	}

	public String geteNodeBId() {
		return eNodeBId;
	}

	public void seteNodeBId(String eNodeBId) {
		this.eNodeBId = eNodeBId;
	}

	public String getEpcAddress() {
		return epcAddress;
	}

	public void setEpcAddress(String epcAddress) {
		this.epcAddress = epcAddress;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPublicLandMobileNetworkId() {
		return publicLandMobileNetworkId;
	}

	public void setPublicLandMobileNetworkId(String publicLandMobileNetworkId) {
		this.publicLandMobileNetworkId = publicLandMobileNetworkId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

}
