package org.fiteagle.adapters.epc.model;

import info.openmultinet.ontology.vocabulary.Epc;
import info.openmultinet.ontology.vocabulary.Omn_resource;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class IpAddress {

	private String address;
	private String netmask;
	private String type;

	public IpAddress(final String address, final String netmask,
			final String type) {

		this.setAddress(address);
		this.setNetmask(netmask);
		this.setType(type);
	}

	public IpAddress() {
		this.address = null;
		this.netmask = null;
		this.type = null;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void parseToModel(Resource controlAddressResource) {
		controlAddressResource.addProperty(RDF.type, Epc.ControlAddress);

		if (this.getAddress() != null && !this.getAddress().equals("")) {
			controlAddressResource.addProperty(Omn_resource.address,
					this.getAddress());
		}
		if (this.getNetmask() != null && !this.getNetmask().equals("")) {
			controlAddressResource.addProperty(Omn_resource.netmask,
					this.getNetmask());
		}
		if (this.getType() != null && !this.getType().equals("")) {
			controlAddressResource.addProperty(Omn_resource.type,
					this.getType());
		}

	}

	public void updateInstance(Resource controlAddressResource) {

		if (controlAddressResource.hasProperty(Omn_resource.address)) {
			String address = controlAddressResource
					.getProperty(Omn_resource.address).getLiteral().getString();
			this.setAddress(address);
		}

		if (controlAddressResource.hasProperty(Omn_resource.netmask)) {
			String netmask = controlAddressResource
					.getProperty(Omn_resource.netmask).getLiteral().getString();
			this.setNetmask(netmask);
		}

		if (controlAddressResource.hasProperty(Omn_resource.type)) {
			String type = controlAddressResource.getProperty(Omn_resource.type)
					.getLiteral().getString();
			this.setType(type);
		}

	}

}
