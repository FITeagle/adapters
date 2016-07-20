package org.fiteagle.adapters.OpenBaton.Model;

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

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
import org.openbaton.catalogue.mano.common.Event;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.common.VNFDeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.InternalVirtualLink;
import org.openbaton.catalogue.mano.descriptor.VNFComponent;
import org.openbaton.catalogue.mano.descriptor.VNFDConnectionPoint;
import org.openbaton.catalogue.mano.descriptor.VirtualDeploymentUnit;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.catalogue.nfvo.ConfigurationParameter;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Fiveg;
import info.openmultinet.ontology.vocabulary.OpenBaton;

public class ENodeB extends OpenBatonService{

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

	private static final Logger LOGGER = Logger.getLogger(ENodeB.class
			.toString());

	public ENodeB(final OpenBatonAdapter owningAdapter, final String instanceName) {

		super(owningAdapter, instanceName);
		initVariables();

	}

private void initVariables(){
		
		this.serviceName = "enodeb";
		this.vendor = "fokus";
		this.version = "0.1";
		
		//Initiate the lifecycleEvents
		lifecycleEvents = new HashSet<LifecycleEvent>();
		
		LifecycleEvent event = new LifecycleEvent();
		event.setEvent(Event.CONFIGURE);
		List<String> eventList = event.getLifecycle_events();
		if(eventList == null) eventList = new ArrayList<>();
		eventList.add("mmechess_relation_joined");
		eventList.add("mme_relation_joined");
		eventList.add("dns_relation_joined");
		eventList.add("bind9_relation_joined");
		eventList.add("uemm_relation_joined");
		event.setLifecycle_events(eventList);
		lifecycleEvents.add(event);
		
		event = new LifecycleEvent();
		event.setEvent(Event.INSTANTIATE);
		eventList = event.getLifecycle_events();
		if(eventList == null) eventList = new ArrayList<>();
		eventList.add("preinit.sh");
		eventList.add("configure_interfaces.sh");
		eventList.add("modify_routes.sh");
		eventList.add("enodeb_install");
		eventList.add("enodeb_install");
		event.setLifecycle_events(eventList);
		lifecycleEvents.add(event);
		
		
		event = new LifecycleEvent();
		event.setEvent(Event.START);
		eventList = event.getLifecycle_events();
		if(eventList == null) eventList = new ArrayList<>();
		eventList.add("enodeb_preStart");
		eventList.add("enodeb_start");
		event.setLifecycle_events(eventList);
		lifecycleEvents.add(event);
				
				//Initiate the Configuration Parameter
				this.configurationParameter = new HashMap<>();
				this.configurationParameter.put("var_num_intf", "3");
				this.configurationParameter.put("var_mgmt_network", "mgmt");
				this.configurationParameter.put("var_net_c_network", "net_c");
				this.configurationParameter.put("var_net_d_network", "net_d");
				this.configurationParameter.put("var_upstart", "true");
				this.configurationParameter.put("var_ue_bind_network_name", "net_c");
				this.configurationParameter.put("var_console_port", "10000");
				this.configurationParameter.put("var_range", "001011234567760:001011234567780");
				this.configurationParameter.put("var_name", "enodeb");
				this.configurationParameter.put("var_dns_intf", "mgmt");
				this.configurationParameter.put("var_use_floating", "false");
				
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

				//Initiate the vdu params
				vduSet = new HashSet<VirtualDeploymentUnit>();

					VirtualDeploymentUnit vdu = new VirtualDeploymentUnit();
					vdu.setScale_in_out(1);
					List<String> vimInstances = new ArrayList<String>();
					vimInstances.add("vim-instance");
					vdu.setVimInstanceName(vimInstances);
					// TODO set VM-Image correctly 
					Set<String> imageSet = new HashSet<String>();
					imageSet.add("Ubuntu 14.04 Cloud based");
					vdu.setVm_image(imageSet);
				
						Set<VNFComponent> vnfdSet = new HashSet<VNFComponent>();
							VNFComponent vnfComponent = new VNFComponent();
								VNFDConnectionPoint vnfdConnectionPoint = new VNFDConnectionPoint();
								Set<VNFDConnectionPoint> vnfdConnecSet = new HashSet<VNFDConnectionPoint>();
							
								// First Connection Point in above VNFComponent
								vnfdConnectionPoint.setVirtual_link_reference("mgmt");
								vnfdConnecSet.add(vnfdConnectionPoint);
								
								// Next Connection Point. And so on
								vnfdConnectionPoint = new VNFDConnectionPoint();
								vnfdConnectionPoint.setVirtual_link_reference("net_d");
								vnfdConnectionPoint.setFloatingIp("random");
								vnfdConnecSet.add(vnfdConnectionPoint);
	
								vnfdConnectionPoint = new VNFDConnectionPoint();
								vnfdConnectionPoint.setVirtual_link_reference("net_c");
								vnfdConnectionPoint.setFloatingIp("random");
								vnfdConnecSet.add(vnfdConnectionPoint);
						
						
						vnfComponent.setConnection_point(vnfdConnecSet);
						vnfdSet.add(vnfComponent);
				vdu.setVnfc(vnfdSet);
			
			vduSet.add(vdu);
			
			
			this.vimInstanceName = "vim-instance";
			
				
				//Initiate Virtual Links
			InternalVirtualLink virtualLink = new InternalVirtualLink();
			Set<String> connectionPointsSet = new HashSet<String>();
			connectionPointsSet.add("mgmt");
			connectionPointsSet.add("net_d");
			connectionPointsSet.add("net_d");
			virtualLink.setConnection_points_references(connectionPointsSet);
			
				//Initiate deployment Flavour and others
			this.deploymentFlavour = new VNFDeploymentFlavour();
			deploymentFlavour.setFlavour_key("openbaton");
			this.type = "enodeb";
			this.endpoint = "generic";
	}
	
	@Override
	public void updateInstance(Resource fivegResource) {
		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "Update gateway instance: "
					+ this.getOwningAdapter().getInstanceUri(this));
		}
		super.updateInstance(fivegResource);

	}

	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type, Fiveg.ENodeB);
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
		
		if (this.getVimInstanceName() != null && !this.getVimInstanceName().equals("") ) {
			String vimInstanceName = this.getVimInstanceName();
			resource.addLiteral(OpenBaton.vimInstanceName, vimInstanceName);
		}
		
		
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

	public ArrayList<String> getVirtualLink() {
		return virtualLink;
	}

	public void setVirtualLink(ArrayList<String> virtualLink) {
		this.virtualLink = virtualLink;
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
