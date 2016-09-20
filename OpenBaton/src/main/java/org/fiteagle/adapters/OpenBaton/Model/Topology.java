package org.fiteagle.adapters.OpenBaton.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
import org.fiteagle.adapters.OpenBaton.OpenBatonClient;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_resource;

public class Topology extends OpenBatonGeneric {
	List<String> locations;
	List<ServiceContainer> serviceContainers;
	String projectId;
	OpenBatonClient projectClient;
	String experimenterName;

	private static final Logger LOGGER = Logger.getLogger(Topology.class
			.toString());

	/**
	 * Constructor method
	 * 
	 * @param owningAdapter
	 * @param instanceUri
	 */
	public Topology(final OpenBatonAdapter owningAdapter, final String instanceUri) {

		super(owningAdapter, instanceUri);

		this.locations = new ArrayList<String>();
		this.serviceContainers = new ArrayList<ServiceContainer>();

		// TODO: fix to make locations work properly
		this.locations.add("Berlin2");
	}

	/**
	 * Methods to be overridden/expanded in subclasses
	 * 
	 * @param fivegResource
	 */

	@Override
	public void updateInstance(Resource fivegResource) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update topology instance: "
					+ this.getOwningAdapter().getInstanceUri(this));
		}

		super.updateInstance(fivegResource);

		// TODO: update locations
		// TODO: update service containers and resources

	}

	@Override
	public void parseToModel(Resource resource) {

		resource.addProperty(RDF.type, Omn.Topology);

		super.parseToModel(resource);

		for (String location : this.getLocations()) {
			String uuid = "urn:uuid:" + UUID.randomUUID().toString();
			Resource locationResource = resource.getModel()
					.createResource(uuid);
			locationResource.addProperty(RDF.type, Omn_resource.Location);
			locationResource.addProperty(RDFS.label, location);
			resource.addProperty(Omn_resource.hasLocation, locationResource);
		}

		// add Omn.hasResource property for all of the service containers and
		// resources of the topology
		for (ServiceContainer sc : this.getServiceContainers()) {
			Resource scResource = resource.getModel().createResource(
					sc.getInstanceUri());
			resource.addProperty(Omn.hasResource, scResource);

			for (OpenBatonGeneric service : sc.getServices()) {
				Resource serviceResource = resource.getModel().createResource(
						service.getInstanceUri());
				resource.addProperty(Omn.hasResource, serviceResource);
			}
		}

	}

	@Override
	public void updateInstance(JsonObject jsonObject) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update from JsonObject for "
					+ this.getOwningAdapter().getInstanceUri(this));
		}

		super.updateInstance(jsonObject);

		// TODO: update locations

		JsonArray serviceContainers = jsonObject
				.getJsonArray("serviceContainers");
		for (int i = 0; i < serviceContainers.size(); i++) {
			JsonObject serviceContainer = serviceContainers.getJsonObject(i);
			String containerName = serviceContainer.getString("containerName");

			for (ServiceContainer sc : this.getServiceContainers()) {
				if (sc.getContainerName() != null
						&& sc.getContainerName().equals(containerName)) {
					sc.updateInstance(serviceContainer);
				}
			}
		}
	}

	@Override
	public JsonObject parseToJson() {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "parseToJson : " + this.getInstanceUri());
		}

		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder serviceContainerBuilder = Json.createArrayBuilder();

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO,
					"parseToJson number of service containers : "
							+ this.getServiceContainers().size());
		}

		for (ServiceContainer sc : this.getServiceContainers()) {
			JsonObject json = sc.parseToJson();
			serviceContainerBuilder.add(json);
		}

		String name = null;
		if (this.getOscoName() != null) {
			name = this.getOscoName();
		} else {
			name = this.getInstanceUri();
		}

		JsonArrayBuilder locationsBuilder = Json.createArrayBuilder();
		for (String location : this.getLocations()) {
			locationsBuilder.add(location);
		}

		JsonObject topologyBuilder = factory.createObjectBuilder()
				.add("name", name).add("locations", locationsBuilder)
				.add("serviceContainers", serviceContainerBuilder).build();

		return topologyBuilder;
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public List<ServiceContainer> getServiceContainers() {
		return serviceContainers;
	}

	public void setServiceContainers(List<ServiceContainer> serviceContainers) {
		this.serviceContainers = serviceContainers;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public OpenBatonClient getProjectClient() {
		return projectClient;
	}

	public void setProjectClient(OpenBatonClient projectClient) {
		this.projectClient = projectClient;
	}

	public String getExperimenterName() {
		return experimenterName;
	}

	public void setExperimenterName(String experimenterName) {
		this.experimenterName = experimenterName;
	}
}
