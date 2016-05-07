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

public class HomeSubscriberService extends OpenBatonService {

	private int localDb;
	private int dbProvi;
	private String dbUser;
	private String dbPw;
	private String dbName;
	private String domainName;
	private int port;
	private Boolean slfPresence;
	private int consolePortOne;
	private int consolePortTwo;
	private String consolePortBindOne;
	private String consolePortBindTwo;
	private int diameterListenIntf;
	private int mgmtIntf;
	private int dnsIntf;
	private String defaultRouteVia;
	private int minNumIntf;
	private String version;

	// private String name;
	// private List<String> requires;
	// private ServiceContainer serviceContainer;

	private static final Logger LOGGER = Logger
			.getLogger(HomeSubscriberService.class.toString());

	public HomeSubscriberService(final OpenBatonAdapter owningAdapter,
			final String instanceName) {

		super(owningAdapter, instanceName);

		this.localDb = -1;
		this.dbProvi = -1;
		this.dbUser = null;
		this.dbPw = null;
		this.dbName = null;
		this.domainName = null;
		this.port = -1;
		this.slfPresence = null;
		this.consolePortOne = -1;
		this.consolePortTwo = -1;
		this.consolePortBindOne = null;
		this.consolePortBindTwo = null;
		this.diameterListenIntf = -1;
		this.defaultRouteVia = null;
		this.version = null;
		this.mgmtIntf = -1;
		this.minNumIntf = -1;
		this.dnsIntf = -1;
	}

	@Override
	public void updateInstance(Resource fivegResource) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update gateway instance: "
					+ this.getOwningAdapter().getInstanceUri(this));
		}

		super.updateInstance(fivegResource);

		if (fivegResource.hasProperty(Fiveg.localDatabase)) {
			int localDb = fivegResource.getProperty(Fiveg.localDatabase)
					.getObject().asLiteral().getInt();
			this.setLocalDb(localDb);
		}

		if (fivegResource.hasProperty(Fiveg.databaseProvi)) {
			int dbProvi = fivegResource.getProperty(Fiveg.databaseProvi)
					.getObject().asLiteral().getInt();
			this.setDbProvi(dbProvi);
		}

		if (fivegResource.hasProperty(Fiveg.databaseUser)) {
			String dbUser = fivegResource.getProperty(Fiveg.databaseUser)
					.getObject().asLiteral().getString();
			this.setDbUser(dbUser);
		}

		if (fivegResource.hasProperty(Fiveg.databasePassword)) {
			String dbPw = fivegResource.getProperty(Fiveg.databasePassword)
					.getObject().asLiteral().getString();
			this.setDbPw(dbPw);
		}

		if (fivegResource.hasProperty(Fiveg.databaseName)) {
			String dbName = fivegResource.getProperty(Fiveg.databaseName)
					.getObject().asLiteral().getString();
			this.setDbName(dbName);
		}

		if (fivegResource.hasProperty(Fiveg.domainName)) {
			String domainName = fivegResource.getProperty(Fiveg.domainName)
					.getObject().asLiteral().getString();
			this.setDomainName(domainName);
		}

		if (fivegResource.hasProperty(Fiveg.port)) {
			int port = fivegResource.getProperty(Fiveg.port).getObject()
					.asLiteral().getInt();
			this.setPort(port);
		}

		if (fivegResource.hasProperty(Fiveg.slfPresence)) {
			Boolean slfPresence = fivegResource.getProperty(Fiveg.slfPresence)
					.getObject().asLiteral().getBoolean();
			this.setSlfPresence(slfPresence);
		}

		if (fivegResource.hasProperty(Fiveg.consolePortOne)) {
			int consolePortOne = fivegResource
					.getProperty(Fiveg.consolePortOne).getObject().asLiteral()
					.getInt();
			this.setConsolePortOne(consolePortOne);
		}

		if (fivegResource.hasProperty(Fiveg.consolePortTwo)) {
			int consolePortTwo = fivegResource
					.getProperty(Fiveg.consolePortTwo).getObject().asLiteral()
					.getInt();
			this.setConsolePortTwo(consolePortTwo);
		}

		if (fivegResource.hasProperty(Fiveg.consolePortBindOne)) {
			String consolePortBindOne = fivegResource
					.getProperty(Fiveg.consolePortBindOne).getObject()
					.asLiteral().getString();
			this.setConsolePortBindOne(consolePortBindOne);
		}

		if (fivegResource.hasProperty(Fiveg.consolePortBindTwo)) {
			String consolePortBindTwo = fivegResource
					.getProperty(Fiveg.consolePortBindTwo).getObject()
					.asLiteral().getString();
			this.setConsolePortBindTwo(consolePortBindTwo);
		}

		if (fivegResource.hasProperty(Fiveg.diameterListenIntf)) {
			int diameterListenIntf = fivegResource
					.getProperty(Fiveg.diameterListenIntf).getObject()
					.asLiteral().getInt();
			this.setDiameterListenIntf(diameterListenIntf);
		}

		if (fivegResource.hasProperty(Fiveg.defaultRouteVia)) {
			String defaultRouteVia = fivegResource
					.getProperty(Fiveg.defaultRouteVia).getObject().asLiteral()
					.getString();
			this.setDefaultRouteVia(defaultRouteVia);
		}

		if (fivegResource.hasProperty(Fiveg.version)) {
			String version = fivegResource.getProperty(Fiveg.version)
					.getObject().asLiteral().getString();
			this.setVersion(version);
		}

		if (fivegResource.hasProperty(Fiveg.managementInterface)) {
			int mgmtIntf = fivegResource.getProperty(Fiveg.managementInterface)
					.getObject().asLiteral().getInt();
			this.setMgmtIntf(mgmtIntf);
		}

		if (fivegResource.hasProperty(Fiveg.dnsInterface)) {
			int dnsInterface = fivegResource.getProperty(Fiveg.dnsInterface)
					.getObject().asLiteral().getInt();
			this.setDnsIntf(dnsInterface);
		}

		if (fivegResource.hasProperty(Fiveg.minInterfaces)) {
			int minNumIntf = fivegResource.getProperty(Fiveg.minInterfaces)
					.getObject().asLiteral().getInt();
			this.setMinNumIntf(minNumIntf);
		}
	}

	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type, Fiveg.HomeSubscriberServer);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);

		super.parseToModel(resource);

		if (this.getLocalDb() != -1) {
			BigInteger localDb = BigInteger.valueOf(this.getLocalDb());
			resource.addLiteral(Fiveg.localDatabase, localDb);
		}

		if (this.getDbProvi() != -1) {
			BigInteger dbProvi = BigInteger.valueOf(this.getDbProvi());
			resource.addLiteral(Fiveg.databaseProvi, dbProvi);
		}

		if (this.getDbUser() != null && !this.getDbUser().equals("")) {
			resource.addLiteral(Fiveg.databaseUser, this.getDbUser());
		}

		if (this.getDbPw() != null && !this.getDbPw().equals("")) {
			resource.addLiteral(Fiveg.databasePassword, this.getDbPw());
		}

		if (this.getDbName() != null && !this.getDbName().equals("")) {
			resource.addLiteral(Fiveg.databaseName, this.getDbName());
		}

		if (this.getDomainName() != null && !this.getDomainName().equals("")) {
			resource.addLiteral(Fiveg.domainName, this.getDomainName());
		}

		if (this.getPort() != -1) {
			BigInteger port = BigInteger.valueOf(this.getPort());
			resource.addLiteral(Fiveg.port, port);
		}

		if (this.getSlfPresence() != null) {
			resource.addLiteral(Fiveg.slfPresence, this.getSlfPresence());
		}

		if (this.getConsolePortOne() != -1) {
			BigInteger consolePortOne = BigInteger.valueOf(this
					.getConsolePortOne());
			resource.addLiteral(Fiveg.consolePortOne, consolePortOne);
		}

		if (this.getConsolePortTwo() != -1) {
			BigInteger consolePortTwo = BigInteger.valueOf(this
					.getConsolePortTwo());
			resource.addLiteral(Fiveg.consolePortTwo, consolePortTwo);
		}

		if (this.getConsolePortBindOne() != null
				&& !this.getConsolePortBindOne().equals("")) {
			resource.addLiteral(Fiveg.consolePortBindOne,
					this.getConsolePortBindOne());
		}

		if (this.getConsolePortBindTwo() != null
				&& !this.getConsolePortBindTwo().equals("")) {
			resource.addLiteral(Fiveg.consolePortBindTwo,
					this.getConsolePortBindTwo());
		}

		if (this.getDiameterListenIntf() != -1) {
			BigInteger diameterListenIntf = BigInteger.valueOf(this
					.getDiameterListenIntf());
			resource.addLiteral(Fiveg.diameterListenIntf, diameterListenIntf);
		}

		if (this.getDefaultRouteVia() != null
				&& !this.getDefaultRouteVia().equals("")) {
			resource.addLiteral(Fiveg.defaultRouteVia,
					this.getDefaultRouteVia());
		}

		if (this.getVersion() != null && !this.getVersion().equals("")) {
			resource.addLiteral(Fiveg.version, this.getVersion());
		}

		if (this.getMgmtIntf() != -1) {
			BigInteger mgmtIntf = BigInteger.valueOf(this.getMgmtIntf());
			resource.addLiteral(Fiveg.managementInterface, mgmtIntf);
		}

		if (this.getDnsIntf() != -1) {
			BigInteger dnsIntf = BigInteger.valueOf(this.getDnsIntf());
			resource.addLiteral(Fiveg.dnsInterface, dnsIntf);
		}

		if (this.getMinNumIntf() != -1) {
			BigInteger minNumIntf = BigInteger.valueOf(this.getMinNumIntf());
			resource.addLiteral(Fiveg.minInterfaces, minNumIntf);
		}
	}

	@Override
	public JsonObject parseToJson() {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "parseToJson HomeSubscriberService : "
					+ this.getInstanceUri());
		}

		JsonBuilderFactory factory = Json.createBuilderFactory(null);

		JsonArrayBuilder requiresBuilder = Json.createArrayBuilder();
		for (String require : this.getRequires()) {
			requiresBuilder.add(require);
		}

		String localDatabaseString = "";
		if (this.getLocalDb() != -1) {
			localDatabaseString = Integer.toString(this.getLocalDb());
		}

		String databaseProviString = "";
		if (this.getDbProvi() != -1) {
			databaseProviString = Integer.toString(this.getDbProvi());
		}

		String databaseUser = "";
		if (this.getDbUser() != null && !this.getDbUser().equals("")) {
			databaseUser = this.getDbUser();
		}

		String databasePassword = "";
		if (this.getDbPw() != null && !this.getDbPw().equals("")) {
			databasePassword = this.getDbPw();
		}

		String databaseName = "";
		if (this.getDbName() != null && !this.getDbName().equals("")) {
			databaseName = this.getDbName();
		}

		String domainName = "";
		if (this.getDomainName() != null && !this.getDomainName().equals("")) {
			domainName = this.getDomainName();
		}

		String portString = "";
		if (this.getPort() != -1) {
			portString = Integer.toString(this.getPort());
		}

		String slfPresenceString = "";
		if (this.getSlfPresence() != null) {
			slfPresenceString = Boolean.toString(this.getSlfPresence());
		}

		String consolePortOne = "";
		if (this.getConsolePortOne() != -1) {
			consolePortOne = Integer.toString(this.getConsolePortOne());
		}

		String consolePortTwo = "";
		if (this.getConsolePortTwo() != -1) {
			consolePortTwo = Integer.toString(this.getConsolePortTwo());
		}

		String consolePortBindOne = "";
		if (this.getConsolePortBindOne() != null
				&& !this.getConsolePortBindOne().equals("")) {
			consolePortBindOne = this.getConsolePortBindOne();
		}

		String consolePortBindTwo = "";
		if (this.getConsolePortBindTwo() != null
				&& !this.getConsolePortBindTwo().equals("")) {
			consolePortBindTwo = this.getConsolePortBindTwo();
		}

		String diameterListenIntfString = "";
		if (this.getDiameterListenIntf() != -1) {
			diameterListenIntfString = Integer.toString(this
					.getDiameterListenIntf());
		}

		String defaultRouteViaString = "";
		if (this.getDefaultRouteVia() != null
				&& !this.getDefaultRouteVia().equals("")) {
			defaultRouteViaString = this.getDefaultRouteVia();
		}

		String version = "";
		if (this.getVersion() != null && !this.getVersion().equals("")) {
			version = this.getVersion();
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

		JsonArrayBuilder parameters = factory
				.createArrayBuilder()
				.add(factory.createObjectBuilder().add("config_key", "localDB")
						.add("config_value", localDatabaseString))
				.add(factory.createObjectBuilder()
						.add("config_key", "db-provi")
						.add("config_value", databaseProviString))
				.add(factory.createObjectBuilder().add("config_key", "db-user")
						.add("config_value", databaseUser))
				.add(factory.createObjectBuilder().add("config_key", "db-pw")
						.add("config_value", databasePassword))
				.add(factory.createObjectBuilder().add("config_key", "db-name")
						.add("config_value", databaseName))
				.add(factory.createObjectBuilder()
						.add("config_key", "domain-name")
						.add("config_value", domainName))
				.add(factory.createObjectBuilder().add("config_key", "port")
						.add("config_value", portString))
				.add(factory.createObjectBuilder()
						.add("config_key", "SLF_PRESENCE")
						.add("config_value", slfPresenceString))
				.add(factory.createObjectBuilder()
						.add("config_key", "CONSOLE_PORT_ONE")
						.add("config_value", consolePortOne))
				.add(factory.createObjectBuilder()
						.add("config_key", "CONSOLE_PORT_TWO")
						.add("config_value", consolePortTwo))
				.add(factory.createObjectBuilder()
						.add("config_key", "CONSOLE_PORT_BIND_ONE")
						.add("config_value", consolePortBindOne))
				.add(factory.createObjectBuilder()
						.add("config_key", "CONSOLE_PORT_BIND_TWO")
						.add("config_value", consolePortBindTwo))
				.add(factory.createObjectBuilder()
						.add("config_key", "DIAMETER_LISTEN_INTF")
						.add("config_value", diameterListenIntfString))
				.add(factory.createObjectBuilder()
						.add("config_key", "MGMT_INTF")
						.add("config_value", mgmtIntfString))
				.add(factory.createObjectBuilder()
						.add("config_key", "DNS_INTF")
						.add("config_value", dnsIntfString))
				.add(factory.createObjectBuilder()
						.add("config_key", "DEFAULT_ROUTE_VIA")
						.add("config_value", defaultRouteViaString))
				.add(factory.createObjectBuilder()
						.add("config_key", "MIN_NUM_INTF")
						.add("config_value", minNumIntfString))
				.add(factory.createObjectBuilder().add("config_key", "VERSION")
						.add("config_value", version));

		JsonObject configuration = factory
				.createObjectBuilder()
				.add("configuration",
						factory.createObjectBuilder().add("parameters",
								parameters)).add("serviceType", "hss")
				.add("instanceName", this.getName())
				.add("requires", requiresBuilder).build();

		return configuration;
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
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

	public int getDnsIntf() {
		return dnsIntf;
	}

	public void setDnsIntf(int dnsIntf) {
		this.dnsIntf = dnsIntf;
	}

	public int getLocalDb() {
		return localDb;
	}

	public void setLocalDb(int localDb) {
		this.localDb = localDb;
	}

	public int getDbProvi() {
		return dbProvi;
	}

	public void setDbProvi(int dbProvi) {
		this.dbProvi = dbProvi;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPw() {
		return dbPw;
	}

	public void setDbPw(String dbPw) {
		this.dbPw = dbPw;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Boolean getSlfPresence() {
		return slfPresence;
	}

	public void setSlfPresence(Boolean slfPresence) {
		this.slfPresence = slfPresence;
	}

	public int getConsolePortOne() {
		return consolePortOne;
	}

	public void setConsolePortOne(int consolePortOne) {
		this.consolePortOne = consolePortOne;
	}

	public int getConsolePortTwo() {
		return consolePortTwo;
	}

	public void setConsolePortTwo(int consolePortTwo) {
		this.consolePortTwo = consolePortTwo;
	}

	public String getConsolePortBindOne() {
		return consolePortBindOne;
	}

	public void setConsolePortBindOne(String consolePortBindOne) {
		this.consolePortBindOne = consolePortBindOne;
	}

	public String getConsolePortBindTwo() {
		return consolePortBindTwo;
	}

	public void setConsolePortBindTwo(String consolePortBindTwo) {
		this.consolePortBindTwo = consolePortBindTwo;
	}

	public int getDiameterListenIntf() {
		return diameterListenIntf;
	}

	public void setDiameterListenIntf(int diameterListenIntf) {
		this.diameterListenIntf = diameterListenIntf;
	}

	public String getDefaultRouteVia() {
		return defaultRouteVia;
	}

	public void setDefaultRouteVia(String defaultRouteVia) {
		this.defaultRouteVia = defaultRouteVia;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
