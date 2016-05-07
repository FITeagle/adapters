package org.fiteagle.adapters.OpenBaton.Model;

import javax.json.JsonObject;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Osco;

public class Subnet extends OpenBatonGeneric{
	private String datacenter;
	private String name;
	// private String instanceUri;
	private Boolean mgmt;

	public Subnet(final OpenBatonAdapter owningAdapter, final String instanceUri) {

		super(owningAdapter, instanceUri);

		this.datacenter = null;
		this.name = null;
		// this.setInstanceUri(null);
	}

	// public Subnet(String datacenter, String name, String instanceUri) {
	// this.datacenter = datacenter;
	// this.name = name;
	// this.setInstanceUri(instanceUri);
	// this.mgmt = null;
	// }

	public void updateInstance(Resource subnetResource) {

		super.updateInstance(subnetResource);

		if (this.getInstanceUri() == null) {
			this.setInstanceUri(subnetResource.getURI());
		}

		if (subnetResource.hasProperty(Osco.datacenter)) {
			String datacenter = subnetResource.getProperty(Osco.datacenter)
					.getObject().asLiteral().getString();
			this.setDatacenter(datacenter);
		}

		if (subnetResource.hasProperty(RDFS.label)) {
			String name = subnetResource.getProperty(RDFS.label).getObject()
					.asLiteral().getString();
			this.setName(name);
		}
		
		if (subnetResource.hasProperty(Osco.mgmt)) {
			Boolean mgmt = subnetResource.getProperty(Osco.mgmt)
					.getObject().asLiteral().getBoolean();
			this.setMgmt(mgmt);
		}

	}

	@Override
	public void updateInstance(JsonObject jsonObject) {

		super.updateInstance(jsonObject);

		Boolean mgmt = jsonObject.getBoolean("mgmt");
		this.setMgmt(mgmt);
	}

	public void parseToModel(Resource subnetResource) {

		subnetResource.addProperty(RDF.type, Osco.Subnet);

		super.parseToModel(subnetResource);

		if (this.getDatacenter() != null && !this.getDatacenter().equals("")) {
			subnetResource.addProperty(Osco.datacenter, this.getDatacenter());
		}

		if (this.getName() != null && !this.getName().equals("")) {
			subnetResource.addProperty(RDFS.label, this.getName());
		}

		if (this.getMgmt() != null) {
			subnetResource.addLiteral(Osco.mgmt, this.getMgmt());
		}
	}

	/**
	 * Getters and setters
	 */

	public String getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(String datacenter) {
		this.datacenter = datacenter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	// public String getInstanceUri() {
	// return instanceUri;
	// }
	//
	// public void setInstanceUri(String instanceUri) {
	// this.instanceUri = instanceUri;
	// }

	public Boolean getMgmt() {
		return mgmt;
	}

	public void setMgmt(Boolean mgmt) {
		this.mgmt = mgmt;
	}
}
