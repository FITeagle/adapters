package org.fiteagle.adapters.OpenBaton.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.OpenBaton;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Osco;

public class OpenBatonService extends OpenBatonGeneric{

	private static final Logger LOGGER = Logger.getLogger(OpenBatonService.class
			.toString());

	private ServiceContainer serviceContainer;
	private String name;
	private List<String> requires;

	// private String label;

	/**
	 * Constructor method
	 * 
	 * @param owningAdapter
	 * @param instanceUri
	 */
	public OpenBatonService(final OpenBatonAdapter owningAdapter,
			final String instanceUri) {

		super(owningAdapter, instanceUri);

		this.serviceContainer = null;
		this.name = null;
		this.requires = new ArrayList<String>();
	}

	/**
	 * Methods to be overridden/expanded in subclasses
	 * 
	 * @param fivegResource
	 */
	public void updateInstance(Resource fivegResource) {

		this.addToServiceContainer(fivegResource);

		if (fivegResource.hasProperty(OpenBaton.requires)) {

			String requireString = fivegResource.getProperty(OpenBaton.requires)
					.getObject().asLiteral().getString();

			String[] parts = requireString.split(",");
			for (String string : parts) {
				String requireStringTrimmed = string.trim();
				// check if not already in list
				if (!this.getRequires().contains(requireStringTrimmed)) {
					this.addRequire(requireStringTrimmed);
				}
			}
		}

		if (fivegResource.hasProperty(RDFS.label)) {
			String name = fivegResource.getProperty(RDFS.label).getObject()
					.asLiteral().getString();
			this.setName(name);
		}
	}

	public void parseToModel(Resource resource) {

		if (this.getServiceContainer() != null) {
			String serviceContainerUri = this.getServiceContainer()
					.getInstanceUri();
			Resource serviceContainerResource = resource.getModel()
					.createResource(serviceContainerUri);
			resource.addProperty(Osco.deployedOn, serviceContainerResource);

			// add topology
			if (this.serviceContainer.getTopology() != null) {
				String topologyUri = this.serviceContainer.getTopology()
						.getInstanceUri();
				Resource topologyResource = resource.getModel().createResource(
						topologyUri);
				resource.addProperty(Omn.isResourceOf, topologyResource);
			}
		}

		if (this.getName() != null) {
			resource.addLiteral(RDFS.label, this.getName());
		}

		if (this.getRequires() != null && !this.getRequires().isEmpty()) {
			String requiresString = "";
			for (int i = 0; i < this.getRequires().size(); i++) {
				requiresString += this.getRequires().get(i);
				if (i < this.getRequires().size() - 1) {
					requiresString += ",";
				}
			}
			resource.addProperty(OpenBaton.requires, requiresString);
		}
	}

	/**
	 * Method to add a service to the the list of services in the service
	 * container
	 * 
	 * @param fivegResource
	 */
	public void addToServiceContainer(Resource fivegResource) {

		LOGGER.warning("FiveGService addToServiceContainer");
		if (this.getServiceContainer() != null) {
			LOGGER.warning("FiveGService addToServiceContainer this.getServiceContainer() != null");
			if (this.getServiceContainer().getServices().contains(this)) {
				LOGGER.warning("FiveGService addToServiceContainer SC contains service");
				return;
			} else {
				LOGGER.warning("FiveGService addToServiceContainer SC doesn't contain service");
				this.getServiceContainer().addService(this);
			}
		} else {
			LOGGER.warning("FiveGService addToServiceContainer this.getServiceContainer() == null");
			// check that the resource belongs to a service container
			if (fivegResource.hasProperty(Osco.deployedOn)) {

				// get the topology on which it is deployed
				Resource deployedOn = fivegResource
						.getProperty(Osco.deployedOn).getObject().asResource();

				ServiceContainer serviceContainer = null;
				// check if service container already created
				for (Map.Entry<String, OpenBatonGeneric> entry : this
						.getOwningAdapter().getInstanceList().entrySet()) {
					String key = entry.getKey();
					OpenBatonGeneric value = entry.getValue();
					if (deployedOn.getURI().equals(key)) {
						if (LOGGER.isLoggable(Level.WARNING)) {
							LOGGER.log(
									Level.WARNING,
									"Instance already exists: "
											+ deployedOn.getURI());
							serviceContainer = (ServiceContainer) value;
						}
					}
				}

				// if service container does not exist, create it
				if (serviceContainer == null) {
					serviceContainer = new ServiceContainer(
							this.getOwningAdapter(), deployedOn.getURI());
					serviceContainer.updateInstance(deployedOn);
					this.getOwningAdapter().getInstanceList()
							.put(deployedOn.getURI(), serviceContainer);

					if (fivegResource.hasProperty(Omn.isResourceOf)) {
						String topologyUri = fivegResource
								.getProperty(Omn.isResourceOf).getObject()
								.asResource().getURI();
						Topology topology = this.getOwningAdapter()
								.getTopologyObject(topologyUri);
						if (topology != null) {
							topology.getServiceContainers().add(
									serviceContainer);
						}
					}

				}

				// add this service to the service container
				serviceContainer.addService(this);
				this.setServiceContainer(serviceContainer);
			}
		}
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public ServiceContainer getServiceContainer() {
		return serviceContainer;
	}

	public void setServiceContainer(ServiceContainer serviceContainer) {
		this.serviceContainer = serviceContainer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addRequire(String requireString) {
		this.requires.add(requireString);
	}

	public List<String> getRequires() {
		return this.requires;
	}

}
