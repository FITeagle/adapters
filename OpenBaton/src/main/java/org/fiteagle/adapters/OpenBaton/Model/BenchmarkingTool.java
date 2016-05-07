package org.fiteagle.adapters.OpenBaton.Model;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Fiveg;

public class BenchmarkingTool extends OpenBatonService{
	private int consolePort;
	private String btHostName;
	private Boolean upstartOn;
	private int mgmtIntf;
	private int netCIntf;
	private int netDIntf;
	private int dnsIntf;
	private String netIp;
	private String netMask;
	private String ipRangeStart;
	private String ipRangeEnd;
	private String imsiRangeStart;
	private String imsiRangeEnd;
	private int minNumIntf;
	private String ueAddress;
	private String cloudPublicRouterIp;
	private Boolean useFloatingIps;

	final String CONFIG_KEY = "config_key";
	final String CONFIG_VALUE = "config_value";

	private static final Logger LOGGER = Logger
			.getLogger(BenchmarkingTool.class.toString());

	public BenchmarkingTool(final OpenBatonAdapter owningAdapter,
			final String instanceName) {

		super(owningAdapter, instanceName);

		this.consolePort = -1;
		this.btHostName = null;
		this.upstartOn = null;
		this.mgmtIntf = -1;
		this.netCIntf = -1;
		this.netDIntf = -1;
		this.dnsIntf = -1;
		this.netIp = null;
		this.netMask = null;
		this.ipRangeStart = null;
		this.ipRangeEnd = null;
		this.imsiRangeStart = null;
		this.imsiRangeEnd = null;
		this.minNumIntf = -1;
		this.ueAddress = null;
		this.cloudPublicRouterIp = null;
		this.useFloatingIps = null;

	}

	@Override
	public void updateInstance(Resource fivegResource) {
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update gateway instance: "
					+ this.getOwningAdapter().getInstanceUri(this));
		}
		super.updateInstance(fivegResource);

		if (fivegResource.hasProperty(Fiveg.consolePort)) {
			final int consolePort = fivegResource
					.getProperty(Fiveg.consolePort).getObject().asLiteral()
					.getInt();
			this.setConsolePort(consolePort);
		}

		if (fivegResource.hasProperty(Fiveg.benchmarkingToolHostName)) {
			final String benchmarkingToolHostName = fivegResource
					.getProperty(Fiveg.benchmarkingToolHostName).getObject()
					.asLiteral().getString();
			this.setBtHostName(benchmarkingToolHostName);
		}

		if (fivegResource.hasProperty(Fiveg.upstartOn)) {
			this.setUpstartOn(fivegResource.getProperty(Fiveg.upstartOn)
					.getObject().asLiteral().getBoolean());
		}

		if (fivegResource.hasProperty(Fiveg.managementInterface)) {
			final int mgmtIntf = fivegResource
					.getProperty(Fiveg.managementInterface).getObject()
					.asLiteral().getInt();

			this.setMgmtIntf(mgmtIntf);
		}

		if (fivegResource.hasProperty(Fiveg.subscriberIpRange)) {
			final int netCIntf = fivegResource
					.getProperty(Fiveg.subscriberIpRange).getObject()
					.asLiteral().getInt();
			this.setNetCIntf(netCIntf);
		}

		if (fivegResource.hasProperty(Fiveg.ranBackhaul)) {
			final int netDIntf = fivegResource.getProperty(Fiveg.ranBackhaul)
					.getObject().asLiteral().getInt();
			this.setNetDIntf(netDIntf);
		}

		if (fivegResource.hasProperty(Fiveg.dnsInterface)) {
			final int dnsInterface = fivegResource
					.getProperty(Fiveg.dnsInterface).getObject().asLiteral()
					.getInt();
			this.setDnsIntf(dnsInterface);
		}

		if (fivegResource.hasProperty(Fiveg.netIp)) {
			final String netIp = fivegResource.getProperty(Fiveg.netIp)
					.getObject().asLiteral().getString();
			this.setNetIp(netIp);
		}

		if (fivegResource.hasProperty(Fiveg.netMask)) {
			final String netMask = fivegResource.getProperty(Fiveg.netMask)
					.getObject().asLiteral().getString();
			this.setNetMask(netMask);
		}

		if (fivegResource.hasProperty(Fiveg.ipRangeStart)) {
			final String ipRangeStart = fivegResource
					.getProperty(Fiveg.ipRangeStart).getObject().asLiteral()
					.getString();
			this.setIpRangeStart(ipRangeStart);
		}

		if (fivegResource.hasProperty(Fiveg.ipRangeEnd)) {
			final String ipRangeEnd = fivegResource
					.getProperty(Fiveg.ipRangeEnd).getObject().asLiteral()
					.getString();
			this.setIpRangeEnd(ipRangeEnd);
		}

		if (fivegResource.hasProperty(Fiveg.imsiRangeStart)) {
			final String imsiRangeStart = fivegResource
					.getProperty(Fiveg.imsiRangeStart).getObject().asLiteral()
					.getString();
			this.setImsiRangeStart(imsiRangeStart);
		}

		if (fivegResource.hasProperty(Fiveg.imsiRangeEnd)) {
			final String imsiRangeEnd = fivegResource
					.getProperty(Fiveg.imsiRangeEnd).getObject().asLiteral()
					.getString();
			this.setImsiRangeEnd(imsiRangeEnd);
		}

		if (fivegResource.hasProperty(Fiveg.minInterfaces)) {
			final int minNumIntf = fivegResource
					.getProperty(Fiveg.minInterfaces).getObject().asLiteral()
					.getInt();

			this.setMinNumIntf(minNumIntf);
		}

		if (fivegResource.hasProperty(Fiveg.userEquipmentAddress)) {
			final String userEquipmentAddress = fivegResource
					.getProperty(Fiveg.userEquipmentAddress).getObject()
					.asLiteral().getString();
			this.setUeAdress(userEquipmentAddress);
		}

		if (fivegResource.hasProperty(Fiveg.cloudPublicRouterIp)) {
			final String cloudPublicRouterIp = fivegResource
					.getProperty(Fiveg.cloudPublicRouterIp).getObject()
					.asLiteral().getString();
			this.setCloudPublicRouterIp(cloudPublicRouterIp);
		}

		if (fivegResource.hasProperty(Fiveg.useFloatingIps)) {
			this.setUseFloatingIps(fivegResource
					.getProperty(Fiveg.useFloatingIps).getObject().asLiteral()
					.getBoolean());
		}
	}

	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type, Fiveg.BenchmarkingTool);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);

		super.parseToModel(resource);

		if (this.getConsolePort() != -1) {
			BigInteger consolePort = BigInteger.valueOf(this.getConsolePort());
			resource.addLiteral(Fiveg.managementInterface, consolePort);
		}

		if (this.getBtHostName() != null && !this.getBtHostName().equals("")) {
			String btHostName = this.getBtHostName();
			resource.addLiteral(Fiveg.benchmarkingToolHostName, btHostName);
		}

		if (this.upstartOn != null) {
			resource.addLiteral(Fiveg.upstartOn, this.isUpstartOn());
		}

		if (this.getMgmtIntf() != -1) {
			BigInteger mgmtIntf = BigInteger.valueOf(this.getMgmtIntf());
			resource.addLiteral(Fiveg.managementInterface, mgmtIntf);
		}

		if (this.getNetCIntf() != -1) {
			BigInteger netCIntf = BigInteger.valueOf(this.getNetCIntf());
			resource.addLiteral(Fiveg.subscriberIpRange, netCIntf);
		}

		if (this.getNetDIntf() != -1) {
			BigInteger netDIntf = BigInteger.valueOf(this.getNetDIntf());
			resource.addLiteral(Fiveg.ranBackhaul, netDIntf);
		}

		if (this.getDnsIntf() != -1) {
			BigInteger dnsIntf = BigInteger.valueOf(this.getDnsIntf());
			resource.addLiteral(Fiveg.dnsInterface, dnsIntf);
		}

		if (this.getNetIp() != null && !this.getNetIp().equals("")) {
			String netIp = this.getNetIp();
			resource.addLiteral(Fiveg.netIp, netIp);
		}

		if (this.getNetMask() != null && !this.getNetMask().equals("")) {
			String netMask = this.getNetMask();
			resource.addLiteral(Fiveg.netMask, netMask);
		}

		if (this.getIpRangeStart() != null
				&& !this.getIpRangeStart().equals("")) {
			String ipRangeStart = this.getIpRangeStart();
			resource.addLiteral(Fiveg.ipRangeStart, ipRangeStart);
		}

		if (this.getIpRangeEnd() != null && !this.getIpRangeEnd().equals("")) {
			String ipRangeEnd = this.getIpRangeEnd();
			resource.addLiteral(Fiveg.ipRangeEnd, ipRangeEnd);
		}

		if (this.getImsiRangeStart() != null
				&& !this.getImsiRangeStart().equals("")) {
			String imsiRangeStart = this.getImsiRangeStart();
			resource.addLiteral(Fiveg.imsiRangeStart, imsiRangeStart);
		}

		if (this.getImsiRangeEnd() != null
				&& !this.getImsiRangeEnd().equals("")) {
			String imsiRangeEnd = this.getImsiRangeEnd();
			resource.addLiteral(Fiveg.imsiRangeEnd, imsiRangeEnd);
		}

		if (this.getMinNumIntf() != -1) {
			BigInteger minNumIntf = BigInteger.valueOf(this.getMinNumIntf());
			resource.addLiteral(Fiveg.minInterfaces, minNumIntf);
		}

		if (this.getUeAdress() != null && !this.getUeAdress().equals("")) {
			String ueAddress = this.getUeAdress();
			resource.addLiteral(Fiveg.userEquipmentAddress, ueAddress);
		}

		if (this.getCloudPublicRouterIp() != null
				&& !this.getCloudPublicRouterIp().equals("")) {
			String cloudPublicRouterIp = this.getCloudPublicRouterIp();
			resource.addLiteral(Fiveg.cloudPublicRouterIp, cloudPublicRouterIp);
		}

		if (this.isUseFloatingIps() != null) {
			resource.addLiteral(Fiveg.useFloatingIps, this.isUseFloatingIps());
		}
	}

	@Override
	public JsonObject parseToJson() {

		final JsonBuilderFactory factory = Json.createBuilderFactory(null);

		final JsonArrayBuilder requiresBuilder = Json.createArrayBuilder();
		for (final String require : this.getRequires()) {
			requiresBuilder.add(require);
		}

		String consolePortString = "";
		final int consolePort1 = this.getConsolePort();
		if (consolePort1 != -1) {
			consolePortString = Integer.toString(consolePort1);
		}

		String btHostName = "";
		if (this.getBtHostName() != null && !this.getBtHostName().equals("")) {
			btHostName = this.getBtHostName();
		}

		String upstartOnString = "";
		final Boolean upstartOn1 = this.isUpstartOn();
		if (upstartOn1 != null) {
			upstartOnString = Boolean.toString(upstartOn1);
		}

		String mgmtIntfString = "";
		final int mgmtIntf1 = this.getMgmtIntf();
		if (mgmtIntf1 != -1) {
			mgmtIntfString = Integer.toString(mgmtIntf1);
		}

		String netCIntfString = "";
		final int netCIntf1 = this.getNetCIntf();
		if (netCIntf1 != -1) {
			netCIntfString = Integer.toString(netCIntf1);
		}

		String netDIntfString = "";
		final int netDIntf1 = this.getNetDIntf();
		if (this.getNetDIntf() != -1) {
			netDIntfString = Integer.toString(netDIntf1);
		}

		String dnsIntfString = "";
		final int dnsIntf1 = this.getDnsIntf();
		if (this.getDnsIntf() != -1) {
			dnsIntfString = Integer.toString(dnsIntf1);
		}

		String netIp1 = "";
		if (this.getNetIp() != null && !this.getNetIp().equals("")) {
			netIp1 = this.getNetIp();
		}

		String netMask = "";
		if (this.getNetMask() != null && !this.getNetMask().equals("")) {
			netMask = this.getNetMask();
		}

		String ipRangeStart = "";
		if (this.getIpRangeStart() != null
				&& !this.getIpRangeStart().equals("")) {
			ipRangeStart = this.getIpRangeStart();
		}

		String ipRangeEnd = "";
		if (this.getIpRangeEnd() != null && !this.getIpRangeEnd().equals("")) {
			ipRangeEnd = this.getIpRangeEnd();

		}

		String imsiRangeStart = "";
		if (this.getImsiRangeStart() != null
				&& !this.getImsiRangeStart().equals("")) {
			imsiRangeStart = this.getImsiRangeStart();
		}

		String imsiRangeEnd = "";
		if (this.getImsiRangeEnd() != null
				&& !this.getImsiRangeEnd().equals("")) {
			imsiRangeEnd = this.getImsiRangeEnd();
		}

		String minNumIntfString = "";
		final int minNumIntf1 = this.getMinNumIntf();
		if (this.getMinNumIntf() != -1) {
			minNumIntfString = Integer.toString(minNumIntf1);
		}

		String ueAddress = "";
		if (this.getUeAdress() != null && !this.getUeAdress().equals("")) {
			ueAddress = this.getUeAdress();
		}

		String cloudPublicRouterIp = "";
		if (this.getCloudPublicRouterIp() != null
				&& !this.getCloudPublicRouterIp().equals("")) {
			cloudPublicRouterIp = this.getCloudPublicRouterIp();
		}

		String useFloatingIpsString = "";
		if (this.isUseFloatingIps() != null) {
			useFloatingIpsString = Boolean.toString(this.isUseFloatingIps());
		}

		final JsonObjectBuilder netIp2 = factory.createObjectBuilder().add(
				CONFIG_KEY, "NET_IP");
		final JsonObjectBuilder ueAddress1 = factory.createObjectBuilder().add(
				CONFIG_KEY, "UE_ADDR");
		final JsonObjectBuilder consolePort2 = factory.createObjectBuilder()
				.add(CONFIG_KEY, "CONSOLE_PORT")
				.add(CONFIG_VALUE, consolePortString);
		final JsonObjectBuilder btHostName1 = factory.createObjectBuilder()
				.add(CONFIG_KEY, "BT_HOST_NAME").add(CONFIG_VALUE, btHostName);

		JsonArrayBuilder parameters = factory
				.createArrayBuilder()
				.add(consolePort2)
				.add(btHostName1)
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "UPSTART_ON")
						.add(CONFIG_VALUE, upstartOnString))
				.add(factory.createObjectBuilder().add(CONFIG_KEY, "MGMT_INTF")
						.add(CONFIG_VALUE, mgmtIntfString))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "NET_C_INTF")
						.add(CONFIG_VALUE, netCIntfString))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "NET_D_INTF")
						.add(CONFIG_VALUE, netDIntfString))
				.add(factory.createObjectBuilder().add(CONFIG_KEY, "DNS_INTF")
						.add(CONFIG_VALUE, dnsIntfString))
				.add(netIp2.add(CONFIG_VALUE, netIp1))
				.add(factory.createObjectBuilder().add(CONFIG_KEY, "NET_MASK")
						.add(CONFIG_VALUE, netMask))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "IP_RANGE_START")
						.add(CONFIG_VALUE, ipRangeStart))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "IP_RANGE_END")
						.add(CONFIG_VALUE, ipRangeEnd))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "IMSI_RANGE_START")
						.add(CONFIG_VALUE, imsiRangeStart))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "IMSI_RANGE_END")
						.add(CONFIG_VALUE, imsiRangeEnd))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "MIN_NUM_INTF")
						.add(CONFIG_VALUE, minNumIntfString))
				.add(ueAddress1.add(CONFIG_VALUE, ueAddress))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "CLOUD_PUBLIC_ROUTER_IP")
						.add(CONFIG_VALUE, cloudPublicRouterIp))
				.add(factory.createObjectBuilder()
						.add(CONFIG_KEY, "USE_FLOATING_IPS")
						.add(CONFIG_VALUE, useFloatingIpsString));

		JsonObject configuration = factory
				.createObjectBuilder()
				.add("configuration",
						factory.createObjectBuilder().add("parameters",
								parameters)).add("serviceType", "bt-5G")
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

	public int getConsolePort() {
		return consolePort;
	}

	public void setConsolePort(int consolePort) {
		this.consolePort = consolePort;
	}

	public String getBtHostName() {
		return btHostName;
	}

	public void setBtHostName(String btHostName) {
		this.btHostName = btHostName;
	}

	public int getNetCIntf() {
		return netCIntf;
	}

	public void setNetCIntf(int netCIntf) {
		this.netCIntf = netCIntf;
	}

	public int getDnsIntf() {
		return dnsIntf;
	}

	public void setDnsIntf(final int dnsIntf) {
		this.dnsIntf = dnsIntf;
	}

	public String getNetIp() {
		return netIp;
	}

	public void setNetIp(String netIp) {
		this.netIp = netIp;
	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	public String getIpRangeStart() {
		return ipRangeStart;
	}

	public void setIpRangeStart(String ipRangeStart) {
		this.ipRangeStart = ipRangeStart;
	}

	public String getIpRangeEnd() {
		return ipRangeEnd;
	}

	public void setIpRangeEnd(String ipRangeEnd) {
		this.ipRangeEnd = ipRangeEnd;
	}

	public String getImsiRangeStart() {
		return imsiRangeStart;
	}

	public void setImsiRangeStart(String imsiRangeStart) {
		this.imsiRangeStart = imsiRangeStart;
	}

	public String getImsiRangeEnd() {
		return imsiRangeEnd;
	}

	public void setImsiRangeEnd(String imsiRangeEnd) {
		this.imsiRangeEnd = imsiRangeEnd;
	}

	public String getCloudPublicRouterIp() {
		return cloudPublicRouterIp;
	}

	public void setCloudPublicRouterIp(String cloudPublicRouterIp) {
		this.cloudPublicRouterIp = cloudPublicRouterIp;
	}

	public String getUeAdress() {
		return ueAddress;
	}

	public void setUeAdress(String ueAdress) {
		this.ueAddress = ueAdress;
	}

	public Boolean isUseFloatingIps() {
		return useFloatingIps;
	}

	public void setUseFloatingIps(Boolean useFloatingIps) {
		this.useFloatingIps = useFloatingIps;
	}


}
