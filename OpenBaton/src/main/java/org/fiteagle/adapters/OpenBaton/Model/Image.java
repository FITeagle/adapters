package org.fiteagle.adapters.OpenBaton.Model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Osco;



public class Image extends OpenBatonGeneric {
	
	private static final Logger LOGGER = Logger.getLogger(Image.class
			.toString());
	
	private String datacenter;
	private String id;

	// private String instanceUri;

	public Image(final OpenBatonAdapter owningAdapter, final String instanceUri) {

		super(owningAdapter, instanceUri);

		this.datacenter = null;
		this.id = null;
		// this.instanceUri = null;
	}

	// public Image(String datacenter, String id, String instanceUri) {
	//
	// super(owningAdapter, instanceUri);
	//
	// this.datacenter = datacenter;
	// this.id = id;
	// this.instanceUri = instanceUri;
	// }

	public void updateInstance(Resource imageResource) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO,
					"Image updateInstance " + imageResource.getURI());
		}

		super.updateInstance(imageResource);

		if (this.getInstanceUri() == null) {
			this.setInstanceUri(imageResource.getURI());
		}

		if (imageResource.hasProperty(Osco.datacenter)) {
			String datacenter = imageResource.getProperty(Osco.datacenter)
					.getObject().asLiteral().getString();

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "Image updateInstance datacenter "
						+ datacenter);
			}

			if (datacenter != null && !datacenter.equals("null")
					&& !datacenter.equals("")) {
				this.setDatacenter(datacenter);
			}
		}

		if (imageResource.hasProperty(Osco.id)) {
			String id = imageResource.getProperty(Osco.id).getObject()
					.asLiteral().getString();

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "Image updateInstance id " + id);
			}

			if (id != null && !id.equals("null") && !id.equals("")) {
				this.setId(id);
			}
		}
	}

	public void parseToModel(Resource imageResource) {

		imageResource.addProperty(RDF.type, Osco.Image);

		super.parseToModel(imageResource);

		if (this.getDatacenter() != null && !this.getDatacenter().equals("")) {
			imageResource.addProperty(Osco.datacenter, this.getDatacenter());
		}

		if (this.getId() != null && !this.getId().equals("")) {
			imageResource.addProperty(Osco.id, this.getId());
		}
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public String getDatacenter() {
		return datacenter;
	}

	public void setDatacenter(String datacenter) {
		this.datacenter = datacenter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getInstanceUri() {
		return instanceUri;
	}

	public void setInstanceUri(String instanceUri) {
		this.instanceUri = instanceUri;
	}

}
