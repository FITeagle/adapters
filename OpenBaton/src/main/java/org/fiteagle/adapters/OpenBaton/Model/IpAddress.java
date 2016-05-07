package org.fiteagle.adapters.OpenBaton.Model;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Omn_resource;

public class IpAddress {
	private String address;
	private String netmask;
	private String type;
	private String instanceUri;

	public IpAddress() {
		this.address = null;
		this.netmask = null;
		this.type = null;
		this.instanceUri = null;
	}

	public void parseToModel(Resource ipAddressResource) {
		ipAddressResource.addProperty(RDF.type, Omn_resource.IPAddress);

		if (this.getAddress() != null && !this.getAddress().equals("")) {
			ipAddressResource.addProperty(Omn_resource.address,
					this.getAddress());
		}
		if (this.getNetmask() != null && !this.getNetmask().equals("")) {
			ipAddressResource.addProperty(Omn_resource.netmask,
					this.getNetmask());
		}
		if (this.getType() != null && !this.getType().equals("")) {
			ipAddressResource.addProperty(Omn_resource.type, this.getType());
		}
	}

	public void updateInstance(Resource ipAddressResource) {

		if (this.getInstanceUri() == null) {
			this.setInstanceUri(ipAddressResource.getURI());
		}

		if (ipAddressResource.hasProperty(Omn_resource.address)) {
			String address = ipAddressResource
					.getProperty(Omn_resource.address).getLiteral().getString();
			this.setAddress(address);
		}

		if (ipAddressResource.hasProperty(Omn_resource.netmask)) {
			String netmask = ipAddressResource
					.getProperty(Omn_resource.netmask).getLiteral().getString();
			this.setNetmask(netmask);
		}

		if (ipAddressResource.hasProperty(Omn_resource.type)) {
			String type = ipAddressResource.getProperty(Omn_resource.type)
					.getLiteral().getString();
			this.setType(type);
		}

	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */

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

	public String getInstanceUri() {
		return instanceUri;
	}

	public void setInstanceUri(String instanceUri) {
		this.instanceUri = instanceUri;
	}
}
