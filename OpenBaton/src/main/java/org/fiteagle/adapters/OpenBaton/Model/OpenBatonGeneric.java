package org.fiteagle.adapters.OpenBaton.Model;

import javax.json.JsonObject;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;

import info.openmultinet.ontology.vocabulary.Fiveg;
import info.openmultinet.ontology.vocabulary.OpenBaton;

public class OpenBatonGeneric {
	
	private final transient OpenBatonAdapter owningAdapter;
	String instanceUri;

	String oscoName;
	String oscoState;
	String oscoId;
	Integer oscoVersion;

	// private String label;

	/**
	 * Constructor method
	 * 
	 * @param owningAdapter
	 * @param instanceUri
	 */
	public OpenBatonGeneric(final OpenBatonAdapter owningAdapter,
			final String instanceUri) {
		this.owningAdapter = owningAdapter;
		this.instanceUri = instanceUri;

		this.oscoName = null;
		this.oscoId = null;
		this.oscoState = null;
		this.oscoVersion = null;
	}

	/**
	 * Methods to be overridden/expanded in subclasses
	 * 
	 * @param fivegResource
	 */
	public void updateInstance(Resource fivegResource) {
	}

	public void parseToModel(Resource resource) {
		if (this.getOscoName() != null && !this.getOscoName().equals("")) {
			String name = this.getOscoName();
			resource.addLiteral(Fiveg.oscoName, name);
		}

		if (this.getOscoId() != null && !this.getOscoId().equals("")) {
			String id = this.getOscoId();
			resource.addLiteral(Fiveg.oscoId, id);
		}

		if (this.getOscoState() != null && !this.getOscoState().equals("")) {
			String state = this.getOscoState();
			resource.addLiteral(Fiveg.oscoState, state);
		}

		if (this.getOscoVersion() != null) {
			Integer version = this.getOscoVersion();
			resource.addLiteral(Fiveg.oscoVersion, version);
		}
	}

	public JsonObject parseToJson() {
		return null;
	}

	public void updateInstance(JsonObject jsonObject) {

		String id = jsonObject.getString("id");
		if (id != null && !id.equals("")) {
			this.setOscoId(id);
		}

		if (!(this instanceof Subnet)) {
			String state = jsonObject.getString("state");
			if (state != null && !state.equals("")) {
				this.setOscoState(state);
			}
		}

		String name = null;
		if (this instanceof Topology) {
			name = jsonObject.getString("name");
		} else if (this instanceof ServiceContainer) {
			name = jsonObject.getString("containerName");
		}
		if (name != null && !name.equals("")) {
			this.setOscoName(name);
		}

		Integer version = jsonObject.getInt("version");
		if (version != null) {
			this.setOscoVersion(version);
		}
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */

	public OpenBatonAdapter getOwningAdapter() {
		return this.owningAdapter;
	}

	public String getInstanceUri() {
		return this.instanceUri;
	}

	public void setInstanceUri(String instanceUri) {
		this.instanceUri = instanceUri;
	}

	public String getOscoState() {
		return oscoState;
	}

	public void setOscoState(String oscoState) {
		this.oscoState = oscoState;
	}

	public String getOscoId() {
		return oscoId;
	}

	public void setOscoId(String oscoId) {
		this.oscoId = oscoId;
	}

	public Integer getOscoVersion() {
		return oscoVersion;
	}

	public void setOscoVersion(Integer oscoVersion) {
		this.oscoVersion = oscoVersion;
	}

	public String getOscoName() {
		return oscoName;
	}

	public void setOscoName(String oscoName) {
		this.oscoName = oscoName;
	}
}
