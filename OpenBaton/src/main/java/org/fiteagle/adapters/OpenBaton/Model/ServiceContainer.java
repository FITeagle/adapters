package org.fiteagle.adapters.OpenBaton.Model;

import java.math.BigInteger;
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
import javax.json.JsonObjectBuilder;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.OpenBaton;
import info.openmultinet.ontology.vocabulary.Osco;

public class ServiceContainer extends OpenBatonGeneric {
	private static final Logger LOGGER = Logger
			.getLogger(ServiceContainer.class.toString());

	private String containerName;
	private String version;


	private String flavour;
	private int maxNumInst;
	private int minNumInst;
	private Image image;
	private List<String> datacenters;
	private List<Subnet> subnets;
	private List<OpenBatonGeneric> services;
	private String orchestrator;
	private Topology topology;

	public ServiceContainer(final OpenBatonAdapter owningAdapter,
			final String instanceName) {

		super(owningAdapter, instanceName);

		this.containerName = null;
		this.flavour = null;
		this.version = "0.1";
		this.maxNumInst = -1;
		this.minNumInst = -1;
		this.image = null;
		this.subnets = new ArrayList<Subnet>();
		this.services = new ArrayList<OpenBatonGeneric>();
		this.datacenters = new ArrayList<String>();
		this.topology = null;

		this.setOrchestrator(this.getOwningAdapter().parseConfig(
				this.getOwningAdapter().getAdapterABox(), "orchestrator"));
	}

	@Override
	public void updateInstance(JsonObject jsonObject) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update from JsonObject for "
					+ this.getOwningAdapter().getInstanceUri(this));
		}

		super.updateInstance(jsonObject);
		JsonObject subnets = jsonObject.getJsonObject("subnets");

		for (String key : subnets.keySet()) {

			JsonObject datacenter = subnets.getJsonObject(key);
			JsonArray subnets2 = datacenter.getJsonArray("subnets");

			for (int i = 0; i < subnets2.size(); i++) {

				JsonObject subnet = subnets2.getJsonObject(i);
				String name = subnet.getString("name");

				for (Subnet sn : this.getSubnets()) {
					if (sn.getName() != null && sn.getName().equals(name)) {
						sn.updateInstance(subnet);
					}
				}
			}
		}
	}

	public void updateInstance(Resource deployedOn) {

		super.updateInstance(deployedOn);

		if (deployedOn.hasProperty(Osco.flavour)) {
			String flavour = deployedOn.getProperty(Osco.flavour).getObject()
					.asLiteral().getString();
			this.setFlavour(flavour);
		}

		if (deployedOn.hasProperty(Osco.id)) {
			String name = deployedOn.getProperty(Osco.id).getObject()
					.asLiteral().getString();
			this.setContainerName(name);
		}

		if (deployedOn.hasProperty(Osco.minNumInst)) {
			int minNumInst = deployedOn.getProperty(Osco.minNumInst)
					.getObject().asLiteral().getInt();
			this.setMinNumInst(minNumInst);
		}

		if (deployedOn.hasProperty(Osco.maxNumInst)) {
			int maxNumInst = deployedOn.getProperty(Osco.maxNumInst)
					.getObject().asLiteral().getInt();
			this.setMaxNumInst(maxNumInst);
		}

		StmtIterator subnets = deployedOn.listProperties(Osco.subnet);
		while (subnets.hasNext()) {
			Statement subnetStatement = subnets.next();
			Resource subnetResource = subnetStatement.getObject().asResource();
			Subnet subnet = new Subnet(this.getOwningAdapter(),
					subnetResource.getURI());
			// subnet.setInstanceUri(subnetResource.getURI());
			subnet.updateInstance(subnetResource);
			this.addSubnet(subnet);

			// check if datacenter already in list, if not add it
			if (!this.getDatacenters().contains(subnet.getDatacenter())) {
				this.addDatacenter(subnet.getDatacenter());
			}
		}

		if (deployedOn.hasProperty(Osco.image)) {

			if (this.getImage() != null) {

				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.log(Level.INFO,
							"ServiceContainer updateInstance image datacenter before: "
									+ this.getImage().getDatacenter());
				}

				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.log(Level.INFO,
							"ServiceContainer updateInstance image id before: "
									+ this.getImage().getId());
				}
			} else {
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.log(Level.INFO, "image was null yo");
				}
			}

			LOGGER.log(Level.INFO,
					"ServiceContainer updateInstance: found an image.");
			Resource imageResource = deployedOn.getProperty(Osco.image)
					.getObject().asResource();

			String imageResourceUri = imageResource.getURI();

			if (this.getImage() == null) {
				this.setImage(new Image(this.getOwningAdapter(),
						imageResourceUri));
			}

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"ServiceContainer updateInstance: imageResourceUri "
								+ imageResourceUri);
			}
			// image.setInstanceUri(imageResourceUri);

			this.getImage().updateInstance(imageResource);

			// this.setImage(image);

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"ServiceContainer updateInstance image datacenter: "
								+ this.getImage().getDatacenter());
			}

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"ServiceContainer updateInstance image id: "
								+ this.getImage().getId());
			}

			// check if datacenter already in list, if not add it
			if (!this.getDatacenters()
					.contains(this.getImage().getDatacenter())) {
				this.addDatacenter(this.getImage().getDatacenter());
			}
		}
	}

	public void parseToModel(Resource serviceContainerResource) {

		serviceContainerResource.addProperty(RDF.type, Osco.ServiceContainer);
		serviceContainerResource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);

		super.parseToModel(serviceContainerResource);

		if (this.getSubnets() != null) {
			if (this.getSubnets().size() > 0) {
				for (Subnet subnet : subnets) {
					if (subnet != null) {

						String uuidSubnet = null;
						if (subnet.getInstanceUri() == null) {
							uuidSubnet = "urn:uuid:"
									+ UUID.randomUUID().toString();
						} else {
							uuidSubnet = subnet.getInstanceUri();
						}
						Resource subnetResource = serviceContainerResource
								.getModel().createResource(uuidSubnet);
						subnet.parseToModel(subnetResource);
						serviceContainerResource.addProperty(Osco.subnet,
								subnetResource);
					}
				}
			}
		}

		if (this.getImage() != null) {

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "ServiceContainer parseToModel image.");
			}

			String uuidImage = null;

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"ServiceContainer parseToModel this.getImage().getInstanceUri() "
								+ this.getImage().getInstanceUri());
			}

			if (this.getImage().getInstanceUri() == null) {
				uuidImage = "urn:uuid:" + UUID.randomUUID().toString();
			} else {
				uuidImage = this.getImage().getInstanceUri();
			}

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"ServiceContainer parseToModel this.getImage().getDatacenter() "
								+ this.getImage().getDatacenter());
			}
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"ServiceContainer parseToModel this.getImage().getid() "
								+ this.getImage().getId());
			}
			Resource imageResource = serviceContainerResource.getModel()
					.createResource(uuidImage);
			this.getImage().parseToModel(imageResource);
			serviceContainerResource.addProperty(Osco.image, imageResource);
		}

		if (this.getMaxNumInst() != -1) {
			BigInteger maxNumInst = BigInteger.valueOf(this.getMaxNumInst());
			serviceContainerResource.addLiteral(Osco.maxNumInst, maxNumInst);
		}

		if (this.getMinNumInst() != -1) {
			BigInteger minNumInst = BigInteger.valueOf(this.getMinNumInst());
			serviceContainerResource.addLiteral(Osco.minNumInst, minNumInst);
		}

		if (this.getContainerName() != null
				&& !this.getContainerName().equals("")) {
			serviceContainerResource.addProperty(Osco.id,
					this.getContainerName());
		}

		if (this.getFlavour() != null && !this.getFlavour().equals("")) {
			serviceContainerResource.addProperty(Osco.flavour,
					this.getFlavour());
		}

		if (this.getOrchestrator() != null
				&& !this.getOrchestrator().equals("")) {
			serviceContainerResource.addProperty(serviceContainerResource
					.getModel().createProperty(OpenBaton.getURI(), "orchestrator"),
					this.getOrchestrator());
		}
	}

	@Override
	public JsonObject parseToJson() {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO,
					"parseToJson ServiceContainer : " + this.getInstanceUri());
		}

		JsonBuilderFactory factory = Json.createBuilderFactory(null);
		JsonArrayBuilder servicesBuilder = Json.createArrayBuilder();

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO,
					"ServiceContainer parseToJson number of services : "
							+ this.getServices().size());
		}

		for (OpenBatonGeneric service : this.getServices()) {
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"parseToJson service : " + service.getInstanceUri());
			}
			
			
			// TODO re-implement
			
//			if (service instanceof BenchmarkingTool) {
//				servicesBuilder.add(((BenchmarkingTool) service).parseToJson());
//			} else if (service instanceof Control) {
//				servicesBuilder.add(((Control) service).parseToJson());
//			} else if (service instanceof DomainNameSystem) {
//				servicesBuilder.add(((DomainNameSystem) service).parseToJson());
//			} else if (service instanceof Gateway) {
//				servicesBuilder.add(((Gateway) service).parseToJson());
//			} else if (service instanceof HomeSubscriberService) {
//				servicesBuilder.add(((HomeSubscriberService) service)
//						.parseToJson());
//			} else if (service instanceof ENodeB) {
//				servicesBuilder.add(((ENodeB) service).parseToJson());
//			} else if (service instanceof Switch) {
//				servicesBuilder.add(((Switch) service).parseToJson());
//			}
		}

		JsonObjectBuilder datacenterBuilder = Json.createObjectBuilder();

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "length of datacenters : "
					+ this.getDatacenters().size());
		}

		for (String datacenter : this.getDatacenters()) {
			if (datacenter != null) {
				JsonArrayBuilder subnetsArray = Json.createArrayBuilder();

				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.log(Level.INFO,
							"Parse service container to json for datacenter "
									+ datacenter);
				}

				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.log(Level.INFO,
							"Parse service container to json for subnet amount "
									+ this.getSubnets().size());
				}

				for (Subnet subnet : this.getSubnets()) {

					if (subnet != null && subnet.getDatacenter() != null) {
						if (subnet.getDatacenter().equals(datacenter)) {
							JsonObjectBuilder subnetValues = factory
									.createObjectBuilder().add("name",
											subnet.getName());

							if (subnet.getMgmt() != null) {
								subnetValues.add("mgmt", true);
							}

							subnetsArray.add(subnetValues);
						}
					}
				}
				JsonObjectBuilder subnetArray = factory.createObjectBuilder()
						.add("subnets", subnetsArray);
				datacenterBuilder.add(datacenter, subnetArray);
			}
		}

		String flavour = this.getFlavour();
		if (flavour == null) {
			flavour = "";
		}

		String containerName = this.getContainerName();
		if (containerName == null) {
			containerName = "";
		}

		JsonObjectBuilder imagesBuilder = Json.createObjectBuilder();
		if (this.getImage() != null) {
			String datacenterString = null;

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"Parse service container to json image id: "
								+ this.getImage().getDatacenter());
			}

			if (this.getImage().getDatacenter() == null) {
				datacenterString = "NULL";
			} else {
				datacenterString = this.getImage().getDatacenter();
			}

			String idString = null;

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO,
						"Parse service container to json image id: "
								+ this.getImage().getId());
			}

			if (this.getImage().getId() == null) {
				idString = "NULL";
			} else {
				idString = this.getImage().getId();
			}

			imagesBuilder.add(datacenterString, idString);
		}

		JsonObject serviceContainer = factory.createObjectBuilder()
				.add("flavour", flavour)
				.add("minNumInst", this.getMinNumInst())
				.add("maxNumInst", this.getMaxNumInst())
				.add("images", imagesBuilder).add("subnets", datacenterBuilder)
				.add("containerName", containerName)
				.add("services", servicesBuilder).build();

		return serviceContainer;
	}

	// public String sendStartRequest() throws Exception {
	// String body = "*********sent the folloing START request to server: "
	// + this.parseToJson();
	//
	// HttpMethods.sendJsonPost(this.getOrchestrator(), body);
	//
	// return body;
	// }

	/**
	 * Getters and setters
	 */
	public String getContainerName() {
		return this.containerName;
	}

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public Image getImage() {
		return this.image;
	}

	public int getMinNumInst() {
		return minNumInst;
	}

	public int getMaxNumInst() {
		return maxNumInst;
	}

	public String getFlavour() {
		return flavour;
	}

	public void setFlavour(String flavour) {
		this.flavour = flavour;
	}

	public void setMaxNumInst(int maxNumInst) {
		this.maxNumInst = maxNumInst;
	}

	public void setMinNumInst(int minNumInst) {
		this.minNumInst = minNumInst;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setSubnets(List<Subnet> subnets) {
		this.subnets = subnets;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public List<OpenBatonGeneric> getServices() {
		return services;
	}

	public void setServices(List<OpenBatonGeneric> services) {
		this.services = services;
	}

	public void addService(OpenBatonGeneric fiveG) {
		this.services.add(fiveG);
	}

	public List<String> getDatacenters() {
		return datacenters;
	}

	public void setDatacenters(List<String> datacenters) {
		this.datacenters = datacenters;
	}

	private void addDatacenter(String datacenter) {
		this.datacenters.add(datacenter);
	}

	private void addSubnet(Subnet subnet) {
		this.subnets.add(subnet);
	}

	public String getOrchestrator() {
		return orchestrator;
	}

	public void setOrchestrator(String orchestrator) {
		this.orchestrator = orchestrator;
	}

	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
