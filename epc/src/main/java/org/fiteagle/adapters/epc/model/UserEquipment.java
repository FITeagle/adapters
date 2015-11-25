package org.fiteagle.adapters.epc.model;

import info.openmultinet.ontology.vocabulary.Epc;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import info.openmultinet.ontology.vocabulary.Omn_resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.fiteagle.adapters.epc.EpcAdapter;
import org.fiteagle.adapters.epc.EpcGeneric;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * This class serves to model User Equipment as defined in Deliverable 3.1 from
 * the Flex project, in this case Android phones
 * http://www.flex-project.eu/images/deliverables/FLEX_WP3_D3_1_final.pdf
 * 
 * @author robynloughnane
 *
 */
public class UserEquipment extends EpcGeneric {

	private List<AccessPointName> accessPointNames;
	private boolean lteSupport;
	private String hardwareType;
	private DiskImage diskImage;
	private IpAddress controlAddress;
	// assumes one disk image and one hardware type per device

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

			// super.updateInstance(ueDetails);

			if (ueDetails.hasProperty(Epc.lteSupport)) {
				this.setLteSupport(ueDetails.getProperty(Epc.lteSupport)
						.getObject().asLiteral().getBoolean());
			}

			if (ueDetails.hasProperty(Omn_domain_pc.hasDiskImage)) {
				Resource diskImageResource = ueDetails
						.getProperty(Omn_domain_pc.hasDiskImage).getObject()
						.asResource();

				String name = null;
				String description = null;

				if (diskImageResource
						.hasProperty(Omn_domain_pc.hasDiskimageDescription)) {
					description = diskImageResource
							.getProperty(Omn_domain_pc.hasDiskimageDescription)
							.getLiteral().getString();
				}

				if (diskImageResource
						.hasProperty(Omn_domain_pc.hasDiskimageLabel)) {
					name = diskImageResource
							.getProperty(Omn_domain_pc.hasDiskimageLabel)
							.getLiteral().getString();
				}

				DiskImage diskImage = new DiskImage(name, description);
				this.setDiskImage(diskImage);
			}

			if (ueDetails.hasProperty(Epc.hasControlAddress)) {
				String address = null;
				String netmask = null;
				String type = null;

				Resource controlAddressResource = ueDetails
						.getProperty(Epc.hasControlAddress).getObject()
						.asResource();
				if (controlAddressResource.hasProperty(Omn_resource.address)) {
					address = controlAddressResource
							.getProperty(Omn_resource.address).getLiteral()
							.getString();
				}

				if (controlAddressResource.hasProperty(Omn_resource.netmask)) {
					netmask = controlAddressResource
							.getProperty(Omn_resource.netmask).getLiteral()
							.getString();
				}

				if (controlAddressResource.hasProperty(Omn_resource.type)) {
					type = controlAddressResource
							.getProperty(Omn_resource.type).getLiteral()
							.getString();
				}

				IpAddress controlAddress = new IpAddress(address, netmask, type);
				this.setControlAddress(controlAddress);
			}

			if (ueDetails.hasProperty(Omn_resource.hasHardwareType)) {
				Resource hardwareTypeResource = ueDetails
						.getProperty(Omn_resource.hasHardwareType).getObject()
						.asResource();
				if (hardwareTypeResource.hasProperty(RDFS.label)) {
					String hardwareTypeName = hardwareTypeResource
							.getProperty(RDFS.label).getLiteral().getString();
					this.setHardwareType(hardwareTypeName);
				}
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

		// super.parseToModel(ueDetails);

		resource.addProperty(
				info.openmultinet.ontology.vocabulary.Epc.hasUserEquipment,
				ueDetails);

		ueDetails.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.lteSupport,
				this.isLteSupport());

		if (this.getControlAddress() != null) {
			// control address
			String uuidControlAddress = "urn:uuid:"
					+ UUID.randomUUID().toString();
			Resource controlAddressResource = ueDetails.getModel()
					.createResource(uuidControlAddress);
			controlAddressResource.addProperty(RDF.type, Epc.ControlAddress);
			ueDetails
					.addProperty(Epc.hasControlAddress, controlAddressResource);

			if (this.getControlAddress().getAddress() != null
					&& !this.getControlAddress().getAddress().equals("")) {
				controlAddressResource.addProperty(Omn_resource.address, this
						.getControlAddress().getAddress());
			}
			if (this.getControlAddress().getNetmask() != null
					&& !this.getControlAddress().getNetmask().equals("")) {
				controlAddressResource.addProperty(Omn_resource.netmask, this
						.getControlAddress().getNetmask());
			}
			if (this.getControlAddress().getType() != null
					&& !this.getControlAddress().getType().equals("")) {
				controlAddressResource.addProperty(Omn_resource.type, this
						.getControlAddress().getType());
			}
		}
		if (diskImage != null) {
			// disk image
			String uuidDiskImage = "urn:uuid:" + UUID.randomUUID().toString();
			Resource diskImageResource = ueDetails.getModel().createResource(
					uuidDiskImage);
			diskImageResource.addProperty(RDF.type, Omn_domain_pc.DiskImage);
			ueDetails
					.addProperty(Omn_domain_pc.hasDiskImage, diskImageResource);
			diskImageResource.addProperty(Omn_domain_pc.hasDiskimageLabel, this
					.getDiskImage().getName());
			diskImageResource.addProperty(
					Omn_domain_pc.hasDiskimageDescription, this.getDiskImage()
							.getDescription());
		}

		if (this.getHardwareType() != null) {
			String uuidHwType = "urn:uuid:" + UUID.randomUUID().toString();
			Resource hardwareTypeResource = ueDetails.getModel()
					.createResource(uuidHwType);
			hardwareTypeResource.addProperty(RDF.type,
					Omn_resource.HardwareType);
			ueDetails.addProperty(Omn_resource.hasHardwareType,
					hardwareTypeResource);
			hardwareTypeResource
					.addProperty(RDFS.label, this.getHardwareType());
		}

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

	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}

	public String getHardwareType() {
		return this.hardwareType;
	}

	public void setDiskImage(DiskImage diskImage) {
		this.diskImage = diskImage;
	}

	public DiskImage getDiskImage() {
		return this.diskImage;
	}

	public void setControlAddress(IpAddress ipAddress) {
		this.controlAddress = ipAddress;
	}

	public IpAddress getControlAddress() {
		return this.controlAddress;
	}
}
