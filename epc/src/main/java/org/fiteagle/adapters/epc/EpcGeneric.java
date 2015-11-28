package org.fiteagle.adapters.epc;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * The superclass of all resources that are controlled by the EPC adapter. The
 * three subclasses are AccessNetwork, EvolvedPacketCore and User Equipment.
 * 
 * @author robynloughnane
 *
 */
public class EpcGeneric {

	private final transient EpcAdapter owningAdapter;
	private final String instanceName;

	private String label;

	/**
	 * Constructor method
	 * 
	 * @param owningAdapter
	 * @param instanceName
	 */
	public EpcGeneric(final EpcAdapter owningAdapter, final String instanceName) {
		this.owningAdapter = owningAdapter;
		this.instanceName = instanceName;
		this.label = null;
	}

	/**
	 * Methods to be overridden/expanded in subclasses
	 * 
	 * @param epcResource
	 */
	public void updateInstance(Resource epcResource) {

	}

	public void parseToModel(Resource resource) {
		if (this.getLabel() != null) {
			resource.addLiteral(RDFS.label, this.getLabel());
		}
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public String getInstanceName() {
		return this.instanceName;
	}

	public EpcAdapter getOwningAdapter() {
		return this.owningAdapter;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}