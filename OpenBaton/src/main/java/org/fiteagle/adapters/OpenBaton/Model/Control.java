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

import info.openmultinet.ontology.vocabulary.OpenBaton;

public class Control extends OpenBatonService{


		private int mmeConsolePort;
		private Boolean init;
		private String pgwCOfpCtrTransport;
		private int sgwCOfpCtrPort;
		private int sgwCJsonSrvPort;
		private int pgwCConsolePort;
		private int pgwCOfpCtrPort;
		private String mmeHostName;
		private String pgwCTemplateConfigFile;
		private Boolean upstartOn;
		private int mgmtIntf;
		private int dnsIntf;
		private int minNumIntf;
		private int netDIntf;

		private static final Logger LOGGER = Logger.getLogger(Control.class
				.toString());

		public Control(final OpenBatonAdapter owningAdapter, final String instanceName) {

			super(owningAdapter, instanceName);

			this.mmeConsolePort = -1;
			this.init = null;
			this.pgwCOfpCtrTransport = null;
			this.sgwCOfpCtrPort = -1;
			this.sgwCJsonSrvPort = -1;
			this.pgwCConsolePort = -1;
			this.pgwCOfpCtrPort = -1;
			this.mmeHostName = null;
			this.pgwCTemplateConfigFile = null;
			this.upstartOn = null;
			this.mgmtIntf = -1;
			this.minNumIntf = -1;
			this.dnsIntf = -1;
			this.netDIntf = -1;
		}

		@Override
		public void updateInstance(Resource fivegResource) {

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "Update gateway instance: "
						+ this.getOwningAdapter().getInstanceUri(this));
			}

			super.updateInstance(fivegResource);

//			if (fivegResource.hasProperty(OpenBaton.mmeConsolePort)) {
//				int mmeConsolePort = fivegResource
//						.getProperty(OpenBaton.mmeConsolePort).getObject().asLiteral()
//						.getInt();
//				this.setMmeConsolePort(mmeConsolePort);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.init)) {
//				Boolean init = fivegResource.getProperty(OpenBaton.init).getObject()
//						.asLiteral().getBoolean();
//				this.setInit(init);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.pgwCOfpCtrTransport)) {
//				String pgwCOfpCtrTransport = fivegResource
//						.getProperty(OpenBaton.pgwCOfpCtrTransport).getObject()
//						.asLiteral().getString();
//				this.setPgwCOfpCtrTransport(pgwCOfpCtrTransport);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.sgwCOfpCtrPort)) {
//				int sgwCOfpCtrPort = fivegResource
//						.getProperty(OpenBaton.sgwCOfpCtrPort).getObject().asLiteral()
//						.getInt();
//				this.setSgwCOfpCtrPort(sgwCOfpCtrPort);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.sgwCJsonSrvPort)) {
//				int sgwCJsonSrvPort = fivegResource
//						.getProperty(OpenBaton.sgwCJsonSrvPort).getObject().asLiteral()
//						.getInt();
//				this.setSgwCJsonSrvPort(sgwCJsonSrvPort);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.pgwCConsolePort)) {
//				int pgwCConsolePort = fivegResource
//						.getProperty(OpenBaton.pgwCConsolePort).getObject().asLiteral()
//						.getInt();
//				this.setPgwCConsolePort(pgwCConsolePort);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.pgwCOfpCtrPort)) {
//				int pgwCOfpCtrPort = fivegResource
//						.getProperty(OpenBaton.pgwCOfpCtrPort).getObject().asLiteral()
//						.getInt();
//				this.setPgwCOfpCtrPort(pgwCOfpCtrPort);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.mmeHostName)) {
//				String mmeHostName = fivegResource.getProperty(OpenBaton.mmeHostName)
//						.getObject().asLiteral().getString();
//				this.setMmeHostName(mmeHostName);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.pgwCTemplateConfigFile)) {
//				String pgwCTemplateConfigFile = fivegResource
//						.getProperty(OpenBaton.pgwCTemplateConfigFile).getObject()
//						.asLiteral().getString();
//				this.setPgwCTemplateConfigFile(pgwCTemplateConfigFile);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.upstartOn)) {
//				this.setUpstartOn(fivegResource.getProperty(OpenBaton.upstartOn)
//						.getObject().asLiteral().getBoolean());
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.managementInterface)) {
//				int mgmtIntf = fivegResource.getProperty(OpenBaton.managementInterface)
//						.getObject().asLiteral().getInt();
//
//				this.setMgmtIntf(mgmtIntf);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.dnsInterface)) {
//				int dnsInterface = fivegResource.getProperty(OpenBaton.dnsInterface)
//						.getObject().asLiteral().getInt();
//
//				this.setDnsIntf(dnsInterface);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.minInterfaces)) {
//				int minNumIntf = fivegResource.getProperty(OpenBaton.minInterfaces)
//						.getObject().asLiteral().getInt();
//
//				this.setMinNumIntf(minNumIntf);
//			}
//
//			if (fivegResource.hasProperty(OpenBaton.ranBackhaul)) {
//				int netDIntf = fivegResource.getProperty(OpenBaton.ranBackhaul)
//						.getObject().asLiteral().getInt();
//				this.setNetDIntf(netDIntf);
//			}
		}

		@Override
		public void parseToModel(Resource resource) {
			resource.addProperty(RDF.type, OpenBaton.Control);
			resource.addProperty(RDF.type,
					info.openmultinet.ontology.vocabulary.Omn.Resource);

			super.parseToModel(resource);

//			if (this.getMmeConsolePort() != -1) {
//				BigInteger mmeConsolePort = BigInteger.valueOf(this
//						.getMmeConsolePort());
//				resource.addLiteral(OpenBaton.mmeConsolePort, mmeConsolePort);
//			}
//
//			if (this.getInit() != null) {
//				resource.addLiteral(OpenBaton.init, this.getInit());
//			}
//
//			if (this.getPgwCOfpCtrTransport() != null
//					&& !this.getPgwCOfpCtrTransport().equals("")) {
//				String pgwCOfpCtrTransport = this.getPgwCOfpCtrTransport();
//				resource.addLiteral(OpenBaton.pgwCOfpCtrTransport, pgwCOfpCtrTransport);
//			}
//
//			if (this.getSgwCOfpCtrPort() != -1) {
//				BigInteger sgwCOfpCtrPort = BigInteger.valueOf(this
//						.getSgwCOfpCtrPort());
//				resource.addLiteral(OpenBaton.sgwCOfpCtrPort, sgwCOfpCtrPort);
//			}
//
//			if (this.getSgwCJsonSrvPort() != -1) {
//				BigInteger sgwCJsonSrvPort = BigInteger.valueOf(this
//						.getSgwCJsonSrvPort());
//				resource.addLiteral(OpenBaton.sgwCJsonSrvPort, sgwCJsonSrvPort);
//			}
//
//			if (this.getPgwCConsolePort() != -1) {
//				BigInteger pgwCConsolePort = BigInteger.valueOf(this
//						.getPgwCConsolePort());
//				resource.addLiteral(OpenBaton.pgwCConsolePort, pgwCConsolePort);
//			}
//
//			if (this.getPgwCOfpCtrPort() != -1) {
//				BigInteger pgwCOfpCtrPort = BigInteger.valueOf(this
//						.getPgwCOfpCtrPort());
//				resource.addLiteral(OpenBaton.pgwCOfpCtrPort, pgwCOfpCtrPort);
//			}
//
//			if (this.getMmeHostName() != null && !this.getMmeHostName().equals("")) {
//				String mmeHostName = this.getMmeHostName();
//				resource.addLiteral(OpenBaton.mmeHostName, mmeHostName);
//			}
//
//			if (this.getPgwCTemplateConfigFile() != null
//					&& !this.getPgwCTemplateConfigFile().equals("")) {
//				String pgwCTemplateConfigFile = this.getPgwCTemplateConfigFile();
//				resource.addLiteral(OpenBaton.pgwCTemplateConfigFile,
//						pgwCTemplateConfigFile);
//			}
//
//			if (this.isUpstartOn() != null) {
//				resource.addLiteral(OpenBaton.upstartOn, this.isUpstartOn());
//			}
//
//			if (this.getMgmtIntf() != -1) {
//				BigInteger mgmtIntf = BigInteger.valueOf(this.getMgmtIntf());
//				resource.addLiteral(OpenBaton.managementInterface, mgmtIntf);
//			}
//
//			if (this.getDnsIntf() != -1) {
//				BigInteger dnsIntf = BigInteger.valueOf(this.getDnsIntf());
//				resource.addLiteral(OpenBaton.dnsInterface, dnsIntf);
//			}
//
//			if (this.getMinNumIntf() != -1) {
//				BigInteger minNumIntf = BigInteger.valueOf(this.getMinNumIntf());
//				resource.addLiteral(OpenBaton.minInterfaces, minNumIntf);
//			}
//
//			if (this.getNetDIntf() != -1) {
//				BigInteger netDIntf = BigInteger.valueOf(this.getNetDIntf());
//				resource.addLiteral(OpenBaton.ranBackhaul, netDIntf);
//			}
		}

		@Override
		public JsonObject parseToJson() {

			JsonBuilderFactory factory = Json.createBuilderFactory(null);

			JsonArrayBuilder requiresBuilder = Json.createArrayBuilder();
			for (String require : this.getRequires()) {
				requiresBuilder.add(require);
			}

			String mmeConsolePortString = "";
			if (this.getMmeConsolePort() != -1) {
				mmeConsolePortString = Integer.toString(this.getMmeConsolePort());
			}

			String initString = "";
			if (this.getInit() != null) {
				initString = Boolean.toString(this.getInit());
			}

			String pgwCOfpCtrTransport = "";
			if (this.getPgwCOfpCtrTransport() != null
					&& !this.getPgwCOfpCtrTransport().equals("")) {
				pgwCOfpCtrTransport = this.getPgwCOfpCtrTransport();
			}

			String sgwCOfpCtrPortString = "";
			if (this.getSgwCOfpCtrPort() != -1) {
				sgwCOfpCtrPortString = Integer.toString(this.getSgwCOfpCtrPort());
			}

			String sgwCJsonSrvPortString = "";
			if (this.getSgwCJsonSrvPort() != -1) {
				sgwCJsonSrvPortString = Integer.toString(this.getSgwCJsonSrvPort());
			}

			String pgwCConsolePortString = "";
			if (this.getPgwCConsolePort() != -1) {
				pgwCConsolePortString = Integer.toString(this.getPgwCConsolePort());
			}

			String pgwCOfpCtrPortString = "";
			if (this.getPgwCOfpCtrPort() != -1) {
				pgwCOfpCtrPortString = Integer.toString(this.getPgwCOfpCtrPort());

			}

			String mmeHostName = "";
			if (this.getMmeHostName() != null && !this.getMmeHostName().equals("")) {
				mmeHostName = this.getMmeHostName();
			}

			String pgwCTemplateConfigFile = "";
			if (this.getPgwCTemplateConfigFile() != null
					&& !this.getPgwCTemplateConfigFile().equals("")) {
				pgwCTemplateConfigFile = this.getPgwCTemplateConfigFile();
			}

			String upstartOnString = "";
			if (this.isUpstartOn() != null) {
				upstartOnString = Boolean.toString(this.isUpstartOn());
			}

			String mgmtIntfString = "";
			if (this.getMgmtIntf() != -1) {
				mgmtIntfString = Integer.toString(this.getMgmtIntf());
			}

			String dnsIntfString = "";
			if (this.getDnsIntf() != -1) {
				dnsIntfString = Integer.toString(this.getDnsIntf());
			}

			String minNumIntfString = "";
			if (this.getMinNumIntf() != -1) {
				minNumIntfString = Integer.toString(this.getMinNumIntf());
			}

			String netDIntfString = "";
			if (this.getNetDIntf() != -1) {
				netDIntfString = Integer.toString(this.getNetDIntf());
			}

			JsonArrayBuilder parameters = factory
					.createArrayBuilder()
					.add(factory.createObjectBuilder()
							.add("config_key", "MME_CONSOLE_PORT")
							.add("config_value", mmeConsolePortString))
					.add(factory.createObjectBuilder().add("config_key", "INIT")
							.add("config_value", initString))
					.add(factory.createObjectBuilder()
							.add("config_key", "PGW_C_OFP_CTR_TRANSPORT")
							.add("config_value", pgwCOfpCtrTransport))
					.add(factory.createObjectBuilder()
							.add("config_key", "SGW_C_OFP_CTR_PORT")
							.add("config_value", sgwCOfpCtrPortString))
					.add(factory.createObjectBuilder()
							.add("config_key", "SGW_C_JSON_SRV_PORT")
							.add("config_value", sgwCJsonSrvPortString))
					.add(factory.createObjectBuilder()
							.add("config_key", "PGW_C_CONSOLE_PORT")
							.add("config_value", pgwCConsolePortString))
					.add(factory.createObjectBuilder()
							.add("config_key", "PGW_C_OFP_CTR_PORT")
							.add("config_value", pgwCOfpCtrPortString))
					.add(factory.createObjectBuilder()
							.add("config_key", "MME_HOST_NAME")
							.add("config_value", mmeHostName))
					.add(factory.createObjectBuilder()
							.add("config_key", "PGW_C_template_config_file")
							.add("config_value", pgwCTemplateConfigFile))
					.add(factory.createObjectBuilder()
							.add("config_key", "UPSTART_ON")
							.add("config_value", upstartOnString))
					.add(factory.createObjectBuilder()
							.add("config_key", "MGMT_INTF")
							.add("config_value", mgmtIntfString))
					.add(factory.createObjectBuilder()
							.add("config_key", "DNS_INTF")
							.add("config_value", dnsIntfString))
					.add(factory.createObjectBuilder()
							.add("config_key", "MIN_NUM_INTF")
							.add("config_value", minNumIntfString))
					.add(factory.createObjectBuilder()
							.add("config_key", "NET_D_INTF")
							.add("config_value", netDIntfString));

			JsonObject configuration = factory
					.createObjectBuilder()
					.add("configuration",
							factory.createObjectBuilder().add("parameters",
									parameters))
					.add("serviceType", "mme-sgw_c-pgw_c-5G")
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

		public int getMinNumIntf() {
			return minNumIntf;
		}

		public void setMinNumIntf(int minNumIntf) {
			this.minNumIntf = minNumIntf;
		}

		public int getNetDIntf() {
			return netDIntf;
		}

		public void setNetDIntf(int netDIntf) {
			this.netDIntf = netDIntf;
		}

		public int getMmeConsolePort() {
			return mmeConsolePort;
		}

		public void setMmeConsolePort(int mmeConsolePort) {
			this.mmeConsolePort = mmeConsolePort;
		}

		public Boolean getInit() {
			return init;
		}

		public void setInit(Boolean init) {
			this.init = init;
		}

		public String getPgwCOfpCtrTransport() {
			return pgwCOfpCtrTransport;
		}

		public void setPgwCOfpCtrTransport(String pgwCOfpCtrTransport) {
			this.pgwCOfpCtrTransport = pgwCOfpCtrTransport;
		}

		public int getSgwCOfpCtrPort() {
			return sgwCOfpCtrPort;
		}

		public void setSgwCOfpCtrPort(int sgwCOfpCtrPort) {
			this.sgwCOfpCtrPort = sgwCOfpCtrPort;
		}

		public int getSgwCJsonSrvPort() {
			return sgwCJsonSrvPort;
		}

		public void setSgwCJsonSrvPort(int sgwCJsonSrvPort) {
			this.sgwCJsonSrvPort = sgwCJsonSrvPort;
		}

		public int getPgwCConsolePort() {
			return pgwCConsolePort;
		}

		public void setPgwCConsolePort(int pgwCConsolePort) {
			this.pgwCConsolePort = pgwCConsolePort;
		}

		public int getPgwCOfpCtrPort() {
			return pgwCOfpCtrPort;
		}

		public void setPgwCOfpCtrPort(int pgwCOfpCtrPort) {
			this.pgwCOfpCtrPort = pgwCOfpCtrPort;
		}

		public String getMmeHostName() {
			return mmeHostName;
		}

		public void setMmeHostName(String mmeHostName) {
			this.mmeHostName = mmeHostName;
		}

		public String getPgwCTemplateConfigFile() {
			return pgwCTemplateConfigFile;
		}

		public void setPgwCTemplateConfigFile(String pgwCTemplateConfigFile) {
			this.pgwCTemplateConfigFile = pgwCTemplateConfigFile;
		}

		public int getDnsIntf() {
			return dnsIntf;
		}

		public void setDnsIntf(int dnsIntf) {
			this.dnsIntf = dnsIntf;
		}
	
}
