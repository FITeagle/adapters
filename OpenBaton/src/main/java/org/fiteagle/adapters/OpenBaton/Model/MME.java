package org.fiteagle.adapters.OpenBaton.Model;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
import org.openbaton.catalogue.mano.common.Event;
import org.openbaton.catalogue.mano.common.ConnectionPoint;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.common.VNFDeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.InternalVirtualLink;
import org.openbaton.catalogue.mano.descriptor.VNFComponent;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.mano.descriptor.VirtualLinkDescriptor;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.OpenBaton;

public class MME extends OpenBatonService {
	
	private static final Logger LOGGER = Logger.getLogger(MME.class
			.toString());

	/**
	 * The Config-Files are now Generic, that means that all Classes have the same variables and are build the same way
	 */
	
	private String serviceName;
	private String vendor;
	private String version;
	
	private Set<LifecycleEvent> lifecycleEvents;
	
	private HashMap<String,String> configurationParameter;
	private Configuration configuration;
	
	private Set<VirtualDeploymentUnit> vduSet;

	private int scaleInOut;
	private String vimInstanceName;
	
	private Set<InternalVirtualLink> virtualLinkSet;
	private ArrayList<String> virtualLink;
	
	private VNFDeploymentFlavour deploymentFlavour;
	

	private String type;
	private String endpoint;
	
	
	public MME(OpenBatonAdapter owningAdapter, String instanceUri) {
		
		super(owningAdapter, instanceUri);
		initVariables();

	}
	
	private void initVariables(){
		
		this.serviceName = "mmechessTest";
		this.vendor = "fokus";
		this.version = "0.1";
		
		/**
		 * Adds scripts to each Lifecycle-event that should be startet when reached
		 * First start all Scripts in the CONFIGURE LifecycleEvent, than INSTANTIATE and last START
		 */
				lifecycleEvents = new HashSet<LifecycleEvent>();
				
				LifecycleEvent event = new LifecycleEvent();
				event.setEvent(Event.CONFIGURE);
				List<String> eventList = event.getLifecycle_events();
				if(eventList == null) eventList = new ArrayList<>();
				eventList.add("enodeb_chess_relation_joined");
				eventList.add("enodeb_mme_relation_joined");
				eventList.add("bind9_relation_joined");
				eventList.add("sgwupgwu_relation_joined");
				event.setLifecycle_events(eventList);
				lifecycleEvents.add(event);
				
				event = new LifecycleEvent();
				event.setEvent(Event.INSTANTIATE);
				eventList = event.getLifecycle_events();
				if(eventList == null) eventList = new ArrayList<>();
				eventList.add("configure_interfaces.sh");
				eventList.add("modify_routes.sh");
				eventList.add("preinit");
				eventList.add("mme_install");
				eventList.add("chess_install");
				event.setLifecycle_events(eventList);
				lifecycleEvents.add(event);
				
				
				event = new LifecycleEvent();
				event.setEvent(Event.START);
				eventList = event.getLifecycle_events();
				if(eventList == null) eventList = new ArrayList<>();
				eventList.add("mme_preStart");
				eventList.add("chess_preStart");
				eventList.add("mme_start");
				eventList.add("chess_start");
				event.setLifecycle_events(eventList);
				lifecycleEvents.add(event);
				
				
				/**
				 * Setting all necessary Parameters for the Instance to work
				 * They are all copied from the JSON example file
				 */
				this.configurationParameter = new HashMap<>();
				this.configurationParameter.put("var_num_intf", "2");
				this.configurationParameter.put("var_mgmt_network", "mgmt");
				this.configurationParameter.put("var_net_d_network", "net_d");
				this.configurationParameter.put("var_console_port_one", "10003");
				this.configurationParameter.put("var_console_port_two", "10000");
				this.configurationParameter.put("var_console_port_bind_one", "0.0.0.0");
				this.configurationParameter.put("var_console_port_bind_two", "0.0.0.0");
				this.configurationParameter.put("var_use_slf", "false");
				this.configurationParameter.put("var_port", "3869");
				this.configurationParameter.put("var_bind", "0.0.0.0");
				this.configurationParameter.put("var_localdb", "true");
				this.configurationParameter.put("var_ims_bench", "false");
				this.configurationParameter.put("var_name", "mme");
				this.configurationParameter.put("var_console_port", "10000");
				this.configurationParameter.put("var_ofp_transport", "tcp");
				this.configurationParameter.put("var_ofp_port", "6634");
				this.configurationParameter.put("var_upstart", "true");
				
				configuration = new Configuration();
				Set<ConfigurationParameter> tmpConfs = configuration.getConfigurationParameters();
				if(tmpConfs == null) tmpConfs = new HashSet<ConfigurationParameter>(); 
				
				for(String s: configurationParameter.keySet()){
					ConfigurationParameter confParamter = new ConfigurationParameter();
					confParamter.setConfKey(s);
					confParamter.setValue(configurationParameter.get(s));
					tmpConfs.add(confParamter);
				}
				configuration.setConfigurationParameters(tmpConfs);
				
				
				//Initiate the VirtualDeploymentUnit parameters
				vduSet = new HashSet<VirtualDeploymentUnit>();

					VirtualDeploymentUnit vdu = new VirtualDeploymentUnit();
					vdu.setScale_in_out(1);
					List<String> vimInstances = new ArrayList<String>();
					vimInstances.add("vim-instance");
					vdu.setVimInstanceName(vimInstances);
					Set<String> imageSet = new HashSet<String>();
					imageSet.add("Ubuntu 14.04 Cloud based");
					vdu.setVm_image(imageSet);
				
						// Adding Networks in which this instance is allowed to communicate
						// Also sayin if the Network has an Floating Ip or not
						Set<VNFComponent> vnfdSet = new HashSet<VNFComponent>();
							VNFComponent vnfComponent = new VNFComponent();
								VNFDConnectionPoint vnfdConnectionPoint = new VNFDConnectionPoint();
								Set<VNFDConnectionPoint> vnfdConnecSet = new HashSet<VNFDConnectionPoint>();
							
								vnfdConnectionPoint.setVirtual_link_reference("mgmt");
								vnfdConnecSet.add(vnfdConnectionPoint);
							
								vnfdConnectionPoint = new VNFDConnectionPoint();
								vnfdConnectionPoint.setVirtual_link_reference("net_d");
								vnfdConnectionPoint.setFloatingIp("random");
								vnfdConnecSet.add(vnfdConnectionPoint);

							
							// Vdu [has] Set(VNFComponents), VNFComponent [has] Set(VNFDConnectionPoint)
							vnfComponent.setConnection_point(vnfdConnecSet);
							vnfdSet.add(vnfComponent);
					vdu.setVnfc(vnfdSet);
					
					this.vimInstanceName = "vim-instance";
					
				vduSet.add(vdu);
				
				//Initiate Virtual Links for this Instance
				virtualLinkSet = new HashSet<InternalVirtualLink>();
				
				InternalVirtualLink virtualLink = new InternalVirtualLink();
				Set<String> connectionPointsSet = new HashSet<String>();
				connectionPointsSet.add("mgmt");
				virtualLink.setConnection_points_references(connectionPointsSet);
				virtualLinkSet.add(virtualLink);
				
				virtualLink = new InternalVirtualLink();
				connectionPointsSet = new HashSet<String>();
				connectionPointsSet.add("net_d");
				virtualLink.setConnection_points_references(connectionPointsSet);
				virtualLinkSet.add(virtualLink);
				

				
				//Initiate deployment Flavour and others
				this.deploymentFlavour = new VNFDeploymentFlavour();
				deploymentFlavour.setFlavour_key("s1.small");
				this.type = "mmechess";
				this.endpoint = "generic";
	}
	
	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type, OpenBaton.MME);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);

		super.parseToModel(resource);

		if (this.getServiceName() != null && !this.getServiceName().equals("") ) {
			String flavour = this.getServiceName();
			resource.addLiteral(OpenBaton.instanceName, flavour);
		}
		
		if (this.getVendor() != null && !this.getVendor().equals("") ) {
			String vendor = this.getVendor();
			resource.addLiteral(OpenBaton.vendor, vendor);
		}
		
		if (this.getVersion() != null && !this.getVersion().equals("") ) {
			String version = this.getVersion();
			resource.addLiteral(OpenBaton.version, version);
		}
		
		if (this.getLifecycleEvents() != null && !this.getLifecycleEvents().isEmpty() ) {
			Iterator<LifecycleEvent> keySet = getLifecycleEvents().iterator();
			while(keySet.hasNext()){
				LifecycleEvent event = keySet.next();
				String eventName = event.getEvent().toString();

//				List<String> list = getLifecycleEvents().get(event);
				Property tmpProperty = null;
				switch (eventName){
			case "CONFIGURE":
				tmpProperty = OpenBaton.configureEvent;
				break;
			case "INSTANTIATE":
				tmpProperty = OpenBaton.instantiateEvent;
				break;
			case "START":
				tmpProperty = OpenBaton.startEvent;
				break;
			default:
				LOGGER.log(Level.SEVERE,"LifecycleEvent doesn't equal CONFIGURE, INSTANTIATE or START");
				LOGGER.log(Level.SEVERE,"Couldn't add to Database");
				break;
				}
				
				if(tmpProperty != null){
					Iterator<String> listIterator = event.getLifecycle_events().iterator();
					while(listIterator.hasNext()){
						resource.addLiteral(tmpProperty, listIterator.next());	
					}
				}
				
					
			}
		}
		
		if (this.getEndpoint() != null && !this.getEndpoint().equals("") ) {
			String endpoint = this.getEndpoint();
			resource.addLiteral(OpenBaton.endpoint, endpoint);
		}
		
		if (this.getType() != null && !this.getType().equals("") ) {
			String type = this.getType();
			resource.addLiteral(OpenBaton.type, type);
		}
		
//		if (this.getVirtualLink() != null && !this.getVirtualLink().isEmpty() ) {
//			Iterator<String> virtualLinks = this.getVirtualLink().iterator();
//			while(virtualLinks.hasNext()){
//				resource.addLiteral(OpenBaton.virtualLink, virtualLinks.next());
//			}
//		}
		
		if (this.getVimInstanceName() != null && !this.getVimInstanceName().equals("") ) {
			String vimInstanceName = this.getVimInstanceName();
			resource.addLiteral(OpenBaton.vimInstanceName, vimInstanceName);
		}
		
//		if (this.getVmImage() != null && !this.getVmImage().isEmpty() ) {
//			Iterator<?> vmImage = this.getVmImage().iterator();
//			while(vmImage.hasNext()){
//				resource.addLiteral(OpenBaton.vmImage, vmImage.next());
//			}		}
		
		if (this.getScaleInOut() != -1 ) {
			Integer scaleInOut = this.getScaleInOut();
			resource.addLiteral(OpenBaton.scaleInOut, scaleInOut.toString());
		}
		if (this.getDeploymentFlavour() != null && !this.getDeploymentFlavour().equals("") ) {
			String flavour = this.getDeploymentFlavour().getFlavour_key();
			resource.addLiteral(OpenBaton.deploymentFlavour, flavour);
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String instanceName) {
		this.serviceName = instanceName;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Set<LifecycleEvent> getLifecycleEvents() {
		return lifecycleEvents;
	}

	public void setLifecycleEvents(Set<LifecycleEvent> lifecycleEvents) {
		this.lifecycleEvents = lifecycleEvents;
	}

	public HashMap<String, String> getConfigurationParameter() {
		return configurationParameter;
	}

	public void setConfigurationParameter(HashMap<String, String> configurationParameter) {
		this.configurationParameter = configurationParameter;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public int getScaleInOut() {
		return scaleInOut;
	}

	public void setScaleInOut(int scaleInOut) {
		this.scaleInOut = scaleInOut;
	}

	public String getVimInstanceName() {
		return vimInstanceName;
	}

	public void setVimInstanceName(String vimInstanceName) {
		this.vimInstanceName = vimInstanceName;
	}

	public VNFDeploymentFlavour getDeploymentFlavour() {
		return deploymentFlavour;
	}

	public void setDeploymentFlavour(VNFDeploymentFlavour deploymentFlavour) {
		this.deploymentFlavour = deploymentFlavour;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEndpoint() {
		return endpoint;
	}	
	
	public Set<InternalVirtualLink> getVirtualLinkSet() {
		return this.virtualLinkSet;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public Set<VirtualDeploymentUnit> getVduSet() {
		return vduSet;
	}

	public void setVduSet(Set<VirtualDeploymentUnit> vduSet) {
		this.vduSet = vduSet;
	}




}
