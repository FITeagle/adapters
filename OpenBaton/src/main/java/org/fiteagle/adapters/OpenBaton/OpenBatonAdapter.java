package org.fiteagle.adapters.OpenBaton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.OpenBaton.Model.BenchmarkingTool;
import org.fiteagle.adapters.OpenBaton.Model.Control;
import org.fiteagle.adapters.OpenBaton.Model.DomainNameSystem;
import org.fiteagle.adapters.OpenBaton.Model.ENodeB;
import org.fiteagle.adapters.OpenBaton.Model.FiveGCore;
import org.fiteagle.adapters.OpenBaton.Model.Gateway;
import org.fiteagle.adapters.OpenBaton.Model.HomeSubscriberService;
import org.fiteagle.adapters.OpenBaton.Model.MME;
import org.fiteagle.adapters.OpenBaton.Model.OpenBatonGeneric;
import org.fiteagle.adapters.OpenBaton.Model.ServiceContainer;
import org.fiteagle.adapters.OpenBaton.Model.SgwuPgwu;
import org.fiteagle.adapters.OpenBaton.Model.Switch;
import org.fiteagle.adapters.OpenBaton.Model.Topology;
import org.fiteagle.adapters.OpenBaton.Model.UE;
import org.fiteagle.adapters.OpenBaton.dm.OpenBatonAdapterMDBSender;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.OntologyModelUtil;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;
import info.openmultinet.ontology.vocabulary.OpenBaton;
import info.openmultinet.ontology.vocabulary.Osco;

public final class OpenBatonAdapter extends AbstractAdapter {
	private static final Logger LOGGER = Logger.getLogger(OpenBatonAdapter.class.toString());
	protected OpenBatonClient openBatonClient;

	private OpenBatonAdapterMDBSender listener;

	@EJB
	OpenBatonAdapterControl openBatonAdapterControler;

	private String username;
	private String password;
    private String nfvoIp;
    private String nfvoPort;
    private String version;
    private String vpnIP;
    private String vpnPort;
    private String debugString;
    private VirtualNetworkFunctionDescriptor createdDebugMME;

	private transient final HashMap<String, OpenBatonGeneric> instanceList = new HashMap<String, OpenBatonGeneric>();

	public OpenBatonAdapter(final Model adapterModel, final Resource adapterABox) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.adapterTBox = adapterModel;
		this.adapterABox = adapterABox;
		final Resource adapterType = this.getAdapterClass();
		this.adapterABox.addProperty(RDF.type, adapterType);
		this.adapterABox.addProperty(RDFS.label, this.adapterABox.getLocalName());
		this.adapterABox.addProperty(RDFS.comment, "OpenBaton Adapter");

		// this.adapterABox.addProperty(Omn_lifecycle.canImplement,
		// Omn_domain_pc.PC);
		openBatonClient = new OpenBatonClient(this);

		/**
		 * Looking up all Resources that belongs to the Adapter and will be
		 * shown in SFA as Nodes.
		 */
		Model implementables = OntologyModelUtil.loadModel("ontologies/openBaton-adapter.ttl",
				IMessageBus.SERIALIZATION_TURTLE);
		final NodeIterator resourceIterator = implementables.listObjectsOfProperty(Omn_lifecycle.implements_);

		while (resourceIterator.hasNext()) {
			final Resource resource = resourceIterator.next().asResource();
			this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
			this.adapterABox.getModel().add(resource.getModel());
			final ResIterator propIterator = this.adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
			while (propIterator.hasNext()) {
				final Property property = this.adapterTBox.getProperty(propIterator.next().getURI());
			}
		}
	}

	public void init() {
		openBatonClient.init();
	}

	@Override
	public void updateAdapterDescription() throws ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Model updateInstance(final String instanceURI, final Model configureModel) {

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "updateInstance instanceURI: " + instanceURI);
			LOGGER.log(Level.INFO, "updateInstance configureModel: "
					+ MessageUtil.serializeModel(configureModel, IMessageBus.SERIALIZATION_TURTLE));
		}

		// if the instance is in the list of instances in the adapter
		if (this.getInstanceList().containsKey(instanceURI)) {

			final OpenBatonGeneric currentFiveG = this.getInstanceList().get(instanceURI);
			Resource fivegResource = configureModel.getResource(instanceURI);
			currentFiveG.updateInstance(fivegResource);

			final Model newModel = this.parseToModel(currentFiveG);
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "Returning updated fiveg: " + newModel);
			}

			return newModel;
		} else {
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "Instance list does not contain key.");
			}
		}
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Creating new instance");
		}
		return ModelFactory.createDefaultModel();
	}

	@Override
	public Model createInstances(Model model) throws ProcessingException, InvalidRequestException {

		Model createdInstancesModel = super.createInstances(model);
		LOGGER.warning("createInstances override method.");

		String topologyUri = null;

		NodeIterator objects = model.listObjectsOfProperty(Omn.isResourceOf);
		if (objects.hasNext()) {
			RDFNode object = objects.next();
			topologyUri = object.asResource().getURI();
		}

		// Uncomment this method to make creation at OpenSDNCore automatically
		// occur upon SFA provision call
		// try {
		// HelperMethods.createTopologyAtOpenSDNCore(this, "FiveGAdapter-1",
		// topologyUri, 10);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return createdInstancesModel;
	}

	@Override
	public Model createInstance(String instanceURI, Model newInstanceModel) {

		Resource resource = newInstanceModel.getResource(instanceURI);

		// check if already created
		for (Map.Entry<String, OpenBatonGeneric> entry : this.getInstanceList().entrySet()) {
			String key = entry.getKey();
			OpenBatonGeneric value = entry.getValue();
			if (instanceURI.equals(key)) {
				if (LOGGER.isLoggable(Level.WARNING)) {
					LOGGER.warning("Instance already exists: " + instanceURI);
				}
				return this.parseToModel(value);
			}
		}

		// check if topology exists, otherwise create it
		String topologyUri = null;
		Topology topology = null;
		if (resource.hasProperty(Omn.isResourceOf)) {
			topologyUri = resource.getProperty(Omn.isResourceOf).getObject().asResource().getURI().toString();

			if (this.getInstanceList().get(topologyUri) == null) {
				topology = new Topology(this, topologyUri);
			} else {
				topology = (Topology) this.getInstanceList().get(topologyUri);
			}

			this.getInstanceList().put(topologyUri, topology);
		}

		// Check which Ressource should be created
		if (resource.hasProperty(RDF.type, OpenBaton.Gateway)) {

			final Gateway openBaton = new Gateway(this, instanceURI);
			this.getInstanceList().put(instanceURI, openBaton);
			this.updateInstance(instanceURI, newInstanceModel);
			openBatonClient.createGateway(openBaton, null);
			return this.parseToModel(openBaton);

		} else if (resource.hasProperty(RDF.type, OpenBaton.DomainNameSystem)) {

			final DomainNameSystem openBaton = new DomainNameSystem(this, instanceURI);
			this.getInstanceList().put(instanceURI, openBaton);
			this.updateInstance(instanceURI, newInstanceModel);
			openBatonClient.createDomainNameSystem(openBaton, null);
			return this.parseToModel(openBaton);

		} else if (resource.hasProperty(RDF.type, OpenBaton.ENodeB)) {

			final ENodeB openBaton = new ENodeB(this, instanceURI);
			this.getInstanceList().put(instanceURI, openBaton);
			this.updateInstance(instanceURI, newInstanceModel);
			openBatonClient.createENodeB(openBaton, null);
			return this.parseToModel(openBaton);

		} else if (resource.hasProperty(RDF.type, OpenBaton.Switch)) {

			final Switch fiveg = new Switch(this, instanceURI);
			this.getInstanceList().put(instanceURI, fiveg);
			this.updateInstance(instanceURI, newInstanceModel);
			return this.parseToModel(fiveg);

		} else if (resource.hasProperty(RDF.type, OpenBaton.BenchmarkingTool)) {

			final BenchmarkingTool fiveg = new BenchmarkingTool(this, instanceURI);
			this.getInstanceList().put(instanceURI, fiveg);
			this.updateInstance(instanceURI, newInstanceModel);
			return this.parseToModel(fiveg);

		} else if (resource.hasProperty(RDF.type, OpenBaton.MME)) {

			final MME openBaton = new MME(this, instanceURI);
			this.getInstanceList().put(instanceURI, openBaton);
			this.updateInstance(instanceURI, newInstanceModel);
			Model tmpModel = this.parseToModel(openBaton);
			openBatonClient.createMME(openBaton, null);
			return tmpModel;

		} else if (resource.hasProperty(RDF.type, OpenBaton.Control)) {

			final Control fiveg = new Control(this, instanceURI);
			this.getInstanceList().put(instanceURI, fiveg);
			this.updateInstance(instanceURI, newInstanceModel);
			return this.parseToModel(fiveg);

		} else if (resource.hasProperty(RDF.type, OpenBaton.HomeSubscriberServer)) {

			final HomeSubscriberService fiveg = new HomeSubscriberService(this, instanceURI);
			this.getInstanceList().put(instanceURI, fiveg);
			this.updateInstance(instanceURI, newInstanceModel);
			return this.parseToModel(fiveg);

		} else if (resource.hasProperty(RDF.type, OpenBaton.FiveGCore)) {

            FiveGCore fiveg = new FiveGCore(this, instanceURI);
            this.getInstanceList().put(instanceURI, fiveg);
            this.updateInstance(instanceURI, newInstanceModel);
            this.openBatonClient.createFiveGCore(fiveg);
            Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(), Omn_lifecycle.hasState.getLocalName());
            property.addProperty(RDF.type, (RDFNode)OWL.FunctionalProperty);
            try {
                CreateNSR createNsr = new CreateNSR(resource, fiveg, property, this.listener);
                ManagedThreadFactory threadFactory = (ManagedThreadFactory)new InitialContext().lookup("java:jboss/ee/concurrency/factory/default");
                Thread createVMThread = threadFactory.newThread((Runnable)createNsr);
                createVMThread.start();
            }
            catch (NamingException e) {
                e.printStackTrace();
            }
            Model model2 = this.parseToModel((OpenBatonGeneric)fiveg);
            return model2;

		} else if (resource.hasProperty(RDF.type, OpenBaton.UE)) {

			final UE openBaton = new UE(this, instanceURI);
			this.getInstanceList().put(instanceURI, openBaton);
			this.updateInstance(instanceURI, newInstanceModel);
			openBatonClient.createUe(openBaton, null);
			Model model = this.parseToModel(openBaton);
			return model;

		} else if (resource.hasProperty(RDF.type, OpenBaton.SgwuPgwu)) {

			final SgwuPgwu openBaton = new SgwuPgwu(this, instanceURI);
			this.getInstanceList().put(instanceURI, openBaton);
			this.updateInstance(instanceURI, newInstanceModel);
			openBatonClient.createSgwuPgwu(openBaton, null);
			Model model = this.parseToModel(openBaton);
			return model;

		} else if (resource.hasProperty(RDF.type, Osco.ServiceContainer)) {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("createInstance: Creating ServiceContainer " + instanceURI);
			}
			// need to check if already created
			OpenBatonGeneric fiveg = this.getInstanceObject(instanceURI);
			ServiceContainer sc = null;
			if (fiveg == null) {
				sc = new ServiceContainer(this, instanceURI);
				this.getInstanceList().put(instanceURI, sc);
				topology.getServiceContainers().add(sc);
				sc.setTopology(topology);
				this.updateInstance(instanceURI, newInstanceModel);
			} else {
				sc = (ServiceContainer) fiveg;
			}

			return this.parseToModel(sc);
		}
		if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("Couldn't recognize type, so returning original model.");
		}
		return newInstanceModel;
	}

	Model parseToModel(final OpenBatonGeneric fivegGeneric) {

		LOGGER.warning("Calling parse to model...");
		final Resource resource = ModelFactory.createDefaultModel().createResource(fivegGeneric.getInstanceUri());

		final Property property = resource.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),
				Omn_lifecycle.hasState.getLocalName());
		property.addProperty(RDF.type, OWL.FunctionalProperty);

		if (!(fivegGeneric instanceof Topology)) {
			resource.addProperty(property, Omn_lifecycle.Ready);
			final Property propertyLabel = resource.getModel().createProperty(RDFS.label.getNameSpace(),
					RDFS.label.getLocalName());
			propertyLabel.addProperty(RDF.type, OWL.FunctionalProperty);
		}

		if (fivegGeneric instanceof Gateway) {
			Gateway gw = (Gateway) fivegGeneric;
			gw.parseToModel(resource);
		} else if (fivegGeneric instanceof Switch) {
			Switch sw = (Switch) fivegGeneric;
			sw.parseToModel(resource);
		} else if (fivegGeneric instanceof ENodeB) {
			ENodeB eNodeB = (ENodeB) fivegGeneric;
			eNodeB.parseToModel(resource);
		} else if (fivegGeneric instanceof Control) {
			Control control = (Control) fivegGeneric;
			control.parseToModel(resource);
		} else if (fivegGeneric instanceof HomeSubscriberService) {
			HomeSubscriberService hss = (HomeSubscriberService) fivegGeneric;
			hss.parseToModel(resource);
		} else if (fivegGeneric instanceof BenchmarkingTool) {
			BenchmarkingTool bt = (BenchmarkingTool) fivegGeneric;
			bt.parseToModel(resource);
		} else if (fivegGeneric instanceof DomainNameSystem) {
			DomainNameSystem dns = (DomainNameSystem) fivegGeneric;
			dns.parseToModel(resource);
		} else if (fivegGeneric instanceof ServiceContainer) {
			ServiceContainer sc = (ServiceContainer) fivegGeneric;
			sc.parseToModel(resource);
		} else if (fivegGeneric instanceof Topology) {
			Topology topology = (Topology) fivegGeneric;
			topology.parseToModel(resource);
		} else if (fivegGeneric instanceof MME) {
			MME mme = (MME) fivegGeneric;
			mme.parseToModel(resource);
		} else if (fivegGeneric instanceof UE) {
			UE ue = (UE) fivegGeneric;
			ue.parseToModel(resource);
		} else if (fivegGeneric instanceof SgwuPgwu) {
			SgwuPgwu sgwuPgwu = (SgwuPgwu) fivegGeneric;
			sgwuPgwu.parseToModel(resource);
		} else if (fivegGeneric instanceof FiveGCore) {
			FiveGCore fiveG = (FiveGCore) fivegGeneric;
			fiveG.parseToModel(resource);
		}
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "CONTENT parse to model: " + resource.getModel().toString());
		}
		return resource.getModel();
	}

	// public Model testCreateInstance(String instanceURI, Model
	// newInstanceModel)
	// throws ProcessingException, InvalidRequestException {
	//
	// Resource resource = newInstanceModel.getResource(instanceURI);
	//
	// if (resource.hasProperty(RDF.type, OpenBaton.NetworkServiceDescriptor)) {
	//
	//// final Gateway fiveg = new Gateway(this, instanceURI);
	//// this.getInstanceList().put(instanceURI, fiveg);
	//// this.updateInstance(instanceURI, modelCreate);
	//
	//// NetworkServiceDescriptor netDescriptor =
	// openBatonClient.createNetworkServiceDescriptor();
	// return null;
	//// return this.parseToModel(netDescriptor);
	//
	// }
	//
	// return null;
	// }

	public class CreateNSR	implements Runnable {
	    private Resource resource;
	    private FiveGCore fiveG;
	    private NetworkServiceRecord fivegNSR;
	    private Property property;
	    private OpenBatonClient client;
	    private String nsrID;
	    private int counter;
	    private OpenBatonAdapterMDBSender parent;

	    public CreateNSR(Resource resource, OpenBatonGeneric openBatonGeneric, Property property, OpenBatonAdapterMDBSender parent) {
	        this.resource = resource;
	        this.parent = parent;
	        this.fiveG = (FiveGCore)openBatonGeneric;
	        this.property = property;
	        this.client = new OpenBatonClient(null);
	        this.counter = 0;
	        LOGGER.log(Level.SEVERE, "Thread Created");
	    }
	    
	    @Override
	    public void run() {
	        while (!Thread.currentThread().isInterrupted() && this.counter < 10) {
	            LOGGER.log(Level.SEVERE, "Starting RUN MEthode now");
	            try {
	                try {
	                    if (this.fivegNSR == null) {
	                        this.fiveG.setNsr(client.getAllNSRs().get(0));
	                        this.fivegNSR = this.fiveG.getNsr();
	                    }
	                }
	                catch (Exception e) {
	                    LOGGER.log(Level.SEVERE, "Exception in getting All NSRs");
	                }
	                ++this.counter;
	                if (this.checkIfNsrIsActive()) {
	                    this.getIpsFromNsr();
	                    LOGGER.log(Level.SEVERE, "Adding LoginResource to Resource");
	                    LOGGER.log(Level.SEVERE, "-------------------------------------------");
	                    Resource loginService = this.resource.getModel().createResource(OntologyModelUtil.getResourceNamespace() + "LoginService" + UUID.randomUUID().toString());
	                    if (OpenBatonAdapter.this.vpnIP == null || OpenBatonAdapter.this.vpnIP.equals("") || OpenBatonAdapter.this.vpnPort == null || OpenBatonAdapter.this.vpnPort.equals("")) {
	                        loginService.addProperty(RDF.type, (RDFNode)Omn_service.LoginService);
	                        loginService.addProperty((Property)Omn_service.authentication, "ssh-keys");
	                        loginService.addProperty((Property)Omn_service.username, "home");
	                        loginService.addProperty((Property)Omn_service.hostname, "127.0.0.1");
	                        loginService.addProperty((Property)Omn_service.port, "22");
	                    } else {
	                        loginService.addProperty(RDF.type, (RDFNode)Omn_service.LoginService);
	                        loginService.addProperty((Property)Omn_service.authentication, "ssh-keys");
	                        loginService.addProperty((Property)Omn_service.username, "home");
	                        loginService.addProperty((Property)Omn_service.hostname, OpenBatonAdapter.this.vpnIP);
	                        loginService.addProperty((Property)Omn_service.port, OpenBatonAdapter.this.vpnPort);
	                    }
	                    this.resource.addProperty((Property)Omn.hasService, (RDFNode)loginService);
	                    Statement blub = this.resource.getProperty(this.property);
	                    blub.changeObject((RDFNode)Omn_lifecycle.Started);
	                    this.resource.addProperty(this.property, (RDFNode)Omn_lifecycle.Started);
	                    this.resource.addProperty((Property)Omn_lifecycle.hasOriginalID, this.fivegNSR.getId());
	                    LOGGER.log(Level.SEVERE, "Added LoginService to Resource");
	                    this.parent.publishModelUpdate(this.resource.getModel(), UUID.randomUUID().toString(), "INFORM", "TARGET_ORCHESTRATOR");
	                    LOGGER.log(Level.SEVERE, "Killing Thread now");
	                    Thread.currentThread().interrupt();
	                    continue;
	                }
	                Thread.currentThread();
	                Thread.sleep(30000);
	            }
	            catch (Exception e) {
	                ++this.counter;
	                e.printStackTrace();
	                if (this.counter >= 9) {
	                    try {
	                        Thread.currentThread();
	                        Thread.sleep(30000);
	                    }
	                    catch (InterruptedException e1) {
	                        e1.printStackTrace();
	                    }
	                    continue;
	                }
	                Thread.currentThread().interrupt();
	            }
	        }
	    }

	    public void getIpsFromNsr() {
	    }

	    
	    public boolean checkIfNsrIsActive() throws InterruptedException {
	        this.fivegNSR = this.client.updateNetworkServiceRecord(this.fivegNSR);
	        String status = this.fivegNSR.getStatus().toString();
	        LOGGER.log(Level.SEVERE, "STATUS of NSR: " + status);
	        switch (status) {
	            case "NULL": {
	                LOGGER.log(Level.SEVERE, "NetworkServiceRecord is NULL at the moment. Will check again later");
	                return false;
	            }
	            case "INITIALIZED": {
	                LOGGER.log(Level.SEVERE, "NetworkServiceRecord is INITIALIZED at the moment. Will check again later");
	                return false;
	            }
	            case "ERROR": {
	                LOGGER.log(Level.SEVERE, "NetworkServiceRecord ERRORED while starting. Pls check the Logs");
	                return false;
	            }
	            case "ACTIVE": {
	                LOGGER.log(Level.SEVERE, "NetworkServiceRecord is ACTIVE now. Will try to get Floating Ips now");
	                return true;
	            }
	        }
	        LOGGER.log(Level.SEVERE, "NetworkServiceRecord is not ready at the moment. Will check again later");
	        return false;
	    }
	}
	@Override
	public void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Model getInstance(String instanceURI)
			throws InstanceNotFoundException, ProcessingException, InvalidRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshConfig() throws ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Config configuration) {
		// TODO Auto-generated method stub

	}

	public void setListener(OpenBatonAdapterMDBSender mdbSender) {
		// TODO Auto-generated method stub
		this.listener = mdbSender;
	}

	protected String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	protected String getNfvoIp() {
		return nfvoIp;
	}

	protected void setNfvoIp(String nfvoIp) {
		this.nfvoIp = nfvoIp;
	}

	protected String getNfvoPort() {
		return nfvoPort;
	}

	protected void setNfvoPort(String nfvoPort) {
		this.nfvoPort = nfvoPort;
	}

	protected String getVersion() {
		return version;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

	protected String getVpnIP() {
		return vpnIP;
	}

	protected void setVpnIP(String vpnIP) {
		this.vpnIP = vpnIP;
	}

	protected String getVpnPort() {
		return vpnPort;
	}

	protected void setVpnPort(String vpnPort) {
		this.vpnPort = vpnPort;
	}

	public HashMap<String, OpenBatonGeneric> getInstanceList() {
		return instanceList;
	}

	public Topology getTopologyObject(final String topologyURI) {
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Get topology: " + topologyURI);
		}
		OpenBatonGeneric fiveG = this.getInstanceList().get(topologyURI);

		Topology topology = null;
		if (fiveG instanceof Topology) {
			topology = (Topology) fiveG;
		}

		return topology;
	}

	public String parseConfig(Resource resource, String parameter) {
		Model model = ModelFactory.createDefaultModel();
		return resource.getProperty(model.createProperty(OpenBaton.getURI(), parameter)).getLiteral().getString();
	}

	public String getInstanceUri(OpenBatonGeneric OpenBatonGeneric) {
		return OpenBatonGeneric.getInstanceUri();
	}

	@Override
	public Resource getAdapterABox() {
		// TODO Auto-generated method stub
		return adapterABox;
	}

	@Override
	public Model getAdapterDescriptionModel() {
		// TODO Auto-generated method stub
		return adapterTBox;
	}

	public OpenBatonGeneric getInstanceObject(final String instanceURI) {

		final OpenBatonGeneric fiveg = this.getInstanceList().get(instanceURI);
		if (LOGGER.isLoggable(Level.WARNING)) {
			LOGGER.warning("Get instance: " + instanceURI);
		}
		return fiveg;
	}

	public void createNewVnfPackage() {
        String mmeID;
        MME mme = new MME(this, "http://TEST.OPENBATON.MME");
        this.createdDebugMME = this.openBatonClient.createMME(mme);
        this.debugString = mmeID = this.createdDebugMME.getId();
        Model newmModel = ModelFactory.createDefaultModel();
        Resource newResource = newmModel.createResource("http://TEST.OPENBATON.RESOURCE");
        newResource.addProperty(RDF.type, OWL.Class);
        newResource.addProperty((Property)Omn_lifecycle.hasID, mmeID);
        newResource.addProperty(RDFS.subClassOf, Omn.Resource);
        this.adapterABox.addProperty((Property)Omn_lifecycle.canImplement, newResource);
        this.adapterABox.getModel().add(newResource.getModel());
        ResIterator propIterator = this.adapterTBox.listSubjectsWithProperty(RDFS.domain, newResource);
        while (propIterator.hasNext()) {
            Property property = this.adapterTBox.getProperty(((Resource)propIterator.next()).getURI());
        }
        this.listener.publishModelUpdate(this.adapterABox.getModel(), UUID.randomUUID().toString(), "INFORM", "TARGET_ORCHESTRATOR");
	}

	public void updateOldVnfPackage() {
		// TODO Auto-generated method stub

	}

	public void addUploadedPackageToDatabase(UUID uuid, String fileName) {
		
		Resource resourceToCreate = ModelFactory.createDefaultModel().createResource(adapterABox.getLocalName()+"/" +fileName);
		resourceToCreate.addProperty(Omn_lifecycle.hasID,uuid.toString());
		resourceToCreate.addProperty(RDFS.label,fileName);
		resourceToCreate.addProperty(RDFS.subClassOf, Omn.Resource);
		adapterABox.addProperty(Omn_lifecycle.canImplement, resourceToCreate);
        listener.publishModelUpdate(adapterABox.getModel(), UUID.randomUUID().toString(), "INFORM", "TARGET_ORCHESTRATOR");
        listener.publishModelUpdate(resourceToCreate.getModel(), UUID.randomUUID().toString(), "INFORM", "TARGET_ORCHESTRATOR");

	}

}
