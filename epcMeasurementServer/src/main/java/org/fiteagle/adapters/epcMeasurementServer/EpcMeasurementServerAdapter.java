package org.fiteagle.adapters.epcMeasurementServer;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import org.fiteagle.adapters.common.AbstractAdapter;
import org.fiteagle.adapters.common.AdapterConfiguration;
import org.fiteagle.adapters.common.SSHAccessable;
import org.fiteagle.adapters.common.SSHConnector;
import org.fiteagle.adapters.common.SSHDeployAdapterConfiguration;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class EpcMeasurementServerAdapter extends AbstractAdapter implements
		SSHAccessable {

	private final static Logger LOGGER = Logger
			.getLogger(EpcMeasurementServerAdapter.class.toString());

	private String[] adapterSpecificPrefix = { "epcMeasurementServer",
			"http://fiteagle.org/ontology/adapter/epcMeasurementServer#" };
	private static EpcMeasurementServerAdapter epcMeasurementServerAdapterSingleton;

	private Model modelGeneral;

	private Resource epcMeasurementServerResource;

	private Resource epcMeasurementServerAdapter;

	private Property username_property;

	private Property sshKey_property;

	private List<Property> epcMeasurementServerProperties = new LinkedList<Property>();

	private String hardwareType = "";

	private String ip = "";

	private String username = "";

	private String newUser = "";

	private String password = "";

	private String sshKey = "";

	private String port = "";

	private static boolean loaded = false;

	private AdapterConfiguration adapterConfiguration;

	private static SSHDeployAdapterConfiguration sshDeployAdapterConfig = SSHDeployAdapterConfiguration
			.getInstance();

	public static synchronized EpcMeasurementServerAdapter getInstance() {
		if (epcMeasurementServerAdapterSingleton == null)
			epcMeasurementServerAdapterSingleton = new EpcMeasurementServerAdapter();
		return epcMeasurementServerAdapterSingleton;
	}

	public EpcMeasurementServerAdapter() {
		
		this.setIp(sshDeployAdapterConfig.getEpcServerIP());
		this.setPort(sshDeployAdapterConfig.getEpcServerPort());
		this.setUsername(sshDeployAdapterConfig.getEpcServerUsername());
		this.setPassword(sshDeployAdapterConfig.getEpcServerPassword());
		
		modelGeneral = ModelFactory.createDefaultModel();

		modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
		modelGeneral.setNsPrefix("epcMeasurementServer",
				"http://fiteagle.org/ontology/adapter/epcMeasurementServer#");
		modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
		modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		modelGeneral.setNsPrefix("rdf",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		modelGeneral.setNsPrefix("rdfs",
				"http://www.w3.org/2000/01/rdf-schema#");

		epcMeasurementServerResource = modelGeneral
				.createResource("http://fiteagle.org/ontology/adapter/epcMeasurementServer#EpcMeasurementServer");
		epcMeasurementServerResource.addProperty(RDF.type, OWL.Class);
		epcMeasurementServerResource.addProperty(RDFS.subClassOf, modelGeneral
				.createResource("http://fiteagle.org/ontology#Resource"));

		epcMeasurementServerAdapter = modelGeneral
				.createResource("http://fiteagle.org/ontology/adapter/epcMeasurementServer#EpcMeasurementServer");
		epcMeasurementServerAdapter.addProperty(RDF.type, OWL.Class);
		epcMeasurementServerAdapter.addProperty(RDFS.subClassOf, modelGeneral
				.createResource("http://fiteagle.org/ontology#Adapter"));

		epcMeasurementServerAdapter.addProperty(
				MessageBusOntologyModel.propertyFiteagleImplements,
				epcMeasurementServerResource);
		epcMeasurementServerResource.addProperty(
				MessageBusOntologyModel.propertyFiteagleImplementedBy,
				epcMeasurementServerAdapter);

		// creating properties
		username_property = modelGeneral
				.createProperty("http://fiteagle.org/ontology/adapter/epcMeasurementServer#username");
		username_property.addProperty(RDF.type, OWL.DatatypeProperty);
		username_property
				.addProperty(RDFS.domain, epcMeasurementServerResource);
		username_property.addProperty(RDFS.range, XSD.xstring);
		epcMeasurementServerProperties.add(username_property);

		sshKey_property = modelGeneral
				.createProperty("http://fiteagle.org/ontology/adapter/epcMeasurementServer#sshKey");
		sshKey_property.addProperty(RDF.type, OWL.DatatypeProperty);
		sshKey_property.addProperty(RDFS.domain, epcMeasurementServerResource);
		sshKey_property.addProperty(RDFS.range, XSD.xstring);
		epcMeasurementServerProperties.add(sshKey_property);

	}

	public Resource getEpcMeasurementServerResource() {
		return epcMeasurementServerResource;
	}

	public Property getusername_property() {
		return username_property;
	}

	public Property getsshKey_property() {
		return sshKey_property;
	}

	public EpcMeasurementServerAdapter(String ip, String port, String username,
			String password, String sshKey) {
		super();
		this.setType("org.fiteagle.adapter.epcMeasurementServer.EpcMeasurementServerAdapter");
		this.setIp(ip);
		this.setPort(port);
		this.setUsername(username);
		this.setPassword(password);
		this.setSshKey(sshKey);
		
	}

	public Model handleDiscover(Model modelDiscover) {

		Resource epcMeasurementServer = modelDiscover
				.createResource("http://fiteagleinternal#");
		addPropertiesToResource(epcMeasurementServer);
		return modelDiscover;
	}

	@Override
	public Model handleCreate(Model createModel) {

		StmtIterator iter = createModel.listStatements(new SimpleSelector(null,
				RDF.type, epcMeasurementServerResource));
		while (iter.hasNext()) {
			Resource currentResource = iter.nextStatement().getSubject();
			for (Property currentProperty : epcMeasurementServerProperties) {
				StmtIterator iter2 = currentResource
						.listProperties(currentProperty);
				while (iter2.hasNext()) {
					if (currentProperty == username_property) {
						setNewUser((String) iter2.nextStatement().getObject()
								.asLiteral().getString());
					} else if (currentProperty == sshKey_property) {
						setSshKey((String) iter2.nextStatement().getObject()
								.asLiteral().getString());
					}
				}
			}
		}
		
		start();

		Model newModel = ModelFactory.createDefaultModel();
		Resource epcMeasurementServerRes = newModel
				.createResource("http://fiteagleinternal#");
		epcMeasurementServerRes.addProperty(RDFS.label, "epcMeasurementServer");
		epcMeasurementServerRes.addProperty(
				RDFS.comment,
				modelGeneral.createLiteral("a new user called " + getNewUser()
						+ " is added to the directory. IP " + getIp()));
		return newModel;
	}

	@Override
	public Model handleRelease(Model model) {

		StmtIterator iter = model.listStatements(new SimpleSelector(null,
				RDF.type, epcMeasurementServerResource));
		while (iter.hasNext()) {

			Resource currentResource = iter.nextStatement().getSubject();

			for (Property currentProperty : epcMeasurementServerProperties) {
				StmtIterator iter2 = currentResource
						.listProperties(currentProperty);
				while (iter2.hasNext()) {
					if (currentProperty == username_property) {
						setNewUser((String) iter2.nextStatement().getObject()
								.asLiteral().getString());
					}
				}
			}
		}

		stop();

		Model newModel = ModelFactory.createDefaultModel();
		Resource epcMeasurementServerRes = newModel
				.createResource("http://fiteagleinternal#");
		epcMeasurementServerRes.addProperty(RDFS.label, "epcMeasurementServer");
		epcMeasurementServerRes.addProperty(
				RDFS.comment,
				modelGeneral.createLiteral("The user " + getNewUser()
						+ " is deleted from the directory "));
		return newModel;
	}

	@Override
	public String[] getAdapterSpecificPrefix() {
		return adapterSpecificPrefix;
	}

	public void addPropertiesToResource(Resource epcMeasurementServerRes) {

		epcMeasurementServerRes.addProperty(RDF.type,
				epcMeasurementServerResource);

		epcMeasurementServerRes.addProperty(RDFS.label, "epcMeasurementServer");

		epcMeasurementServerRes.addProperty(RDFS.comment,
				modelGeneral.createLiteral("epcMeasurementServer "));

		epcMeasurementServerRes.addLiteral(username_property, getNewUser());

		epcMeasurementServerRes.addLiteral(sshKey_property, getSshKey());
	}

	@Override
	public String getHardwareType() {
		return this.hardwareType;
	}

	@Override
	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}

	@Override
	public String getIp() {
		return this.ip;
	}

	@Override
	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String getPort() {
		return this.port;
	}

	@Override
	public void setPort(String port) {
		this.port = port;

	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewUser() {
		return this.newUser;
	}

	public void setNewUser(String newUser) {
		this.newUser = newUser;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getSshKey() {
		return this.sshKey;
	}

	@Override
	public void setSshKey(String sshKey) {
		this.sshKey = sshKey;
	}

	@Override
	public void start() {

		SSHConnector connector = new SSHConnector(ip, port, username, password,
				adapterConfiguration);
		connector.connect();
		connector.createUserAccount(newUser);

		connector.createUserSSHDirectory(newUser);
		connector.createAuthorizedKeysFile(newUser);
		connector.changeOwnerOfUserHome(newUser);
		connector.addSSHKey(sshKey, newUser);
		connector.disconnect();

	}

	@Override
	public void stop() {

		SSHConnector connector = new SSHConnector(ip, port, username, password,
				adapterConfiguration);
		connector.connect();
		connector.lockAccount(newUser);
		connector.killAllUserProcesses(newUser);
		connector.deleteUser(newUser);
		connector.deleteUserDirectory(newUser);
		connector.disconnect();

	}



	@Override
	public void configure(AdapterConfiguration configuration) {
		this.adapterConfiguration = configuration;

	}


	@Override
	public boolean isLoaded() {
		return this.loaded;
	}

	@Override
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public Resource getResource() {
		return getEpcMeasurementServerResource();
	}

	public static List<AbstractAdapter> getJavaInstances() {
		List<AbstractAdapter> resourceAdapters = new ArrayList<AbstractAdapter>();

		String[] ips = null;
		String[] ports = null;
		String[] usernames = null;
		String[] passwords = null;
		String[] sshKeys = null;
		String[] countries = null;
		String[] latitudes = null;
		String[] longitudes = null;
		String[] hardwareTypes = null;

		if (sshDeployAdapterConfig.getEpcServerPassword() == null
				/*&& sshDeployAdapterConfig.getSsh_keys() == null*/
				|| sshDeployAdapterConfig.getEpcServerIP() == null
				|| sshDeployAdapterConfig.getEpcServerUsername() == null)
			return resourceAdapters;

		if (sshDeployAdapterConfig.getEpcServerIP() != null) {
			ips = sshDeployAdapterConfig.getEpcServerIP().split(",");
		}

		if (sshDeployAdapterConfig.getEpcServerUsername() != null) {
			usernames = sshDeployAdapterConfig.getEpcServerUsername().split(",");
		}

		if (sshDeployAdapterConfig.getEpcServerPassword() != null) {
			passwords = sshDeployAdapterConfig.getEpcServerPassword().split(",");
		}

		if (sshDeployAdapterConfig.getEpcServerPort() != null) {
			ports = sshDeployAdapterConfig.getEpcServerPort().split(",");
		}

		if (!(ips.length == usernames.length && usernames.length == passwords.length)
				&& (!(ips.length == usernames.length && usernames.length == sshKeys.length)))
			return resourceAdapters;

		if (!(ips.length == usernames.length && usernames.length == passwords.length)
				&& (!(ips.length == usernames.length && usernames.length == sshKeys.length)))
			return resourceAdapters;

		for (int i = 0; i < usernames.length; i++) {
			EpcMeasurementServerAdapter sshDeployAdapter = new EpcMeasurementServerAdapter(
					ips[i].trim(), ports[i].trim(), usernames[i].trim(),
					passwords[i].trim(), sshKeys[i].trim());

			if (hardwareTypes != null && i < hardwareTypes.length)
				sshDeployAdapter.setHardwareType(hardwareTypes[i].trim());
			if (countries != null && i < countries.length)
				sshDeployAdapter.addProperty("country", countries[i].trim());
			if (latitudes != null && i < latitudes.length)
				sshDeployAdapter.addProperty("latitude", latitudes[i].trim());
			if (longitudes != null && i < longitudes.length)
				sshDeployAdapter.addProperty("longitude", longitudes[i].trim());

			sshDeployAdapter.setExclusive(true);
			resourceAdapters.add(sshDeployAdapter);
		}

		return resourceAdapters;
	}

}
