package org.fiteagle.adapters.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.UUID;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class AbstractAdapter {

	private static SSHDeployAdapterConfiguration sshDeployAdapterConfig = SSHDeployAdapterConfiguration
			.getInstance();
	
	private HashMap<String, Object> properties = new HashMap<String, Object>();
	private String type;// class of the implementing adapter
	private String id;
	private String groupId;
	private ResourceAdapterStatus status;
	private boolean exclusive = false;
	private boolean available = true;
	private Date expirationTime;
	
	protected Model modelGeneral = ModelFactory.createDefaultModel();

	public static final String PARAM_TURTLE = "TURTLE";
	public static final String PARAM_RDFXML = "RDF/XML";
	public static final String PARAM_NTRIPLE = "N-TRIPLE";

	public Model discover(String serializationFormat) {

		Model modelDiscover = ModelFactory.createDefaultModel();
		setModelPrefixes(modelDiscover);
		modelDiscover = handleDiscover(modelDiscover);
		return modelDiscover;
	}

	public Model create(Model controlModel) {
		return handleCreate(controlModel);
	}

	public Model release(Model terminateModel) {
		return handleRelease(terminateModel);
	}

	private void setModelPrefixes(Model model) {
		model.setNsPrefix("", "http://fiteagleinternal#");
		model.setNsPrefix(getAdapterSpecificPrefix()[0],
				getAdapterSpecificPrefix()[1]);
		model.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	}

	public static List<AbstractAdapter> getJavaInstances()
			throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public HashMap<String, Object> getProperties() {
		if (properties != null) {
			return properties;
		} else {
			properties = new HashMap<String, Object>();
			return properties;
		}
	}

	public void setProperties(HashMap<String, Object> properties) {
		this.properties = properties;
	}

	public void addProperty(String key, Object value) {
		this.properties.put(key, value);
	}

	public AbstractAdapter() {
		this.setId(UUID.randomUUID().toString());
		this.setStatus(ResourceAdapterStatus.Available);
		
	}

	public void setPreferences(String epcClientIP, String epcClientUsername, String epcClientPassword,
			String epcClientPort, String epcServerIP, String epcServerUsername,
			String epcServerPassword, String epcServerPort) {

		if (epcClientIP != null)
			sshDeployAdapterConfig.setEpcClientIP(epcClientIP);

		if (epcClientUsername != null)
			sshDeployAdapterConfig.setEpcClientUsername(epcClientUsername);

		if (epcClientPassword != null)
			sshDeployAdapterConfig.setEpcClientPassword(epcClientPassword);

		if (epcClientPort != null)
			sshDeployAdapterConfig.setEpcClientPort(epcClientPort);

		if (epcServerIP != null)
			sshDeployAdapterConfig.setEpcServerIP(epcServerIP);

		if (epcServerUsername != null)
			sshDeployAdapterConfig.setEpcServerUsername(epcServerUsername);

		if (epcServerPassword != null)
			sshDeployAdapterConfig.setEpcServerPassword(epcServerPassword);

		if (epcServerPort != null)
			sshDeployAdapterConfig.setEpcServerPort(epcServerPort);
	}


	public void removeAllPreferences() {

		sshDeployAdapterConfig.removeEpcClientIP();

		sshDeployAdapterConfig.removeEpcClientUsername();

		sshDeployAdapterConfig.removeEpcClientPassword();

		sshDeployAdapterConfig.removeEpcClientPort();

		sshDeployAdapterConfig.removeEpcServerIP();

		sshDeployAdapterConfig.removeEpcServerUsername();

		sshDeployAdapterConfig.removeEpcServerPassword();

		sshDeployAdapterConfig.removeEpcServerPort();
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public ResourceAdapterStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceAdapterStatus status) {
		this.status = status;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	public void create(){
	}

	public void release(){
	}

	public void checkStatus(){
	}
	
	public abstract String[] getAdapterSpecificPrefix();

	public abstract Model handleDiscover(Model model);

	public abstract Model handleCreate(Model model);

	public abstract Model handleRelease(Model model);

	public abstract void start();

	public abstract void stop();
	
	public abstract void configure(AdapterConfiguration configuration);

	public abstract boolean isLoaded();

	public abstract void setLoaded(boolean loaded);

	public abstract Resource getResource();

}
