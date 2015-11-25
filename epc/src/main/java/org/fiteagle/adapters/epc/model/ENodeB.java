package org.fiteagle.adapters.epc.model;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ENodeB {

	private String name;
	private String address;

	public ENodeB(final String name, final String address) {

		this.setAddress(address);
		this.setName(name);
	}

	public ENodeB() {
		this.setAddress(null);
		this.setName(null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void updateInstance(Resource eNodeBResource) {

		if (eNodeBResource
				.hasProperty(info.openmultinet.ontology.vocabulary.Epc.eNodeBAddress)) {
			this.address = eNodeBResource
					.getProperty(
							info.openmultinet.ontology.vocabulary.Epc.eNodeBAddress)
					.getObject().asLiteral().getString();
		}

		if (eNodeBResource
				.hasProperty(info.openmultinet.ontology.vocabulary.Epc.eNodeBName)) {
			this.name = eNodeBResource
					.getProperty(
							info.openmultinet.ontology.vocabulary.Epc.eNodeBName)
					.getObject().asLiteral().getString();
		}
	}

	public void parseToModel(Resource eNodeBResource) {

		eNodeBResource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.ENodeB);
		eNodeBResource.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.eNodeBName,
				this.getName());
		eNodeBResource.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.eNodeBAddress,
				this.getAddress());

	}
}