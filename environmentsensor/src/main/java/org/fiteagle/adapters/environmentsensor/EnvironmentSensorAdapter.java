package org.fiteagle.adapters.environmentsensor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;
import info.openmultinet.ontology.vocabulary.Osco;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.jcraft.jsch.*;
import org.fiteagle.api.core.OntologyModelUtil;


public final class EnvironmentSensorAdapter extends AbstractAdapter {

    private static final List<Property> MOTOR_CTRL_PROPS = new ArrayList<Property>();

    private transient final HashMap<String, EnvironmentSensor> instanceList = new HashMap<String, EnvironmentSensor>();
    private static final Logger LOGGER = Logger.getLogger(EnvironmentSensorAdapter.class.toString());
	private String sshIP;
	private String username;
	private String password;
	private JSch jsch;

	public EnvironmentSensorAdapter(final Model adapterModel, final Resource adapterABox) {
	super();
	this.uuid = UUID.randomUUID().toString();
	this.adapterTBox = adapterModel;
	this.adapterABox = adapterABox;
	final Resource adapterType = this.getAdapterClass();
	this.adapterABox.addProperty(RDF.type, adapterType);
	this.adapterABox.addProperty(RDFS.label, this.adapterABox.getLocalName());
	this.adapterABox.addProperty(RDFS.comment,
		"A environmentsensor garage adapter that can simulate different dynamic environmentsensor resources.");
	this.adapterABox.addLiteral(MessageBusOntologyModel.maxInstances, 10);

	final Property longitude = this.adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
	final Property latitude = this.adapterTBox.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
	this.adapterABox.addProperty(latitude, "52.516377");
	this.adapterABox.addProperty(longitude, "13.323732");

	final NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
	if (resourceIterator.hasNext()) {
	    final Resource resource = resourceIterator.next().asResource();

	    this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
	    this.adapterABox.getModel().add(resource.getModel());
	    final ResIterator propIterator = this.adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
	    while (propIterator.hasNext()) {
		final Property property = this.adapterTBox.getProperty(propIterator.next().getURI());
		EnvironmentSensorAdapter.MOTOR_CTRL_PROPS.add(property);
	    }
	}

    }

    @Override
    public Model createInstance(final String instanceURI, final Model modelCreate) {
		final EnvironmentSensor environmentsensor = new EnvironmentSensor(this, instanceURI);
		this.instanceList.put(instanceURI, environmentsensor);
		this.updateInstance(instanceURI, modelCreate);
		return this.parseToModel(environmentsensor);
	}

    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    Model parseToModel(final EnvironmentSensor environmentsensor) {
	final Resource resource = ModelFactory.createDefaultModel().createResource(environmentsensor.getInstanceName());
	resource.addProperty(RDF.type, this.getAdapterManagedResources().get(0));
	resource.addProperty(RDF.type, Omn.Resource);
	resource.addProperty(RDFS.label, resource.getLocalName());
	final Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),
		Omn_lifecycle.hasState.getLocalName());
	property.addProperty(RDF.type, OWL.FunctionalProperty);
	resource.addProperty(property, Omn_lifecycle.Ready);
		Resource loginService = resource.getModel().createResource(OntologyModelUtil
				.getResourceNamespace() + "LoginService" + UUID.randomUUID().toString());
		loginService.addProperty(RDF.type, Omn_service.LoginService);
		loginService.addProperty(Omn_service.authentication,"ssh-keys");
		loginService.addProperty(Omn_service.username, "stack");
		loginService.addProperty(Omn_service.hostname, sshIP);
		loginService.addProperty(Omn_service.port,"22");
		resource.addProperty(Omn.hasService, loginService);
	for (final Property prop : EnvironmentSensorAdapter.MOTOR_CTRL_PROPS) {
	    switch (prop.getLocalName()) {
	    case "rpm":
		resource.addLiteral(prop, environmentsensor.getRpm());
		break;
	    case "maxRpm":
		resource.addLiteral(prop, environmentsensor.getMaxRpm());
		break;
	    case "manufacturer":
		resource.addLiteral(prop, environmentsensor.getManufacturer());
		break;
	    case "throttle":
		resource.addLiteral(prop, environmentsensor.getThrottle());
		break;
	    case "isDynamic":
		resource.addLiteral(prop, environmentsensor.isDynamic());
		break;
	    default:
		LOGGER.warning("Unkown: " + prop.getLocalName());
		break;
	    }
	}
	return resource.getModel();
    }

    @Override
    @SuppressWarnings({ "PMD.GuardLogStatement", "PMD.GuardLogStatementJavaUtil" })
    public Model updateInstance(final String instanceURI, final Model configureModel) {
	if (this.instanceList.containsKey(instanceURI)) {
	    final EnvironmentSensor currentEnvironmentSensor = this.instanceList.get(instanceURI);
	    final StmtIterator iter = configureModel.listStatements();
	    while (iter.hasNext()) {
		currentEnvironmentSensor.updateProperty(iter.next());
	    }
	    final Model newModel = this.parseToModel(currentEnvironmentSensor);
	    LOGGER.info("Returning updated environmentsensor: " + newModel);
	    return newModel;
	}
	LOGGER.info("Creating new instance");
	return ModelFactory.createDefaultModel();
    }

    @Override
    public void deleteInstance(final String instanceURI) {
	final EnvironmentSensor environmentsensor = this.getInstanceByName(instanceURI);
	environmentsensor.terminate();
	this.instanceList.remove(instanceURI);
    }

    private EnvironmentSensor getInstanceByName(final String instanceURI) {
	return this.instanceList.get(instanceURI);
    }

    @Override
    public Resource getAdapterABox() {
	return this.adapterABox;
    }

    @Override
    public Model getAdapterDescriptionModel() {
	return this.adapterTBox;
    }

    @Override
    public void updateAdapterDescription() {
	LOGGER.warning("Not implemented.");
    }

    @Override
    public Model getInstance(final String instanceURI) throws InstanceNotFoundException {
	final EnvironmentSensor environmentsensor = this.instanceList.get(instanceURI);
	if (environmentsensor == null) {
	    throw new InstanceNotFoundException("Instance " + instanceURI + " not found");
	}
	return this.parseToModel(environmentsensor);
    }

    @Override
    public Model getAllInstances() throws InstanceNotFoundException {
	final Model model = ModelFactory.createDefaultModel();
	for (final String uri : this.instanceList.keySet()) {
	    model.add(this.getInstance(uri));
	}
	return model;
    }

    @Override
    public void refreshConfig() throws ProcessingException {
	LOGGER.warning("Not implemented.");
    }

    @Override
    public String getId() {
	return this.uuid;
    }

    @Override
    public void shutdown() {
	LOGGER.warning("Not implemented.");
    }

    @Override
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public void configure(final Config configuration) {
	LOGGER.warning("Not implemented. Input: " + configuration);
    }

	public void handleNewGW(String uri, Model model) {
		Resource service = model.getResource(uri);
		Resource loginService = model.getResource(service.getProperty(Omn.hasService).getObject().asResource().getURI());
		String ip = loginService.getProperty(Omn_service.hostname).getObject().asLiteral().getString();
		String port = service.getProperty(Osco.APP_PORT).getObject().asLiteral().getString();
		String id = service.getProperty(RDFS.label).getObject().asLiteral().getString();
		LOGGER.log(Level.INFO,"################## executing command");
		this.jsch = new JSch();
		try {
			Session session = jsch.getSession(username,sshIP);
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.setPassword(password);
			session.connect();
			ChannelExec commandChannel = (ChannelExec) session.openChannel("exec");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			commandChannel.setOutputStream(stream);
			String command = "./start_iwp.sh " + ip + " "+port + " " + id ;
			commandChannel.setCommand(command);
			executeCommand(commandChannel);
		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, "could not connect to iwp");
		}
	}

	public void setSshIP(String sshIP) {
		this.sshIP = sshIP;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	private void executeCommand(ChannelExec channel) {
		try {
			channel.connect();
			InputStream in = channel.getInputStream();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) break;
					LOGGER.log(Level.INFO, new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					LOGGER.log(Level.INFO, "exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}


			channel.disconnect();

		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
		}

	}

	public void handleDelete(String uri, Model model) {
		LOGGER.log(Level.INFO,"################## executing command");
		Resource service = model.getResource(uri);
		String id = service.getProperty(RDFS.label).getObject().asLiteral().getString();
		this.jsch = new JSch();
		try {
			Session session = jsch.getSession(username,sshIP);
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.setPassword(password);
			session.connect();
			ChannelExec commandChannel = (ChannelExec) session.openChannel("exec");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			commandChannel.setOutputStream(stream);
			String command = "./stop_iwp.sh "+ id;
			commandChannel.setCommand(command);
			executeCommand(commandChannel);
		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, "could not connect to iwp");
		}
	}
}
