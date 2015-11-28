package org.fiteagle.adapters.epc.model;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class AccessPointName {

	private String networkIdentifier;
	private String operatorIdentifier;

	public AccessPointName(final String networkIdentifier,
			final String operatorIdentifier) {

		this.networkIdentifier = networkIdentifier;
		this.operatorIdentifier = operatorIdentifier;
	}

	public AccessPointName() {
		this.networkIdentifier = null;
		this.operatorIdentifier = null;
	}

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

	public void setNetworkIdentifier(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	}

	public String getOperatorIdentifier() {
		return operatorIdentifier;
	}

	public void setOperatorIdentifier(String operatorIdentifier) {
		this.operatorIdentifier = operatorIdentifier;
	}

	public void parseToModel(Resource apnResource) {
		apnResource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.AccessPointName);

		apnResource.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.networkIdentifier,
				this.getNetworkIdentifier());
		apnResource.addLiteral(
				info.openmultinet.ontology.vocabulary.Epc.operatorIdentifier,
				this.getOperatorIdentifier());
	}

	public void updateInstance(Resource apnResource) {

		if (apnResource
				.hasProperty(info.openmultinet.ontology.vocabulary.Epc.networkIdentifier)) {
			this.networkIdentifier = apnResource
					.getProperty(
							info.openmultinet.ontology.vocabulary.Epc.networkIdentifier)
					.getObject().asLiteral().getString();
		}

		if (apnResource
				.hasProperty(info.openmultinet.ontology.vocabulary.Epc.operatorIdentifier)) {
			this.operatorIdentifier = apnResource
					.getProperty(
							info.openmultinet.ontology.vocabulary.Epc.operatorIdentifier)
					.getObject().asLiteral().getString();
		}

	}
}
