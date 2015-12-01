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
				DiskImage diskImage = new DiskImage();
				diskImage.updateInstance(diskImageResource);
				this.setDiskImage(diskImage);
			}

			if (ueDetails.hasProperty(Epc.hasControlAddress)) {
				Resource controlAddressResource = ueDetails
						.getProperty(Epc.hasControlAddress).getObject()
						.asResource();
				IpAddress controlAddress = new IpAddress();
				controlAddress.updateInstance(controlAddressResource);
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
				AccessPointName apn = new AccessPointName();
				apn.updateInstance(apnResource);
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
			this.getControlAddress().parseToModel(controlAddressResource);
			ueDetails
					.addProperty(Epc.hasControlAddress, controlAddressResource);

		}
		if (this.getDiskImage() != null) {
			// disk image
			String uuidDiskImage = "urn:uuid:" + UUID.randomUUID().toString();
			Resource diskImageResource = ueDetails.getModel().createResource(
					uuidDiskImage);
			this.getDiskImage().parseToModel(diskImageResource);
			ueDetails
					.addProperty(Omn_domain_pc.hasDiskImage, diskImageResource);
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
			apn.parseToModel(apnResource);
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
