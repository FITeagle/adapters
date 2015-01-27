package org.fiteagle.adapters.epcClient;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.io.StringWriter;

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

public class EpcClientAdapter extends AbstractAdapter implements SSHAccessable {

	private final static Logger LOGGER = Logger
			.getLogger(EpcClientAdapter.class.toString());

	private String[] adapterSpecificPrefix = { "epcClient",
			"http://fiteagle.org/ontology/adapter/epcClient#" };
	private static EpcClientAdapter epcClientAdapterSingleton;

	private Model modelGeneral;

	private Resource epcClientResource;

	private Resource epcClientAdapter;

	private Property username_property;

	private Property sshKey_property;

	private List<Property> epcClientProperties = new LinkedList<Property>();

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

	public static synchronized EpcClientAdapter getInstance() {
		if (epcClientAdapterSingleton == null)
			epcClientAdapterSingleton = new EpcClientAdapter();
		return epcClientAdapterSingleton;
	}

	public EpcClientAdapter() {
		
		this.setIp(sshDeployAdapterConfig.getEpcClientIP());
		this.setPort(sshDeployAdapterConfig.getEpcClientPort());
		this.setUsername(sshDeployAdapterConfig.getEpcClientUsername());
		this.setPassword(sshDeployAdapterConfig.getEpcClientPassword());
		
		modelGeneral = ModelFactory.createDefaultModel();

		modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
		modelGeneral.setNsPrefix("epcClientClient",
				"http://fiteagle.org/ontology/adapter/epcClient#");
		modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
		modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		modelGeneral.setNsPrefix("rdf",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		modelGeneral.setNsPrefix("rdfs",
				"http://www.w3.org/2000/01/rdf-schema#");

		epcClientResource = modelGeneral
				.createResource("http://fiteagle.org/ontology/adapter/epcClient#EpcClient");
		epcClientResource.addProperty(RDF.type, OWL.Class);
		epcClientResource.addProperty(RDFS.subClassOf, modelGeneral
				.createResource("http://fiteagle.org/ontology#Resource"));

		epcClientAdapter = modelGeneral
				.createResource("http://fiteagle.org/ontology/adapter/epcClient#EpcClient");
		epcClientAdapter.addProperty(RDF.type, OWL.Class);
		epcClientAdapter.addProperty(RDFS.subClassOf, modelGeneral
				.createResource("http://fiteagle.org/ontology#Adapter"));

		epcClientAdapter.addProperty(
		    Omn_lifecycle.implements_,
				epcClientResource);
		epcClientResource.addProperty(
				Omn_lifecycle.implementedBy,
				epcClientAdapter);

		// creating properties
		username_property = modelGeneral
				.createProperty("http://fiteagle.org/ontology/adapter/epcClient#username");
		username_property.addProperty(RDF.type, OWL.DatatypeProperty);
		username_property.addProperty(RDFS.domain, epcClientResource);
		username_property.addProperty(RDFS.range, XSD.xstring);
		epcClientProperties.add(username_property);

		sshKey_property = modelGeneral
				.createProperty("http://fiteagle.org/ontology/adapter/epcClient#sshKey");
		sshKey_property.addProperty(RDF.type, OWL.DatatypeProperty);
		sshKey_property.addProperty(RDFS.domain, epcClientResource);
		sshKey_property.addProperty(RDFS.range, XSD.xstring);
		epcClientProperties.add(sshKey_property);

	}

	public Resource getEpcClientResource() {
		return epcClientResource;
	}

	public Property getusername_property() {
		return username_property;
	}

	public Property getsshKey_property() {
		return sshKey_property;
	}

	public EpcClientAdapter(String ip, String port, String username,
			String password, String sshKey) {
		super();
		this.setType("org.fiteagle.adapter.epcClient.EpcClientAdapter");

		
		this.setIp(sshDeployAdapterConfig.getEpcClientIP());
		this.setPort(sshDeployAdapterConfig.getEpcClientPort());
		this.setUsername(sshDeployAdapterConfig.getEpcClientUsername());
		this.setPassword(sshDeployAdapterConfig.getEpcClientPassword());
	}

	public Model handleDiscover(Model modelDiscover) {

		Resource epcClient = modelDiscover
				.createResource("http://fiteagleinternal#");
		addPropertiesToResource(epcClient);
		return modelDiscover;
	}

	@Override
	public Model handleCreate(Model createModel) {

		StringWriter sw = new StringWriter();

		StmtIterator iter = createModel.listStatements(new SimpleSelector(null,
				RDF.type, epcClientResource));
		while (iter.hasNext()) {
			Resource currentResource = iter.nextStatement().getSubject();
			for (Property currentProperty : epcClientProperties) {
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
		Resource epcClientRes = newModel
				.createResource("http://fiteagleinternal#");
		epcClientRes.addProperty(RDFS.label, "epcClient");
		epcClientRes.addProperty(
				RDFS.comment,
				modelGeneral.createLiteral("a new user called " + getNewUser()
						+ " is added to the directory. IP " + getIp()));
		return newModel;
	}

	@Override
	public Model handleRelease(Model model) {

		StmtIterator iter = model.listStatements(new SimpleSelector(null,
				RDF.type, epcClientResource));
		while (iter.hasNext()) {

			Resource currentResource = iter.nextStatement().getSubject();

			for (Property currentProperty : epcClientProperties) {
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
		Resource epcClientRes = newModel
				.createResource("http://fiteagleinternal#");
		epcClientRes.addProperty(RDFS.label, "epcClient");
		epcClientRes.addProperty(
				RDFS.comment,
				modelGeneral.createLiteral("The user " + getNewUser()
						+ " is deleted from the directory "));
		return newModel;
	}

	@Override
	public String[] getAdapterSpecificPrefix() {
		return adapterSpecificPrefix;
	}

	public void addPropertiesToResource(Resource epcClientRes) {

		epcClientRes.addProperty(RDF.type, epcClientResource);

		epcClientRes.addProperty(RDFS.label, "epcClient");

		epcClientRes.addProperty(RDFS.comment,
				modelGeneral.createLiteral("epcClient "));

		epcClientRes.addLiteral(username_property, getNewUser());

		epcClientRes.addLiteral(sshKey_property, getSshKey());
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
		return getEpcClientResource();
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

		if (sshDeployAdapterConfig.getEpcClientPassword() == null
				/*&& sshDeployAdapterConfig.getSsh_keys() == null*/
				|| sshDeployAdapterConfig.getEpcClientIP() == null
				|| sshDeployAdapterConfig.getEpcClientUsername() == null)
			return resourceAdapters;

		if (sshDeployAdapterConfig.getEpcClientIP() != null) {
			ips = sshDeployAdapterConfig.getEpcClientIP().split(",");
		}

		if (sshDeployAdapterConfig.getEpcClientUsername() != null) {
			usernames = sshDeployAdapterConfig.getEpcClientUsername().split(",");
		}

		if (sshDeployAdapterConfig.getEpcClientPassword() != null) {
			passwords = sshDeployAdapterConfig.getEpcClientPassword().split(",");
		}

		if (sshDeployAdapterConfig.getEpcClientPort() != null) {
			ports = sshDeployAdapterConfig.getEpcClientPort().split(",");
		}

		if (!(ips.length == usernames.length && usernames.length == passwords.length)
				&& (!(ips.length == usernames.length && usernames.length == sshKeys.length)))
			return resourceAdapters;

		for (int i = 0; i < usernames.length; i++) {
			EpcClientAdapter sshDeployAdapter = new EpcClientAdapter(
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
