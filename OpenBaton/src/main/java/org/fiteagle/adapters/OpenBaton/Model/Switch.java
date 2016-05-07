package org.fiteagle.adapters.OpenBaton.Model;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Fiveg;

public class Switch extends OpenBatonService{
	private int mgmtIntf;
	private int netAIntf;
	private Boolean upstartOn;
	private String pgwUBaseId;
	private int pgwUDownloadInterface;
	private int pgwUUploadInterface;
	private int minNumIntf;
	private int sgwUDownloadInterface;
	private int sgwUUploadInterface;
	private int netDIntf;

	// private String name;
	// private List<String> requires;
	// private ServiceContainer serviceContainer;

	private static final Logger LOGGER = Logger.getLogger(Switch.class
			.toString());

	public Switch(final OpenBatonAdapter owningAdapter, final String instanceName) {

		super(owningAdapter, instanceName);

		this.minNumIntf = -1;
		this.mgmtIntf = -1;
		this.netAIntf = -1;
		this.upstartOn = null;
		this.pgwUBaseId = null;
		this.pgwUUploadInterface = -1;
		this.pgwUDownloadInterface = -1;
		this.sgwUUploadInterface = -1;
		this.sgwUDownloadInterface = -1;
		this.netDIntf = -1;
	}

	@Override
	public void updateInstance(Resource fivegResource) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update gateway instance: "
					+ this.getOwningAdapter().getInstanceUri(this));
		}

		super.updateInstance(fivegResource);

		if (fivegResource.hasProperty(Fiveg.upstartOn)) {
			Boolean upstartOn = fivegResource.getProperty(Fiveg.upstartOn)
					.getObject().asLiteral().getBoolean();
			this.setUpstartOn(upstartOn);
		}

		if (fivegResource.hasProperty(Fiveg.pgwUBaseId)) {
			String pgwUBaseId = fivegResource.getProperty(Fiveg.pgwUBaseId)
					.getObject().asLiteral().getString();
			this.setPgwUBaseId(pgwUBaseId);
		}

		if (fivegResource.hasProperty(Fiveg.ranBackhaul)) {
			int netDIntf = fivegResource.getProperty(Fiveg.ranBackhaul)
					.getObject().asLiteral().getInt();
			this.setNetDIntf(netDIntf);
		}

		if (fivegResource.hasProperty(Fiveg.pgwUDownloadInterface)) {
			int pgwUDownloadInterface = fivegResource
					.getProperty(Fiveg.pgwUDownloadInterface).getObject()
					.asLiteral().getInt();
			this.setPgwUDownloadInterface(pgwUDownloadInterface);
		}

		if (fivegResource.hasProperty(Fiveg.pgwUUploadInterface)) {
			int pgwUUploadInterface = fivegResource
					.getProperty(Fiveg.pgwUUploadInterface).getObject()
					.asLiteral().getInt();
			this.setPgwUUploadInterface(pgwUUploadInterface);
		}

		if (fivegResource.hasProperty(Fiveg.sgwUDownloadInterface)) {
			int sgwUDownloadInterface = fivegResource
					.getProperty(Fiveg.sgwUDownloadInterface).getObject()
					.asLiteral().getInt();
			this.setSgwUDownloadInterface(sgwUDownloadInterface);
		}

		if (fivegResource.hasProperty(Fiveg.sgwUUploadInterface)) {
			int sgwUUploadInterface = fivegResource
					.getProperty(Fiveg.sgwUUploadInterface).getObject()
					.asLiteral().getInt();
			this.setSgwUUploadInterface(sgwUUploadInterface);
		}

		if (fivegResource.hasProperty(Fiveg.ipServicesNetwork)) {
			int netAIntf = fivegResource.getProperty(Fiveg.ipServicesNetwork)
					.getObject().asLiteral().getInt();

			this.setNetAIntf(netAIntf);
		}

		if (fivegResource.hasProperty(Fiveg.managementInterface)) {
			int mgmtIntf = fivegResource.getProperty(Fiveg.managementInterface)
					.getObject().asLiteral().getInt();

			this.setMgmtIntf(mgmtIntf);
		}

		if (fivegResource.hasProperty(Fiveg.minInterfaces)) {
			int minNumIntf = fivegResource.getProperty(Fiveg.minInterfaces)
					.getObject().asLiteral().getInt();

			this.setMinNumIntf(minNumIntf);
		}
	}

	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type,
				Fiveg.Switch);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);

		super.parseToModel(resource);

		if (this.isUpstartOn() != null) {
			resource.addLiteral(Fiveg.upstartOn, this.isUpstartOn());
		}

		if (this.getPgwUBaseId() != null && !this.getPgwUBaseId().equals("")) {
			resource.addLiteral(Fiveg.pgwUBaseId, this.getPgwUBaseId());
		}

		if (this.getMgmtIntf() != -1) {
			BigInteger mgmtIntf = BigInteger.valueOf(this.getMgmtIntf());
			resource.addLiteral(Fiveg.managementInterface, mgmtIntf);
		}

		if (this.getMinNumIntf() != -1) {
			BigInteger minNumIntf = BigInteger.valueOf(this.getMinNumIntf());
			resource.addLiteral(Fiveg.minInterfaces, minNumIntf);
		}

		if (this.getNetAIntf() != -1) {
			BigInteger netAIntf = BigInteger.valueOf(this.getNetAIntf());
			resource.addLiteral(Fiveg.ipServicesNetwork, netAIntf);
		}

		if (this.getNetDIntf() != -1) {
			BigInteger netDIntf = BigInteger.valueOf(this.getNetDIntf());
			resource.addLiteral(Fiveg.ranBackhaul, netDIntf);
		}

		if (this.getPgwUDownloadInterface() != -1) {
			BigInteger pgwUDownloadIntf = BigInteger.valueOf(this
					.getPgwUDownloadInterface());
			resource.addLiteral(Fiveg.pgwUDownloadInterface, pgwUDownloadIntf);
		}

		if (this.getPgwUUploadInterface() != -1) {
			BigInteger pgwUUploadIntf = BigInteger.valueOf(this
					.getPgwUUploadInterface());
			resource.addLiteral(Fiveg.pgwUUploadInterface, pgwUUploadIntf);
		}

		if (this.getSgwUDownloadInterface() != -1) {
			BigInteger sgwUDownloadIntf = BigInteger.valueOf(this
					.getSgwUDownloadInterface());
			resource.addLiteral(Fiveg.sgwUDownloadInterface, sgwUDownloadIntf);
		}

		if (this.getSgwUUploadInterface() != -1) {
			BigInteger sgwUUploadIntf = BigInteger.valueOf(this
					.getSgwUUploadInterface());
			resource.addLiteral(Fiveg.sgwUUploadInterface, sgwUUploadIntf);
		}
	}

	@Override
	public JsonObject parseToJson() {

		JsonBuilderFactory factory = Json.createBuilderFactory(null);

		JsonArrayBuilder requiresBuilder = Json.createArrayBuilder();
		for (String require : this.getRequires()) {
			requiresBuilder.add(require);
		}

		String upstartOnString = "";
		if (this.isUpstartOn() != null) {
			upstartOnString = Boolean.toString(this.isUpstartOn());
		}

		String mgmtIntfString = "";
		if (this.getMgmtIntf() != -1) {
			mgmtIntfString = Integer.toString(this.getMgmtIntf());
		}

		String minNumIntfString = "";
		if (this.getMinNumIntf() != -1) {
			minNumIntfString = Integer.toString(this.getMinNumIntf());
		}

		String netAIntfString = "";
		if (this.getNetAIntf() != -1) {
			netAIntfString = Integer.toString(this.getNetAIntf());
		}

		String netDIntfString = "";
		if (this.getNetDIntf() != -1) {
			netDIntfString = Integer.toString(this.getNetDIntf());
		}

		JsonArrayBuilder parameters = factory
				.createArrayBuilder()
				.add(factory.createObjectBuilder()
						.add("config_key", "UPSTART_ON")
						.add("config_value", upstartOnString))
				.add(factory.createObjectBuilder()
						.add("config_key", "MIN_NUM_INTF")
						.add("config_value", minNumIntfString))
				.add(factory.createObjectBuilder()
						.add("config_key", "NET_A_INTF")
						.add("config_value", netAIntfString))
				.add(factory.createObjectBuilder()
						.add("config_key", "NET_D_INTF")
						.add("config_value", netDIntfString))
				.add(factory
						.createObjectBuilder()
						.add("config_key", "PGW_U_Download_Interface")
						.add("config_value",
								Integer.toString(this
										.getPgwUDownloadInterface())))
				.add(factory
						.createObjectBuilder()
						.add("config_key", "PGW_U_Upload_Interface")
						.add("config_value",
								Integer.toString(this.getPgwUUploadInterface())))
				.add(factory
						.createObjectBuilder()
						.add("config_key", "SGW_U_Download_Interface")
						.add("config_value",
								Integer.toString(this
										.getSgwUDownloadInterface())))
				.add(factory
						.createObjectBuilder()
						.add("config_key", "SGW_U_Upload_Interface")
						.add("config_value",
								Integer.toString(this.getSgwUUploadInterface())))
				.add(factory.createObjectBuilder()
						.add("config_key", "PGW_U_BASE_ID")
						.add("config_value", this.getPgwUBaseId()))
				.add(factory.createObjectBuilder()
						.add("config_key", "MGMT_INTF")
						.add("config_value", mgmtIntfString));

		JsonObject configuration = factory
				.createObjectBuilder()
				.add("configuration",
						factory.createObjectBuilder().add("parameters",
								parameters))
				.add("serviceType", "pgw_u-sgw_u-5G")
				.add("instanceName", this.getName())
				.add("requires", requiresBuilder).build();

		return configuration;
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public Boolean isUpstartOn() {
		return upstartOn;
	}

	public void setUpstartOn(Boolean upstartOn) {
		this.upstartOn = upstartOn;
	}

	public int getMgmtIntf() {
		return mgmtIntf;
	}

	public void setMgmtIntf(int mgmtIntf) {
		this.mgmtIntf = mgmtIntf;
	}

	public int getNetAIntf() {
		return netAIntf;
	}

	public void setNetAIntf(int netAIntf) {
		this.netAIntf = netAIntf;
	}

	public int getMinNumIntf() {
		return minNumIntf;
	}

	public void setMinNumIntf(int minNumIntf) {
		this.minNumIntf = minNumIntf;
	}

	public String getPgwUBaseId() {
		return pgwUBaseId;
	}

	public void setPgwUBaseId(String pgwUBaseId) {
		this.pgwUBaseId = pgwUBaseId;
	}

	public int getPgwUDownloadInterface() {
		return pgwUDownloadInterface;
	}

	public void setPgwUDownloadInterface(int pgwUDownloadInterface) {
		this.pgwUDownloadInterface = pgwUDownloadInterface;
	}

	public int getPgwUUploadInterface() {
		return pgwUUploadInterface;
	}

	public void setPgwUUploadInterface(int pgwUUploadInterface) {
		this.pgwUUploadInterface = pgwUUploadInterface;
	}

	public int getSgwUDownloadInterface() {
		return sgwUDownloadInterface;
	}

	public void setSgwUDownloadInterface(int sgwUDownloadInterface) {
		this.sgwUDownloadInterface = sgwUDownloadInterface;
	}

	public int getSgwUUploadInterface() {
		return sgwUUploadInterface;
	}

	public void setSgwUUploadInterface(int sgwUUploadInterface) {
		this.sgwUUploadInterface = sgwUUploadInterface;
	}

	public int getNetDIntf() {
		return netDIntf;
	}

	public void setNetDIntf(int netDIntf) {
		this.netDIntf = netDIntf;
	}
}
