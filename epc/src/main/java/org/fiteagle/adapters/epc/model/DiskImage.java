package org.fiteagle.adapters.epc.model;

import info.openmultinet.ontology.vocabulary.Omn_domain_pc;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class DiskImage {

	private String name;
	private String description;

	public DiskImage(final String name, final String description) {

		this.setDescription(description);
		this.setName(name);
	}

	public DiskImage() {
		this.name = null;
		this.description = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void parseToModel(Resource diskImageResource) {
		diskImageResource.addProperty(RDF.type, Omn_domain_pc.DiskImage);
		diskImageResource.addProperty(Omn_domain_pc.hasDiskimageLabel,
				this.getName());
		diskImageResource.addProperty(Omn_domain_pc.hasDiskimageDescription,
				this.getDescription());
	}

	public void updateInstance(Resource diskImageResource) {

		if (diskImageResource
				.hasProperty(Omn_domain_pc.hasDiskimageDescription)) {
			String description = diskImageResource
					.getProperty(Omn_domain_pc.hasDiskimageDescription)
					.getLiteral().getString();
			this.setDescription(description);
		}

		if (diskImageResource.hasProperty(Omn_domain_pc.hasDiskimageLabel)) {
			String name = diskImageResource
					.getProperty(Omn_domain_pc.hasDiskimageLabel).getLiteral()
					.getString();
			this.setName(name);
		}

	}

}
