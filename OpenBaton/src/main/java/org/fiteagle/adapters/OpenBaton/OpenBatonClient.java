package org.fiteagle.adapters.OpenBaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.OpenBaton.Model.DomainNameSystem;
import org.fiteagle.adapters.OpenBaton.Model.ENodeB;
import org.fiteagle.adapters.OpenBaton.Model.FiveGCore;
import org.fiteagle.adapters.OpenBaton.Model.Gateway;
import org.fiteagle.adapters.OpenBaton.Model.MME;
import org.fiteagle.adapters.OpenBaton.Model.OpenBatonService;
import org.fiteagle.adapters.OpenBaton.Model.SgwuPgwu;
import org.fiteagle.adapters.OpenBaton.Model.UE;
import org.openbaton.catalogue.mano.common.VNFDeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.InternalVirtualLink;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VNFDependency;
import org.openbaton.catalogue.mano.descriptor.VirtualLinkDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.VNFPackage;
import org.openbaton.catalogue.security.Project;
import org.openbaton.sdk.NFVORequestor;
import org.openbaton.sdk.api.exception.SDKException;
import org.openbaton.sdk.api.rest.NetworkServiceDescriptorRestAgent;
import org.openbaton.sdk.api.rest.NetworkServiceRecordRestAgent;
import org.openbaton.sdk.api.rest.VNFPackageAgent;
import org.openbaton.sdk.api.rest.VirtualLinkRestAgent;
import org.openbaton.sdk.api.rest.VirtualNetworkFunctionDescriptorRestAgent;

import com.google.common.annotations.Beta;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenBatonClient {
	private static final Logger LOGGER = Logger.getLogger(OpenBatonClient.class.toString());

	private final OpenBatonAdapter openBatonAdapter;

	private String username;
	private String password;
	private String nfvoIp;
	private String nfvoPort;
	private String version;
	private String vpnIp;
	private String vpnPort;
	private String projectId;
	
	private NetworkServiceDescriptor networkServiceDescriptor; 
	private NetworkServiceRecord networkServiceRecord;
	private HashMap<String,VirtualNetworkFunctionDescriptor> vnfdMap;
	private List<VirtualNetworkFunctionDescriptor> vnfdList;


	private NFVORequestor nfvoRequestor;
	private VirtualNetworkFunctionDescriptorRestAgent vnfdAgent;
	private NetworkServiceDescriptorRestAgent nsdAgent;
	private NetworkServiceRecordRestAgent nsrAgent;

	public OpenBatonClient(OpenBatonAdapter openBatonAdapter, String projectId) {
		this.openBatonAdapter = openBatonAdapter;
		this.projectId = projectId;
		init();
	}
	
	public OpenBatonClient(OpenBatonAdapter openBatonAdapter) {
		this.openBatonAdapter = openBatonAdapter;
		loadPreferences();
	}
	
	public OpenBatonClient(Boolean debug, String username,String password,String nfvoIp,String nfvoPort,String projectId) {
		this.openBatonAdapter = null;
		this.username = username;
		this.password = password;
		this.nfvoIp = nfvoIp;
		this.nfvoPort = nfvoPort;
		version = "1";
		this.projectId = projectId;
	}

	private void loadPreferences() {
		vnfdMap = new HashMap<>();
		try {
			username = openBatonAdapter.getUsername();
			password = openBatonAdapter.getPassword();
			nfvoIp = openBatonAdapter.getNfvoIp();
			nfvoPort = openBatonAdapter.getNfvoPort();
			version = openBatonAdapter.getVersion();
			vpnIp = openBatonAdapter.getVpnIP();
			vpnPort = openBatonAdapter.getVpnPort();
		} catch (Exception e) {
			e.printStackTrace();
		}

		 if (username == null){
		 throw new InsufficientOpenBatonPreferences("username");
		 }
		 if (password == null){
		 throw new InsufficientOpenBatonPreferences("password");
		 }
		if (nfvoIp == null) {
			throw new InsufficientOpenBatonPreferences("nfvoIp");
		}
		if (nfvoPort == null) {
			throw new InsufficientOpenBatonPreferences("nfvoPort");
		}
		if (version == null) {
			throw new InsufficientOpenBatonPreferences("version");
		}
		// if (vpnIp == null){
		// throw new InsufficientOpenBatonPreferences("vpnIp");
		// }
		// if (vpnPort == null){
		// throw new InsufficientOpenBatonPreferences("vpnPort");
		// }
	}

	public void init() {
		loadPreferences();
		checkRequestor();
	}

	private void checkRequestor() {
		if (nfvoRequestor == null) {
			nfvoRequestor = new NFVORequestor(username, password, projectId, false, nfvoIp, nfvoPort, version);
			nsdAgent = nfvoRequestor.getNetworkServiceDescriptorAgent();
			vnfdAgent = nfvoRequestor.getVirtualNetworkFunctionDescriptorAgent();
			nsrAgent = nfvoRequestor.getNetworkServiceRecordAgent();
		}
	}
	
	private void checkRequestorWithoutId() {
		if (nfvoRequestor == null) {
			nfvoRequestor = new NFVORequestor(username, password, null, false, nfvoIp, nfvoPort, version);
		}
	}

	

	public NetworkServiceRecord createNetworkServiceRecord(){

			createNetworkServiceDescriptor(networkServiceDescriptor);
			networkServiceRecord = createNetworkServiceRecord(networkServiceDescriptor.getId());
			
			return networkServiceRecord;
	}
	
	public NetworkServiceRecord createNetworkServiceRecord(String nsdId) {
		try {
			networkServiceRecord = nsrAgent.create(nsdId);
			return networkServiceRecord;
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public NetworkServiceDescriptor createLocalNetworkServiceDescriptor (){
	networkServiceDescriptor = new NetworkServiceDescriptor();
		return networkServiceDescriptor;
		
	}
	
	public NetworkServiceDescriptor createNetworkServiceDescriptor(NetworkServiceDescriptor nsd) {
		checkRequestor();
		addVirtualLinksToTheNsd();
			try {
				networkServiceDescriptor = nsdAgent.create(nsd);
				return networkServiceDescriptor;
			} catch (SDKException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	
	}
	
	private void addVirtualLinksToTheNsd() {
		Set<VirtualLinkDescriptor> virtualLinkSet = networkServiceDescriptor.getVld();
		if(virtualLinkSet == null) virtualLinkSet = new HashSet<VirtualLinkDescriptor>();
		
		if(vnfdMap == null){
			updateVnfdsList();
			for(VirtualNetworkFunctionDescriptor s : vnfdList){
				Set<InternalVirtualLink> vlList = s.getVirtual_link();
				if(vlList != null){
					for(InternalVirtualLink v : vlList){
						VirtualLinkDescriptor tmpVld = new VirtualLinkDescriptor();
						tmpVld.setName(v.getName());
						virtualLinkSet.add(tmpVld);
					}	
				}
			}
			networkServiceDescriptor.setVld(virtualLinkSet);
			}else{
				for(String s : vnfdMap.keySet()){
					Set<InternalVirtualLink> vlList = vnfdMap.get(s).getVirtual_link();
					if(vlList != null){
					for(InternalVirtualLink v : vlList){
						VirtualLinkDescriptor tmpVld = new VirtualLinkDescriptor();
						tmpVld.setName(v.getName());
						virtualLinkSet.add(tmpVld);
					}
					
				}
				}
				networkServiceDescriptor.setVld(virtualLinkSet);
			}
	}

	private void updateVnfdsList() {
		try {
			vnfdList = vnfdAgent.findAll();
		} catch (ClassNotFoundException | SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public NetworkServiceDescriptor createNetworkServiceDescriptor() {
		checkRequestor();
		addVirtualLinksToTheNsd();

			try {
				networkServiceDescriptor = nsdAgent.create(networkServiceDescriptor);
				return networkServiceDescriptor;
			} catch (SDKException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	
	}
	
	public NetworkServiceDescriptor createNetworkServiceDescriptor(OpenBatonService openBaton) {
		checkRequestor();
		NetworkServiceDescriptor networkDescriptor = new NetworkServiceDescriptor();
		networkDescriptor.setEnabled(true);
		networkDescriptor.setVendor("fokus");
		if (openBaton != null) {
			networkDescriptor.setVersion(openBaton.getServiceContainer().getVersion());
			networkDescriptor.setName(openBaton.getServiceContainer().getContainerName());
			networkDescriptor.setVnfd(null);
		} else {
			LOGGER.log(Level.WARNING, "Creating NetworkServiceDescriptor with null Object - Using default values");
			networkDescriptor.setVersion("0.1");
			networkDescriptor.setName("defaultName-" + new Random().nextInt());
			networkDescriptor.setVnfd(null);
		}

		try {
			NetworkServiceDescriptor createdNSD = nfvoRequestor.getNetworkServiceDescriptorAgent()
					.create(networkDescriptor);
			setNetworkServiceDescriptor(createdNSD);
			return createdNSD;
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public String createNewProjectOnServer(){
		checkRequestor();
		Project project = new Project();
		project.setName("Test" + new Random().nextInt());
		try {
			Project response = nfvoRequestor.getProjectAgent().create(project);
			return response.getId();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Beta
	public NetworkServiceDescriptor updateNetworkServiceDescriptor(NetworkServiceDescriptor newNsd, String id) {
		checkRequestor();

		try {
			NetworkServiceDescriptor updatedNSD = nsdAgent.update(newNsd, id);
			networkServiceDescriptor = updatedNSD;
			return updatedNSD;
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public NetworkServiceDescriptor getNetworkServiceDescriptor(String nsdId) {
		checkRequestor();
		NetworkServiceDescriptor nsd;
		try {
			nsd = nfvoRequestor.getNetworkServiceDescriptorAgent().findById(nsdId);
			return nsd;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param openBaton
	 * @param vnfSet
	 * @return A created NSD with the created and linked Instances of the given
	 *         vnfSet
	 */
	@Beta
	public NetworkServiceDescriptor createNetworkServiceDescriptor(OpenBatonService openBaton,
			Set<VirtualNetworkFunctionDescriptor> vnfSet) {
		checkRequestor();
		NetworkServiceDescriptor networkDescriptor = new NetworkServiceDescriptor();
		networkDescriptor.setEnabled(true);
		networkDescriptor.setVendor("fokus");
		if (openBaton != null) {
			networkDescriptor.setVersion(openBaton.getServiceContainer().getVersion());
			networkDescriptor.setName(openBaton.getServiceContainer().getContainerName());
			networkDescriptor.setVnfd(null);
		} else {
			LOGGER.log(Level.WARNING, "Creating NetworkServiceDescriptor with null Object - Using default values");
			networkDescriptor.setVersion("0.1");
			networkDescriptor.setName("defaultName-" + new Random().nextInt());
			networkDescriptor.setVnfd(vnfSet);
		}

		try {
			NetworkServiceDescriptor createdNSD = nfvoRequestor.getNetworkServiceDescriptorAgent()
					.create(networkDescriptor);
			return createdNSD;
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	

	public void createVirtualNetworkFunctionPackage() {

	}

	public void createPoPInstance() {

	}

	public List<NetworkServiceDescriptor> getAllNSDs() {
		NetworkServiceDescriptorRestAgent nsdAgend = nfvoRequestor.getNetworkServiceDescriptorAgent();
		List<NetworkServiceDescriptor> tmpList = null;
		try {
			tmpList = nsdAgend.findAll();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmpList;
	}

	public List<VirtualLinkDescriptor> getAllVnfDescriptor() {
		VirtualLinkRestAgent nsdAgend = nfvoRequestor.getVirtualLinkAgent();
		List<VirtualLinkDescriptor> tmpList = null;
		try {
			tmpList = nsdAgend.findAll();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmpList;

	}

	public void getAllVnfManagers() {

	}

	public List<NetworkServiceRecord> getAllNSRs() {
		this.checkRequestor();
		try {
			return nsrAgent.findAll();
		} catch (ClassNotFoundException | SDKException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Beta
	public NetworkServiceRecord updateNetworkServiceRecord(NetworkServiceRecord nsr) {
		checkRequestor();
		try {
			NetworkServiceRecord updatedNSR = nsrAgent.findById(nsr.getId());
			return updatedNSR;
		} catch (SDKException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class InsufficientOpenBatonPreferences extends RuntimeException {

		private static final long serialVersionUID = 6511540487288262809L;

		public InsufficientOpenBatonPreferences(String preferenceName) {
			super("Please set the preference: " + preferenceName);
		}
	}

	public String uploadPackageToDatabase(String fileNameWithDirectory) {
		checkRequestor();
		try {
			VNFPackageAgent agent = nfvoRequestor.getVNFPackageAgent();
			VNFPackage createdPackage = agent.create(fileNameWithDirectory);
			String fileName = null ;
			for(VirtualNetworkFunctionDescriptor vnfd : vnfdAgent.findAll()){
				if (vnfd.getVnfPackageLocation().equals(createdPackage.getId())){
					String[] nameArray = fileNameWithDirectory.split("/");
					fileName =  nameArray[nameArray.length -1 ];
					vnfdMap.put(fileName, vnfd);
				}
			}
			
			return vnfdMap.get(fileName).getId();
			} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
		
	}
	
	
	public void addVnfdToNsd(VirtualNetworkFunctionDescriptor virtualNetworkFunctionDescriptor){
		checkRequestor();
		networkServiceDescriptor.getVnfd().add(virtualNetworkFunctionDescriptor);
		updateNetworkServiceDescriptor(networkServiceDescriptor, networkServiceDescriptor.getId());
	}
	
	public void addVnfdToLocalNsd(VirtualNetworkFunctionDescriptor virtualNetworkFunctionDescriptor){
		checkRequestor();
		networkServiceDescriptor.getVnfd().add(virtualNetworkFunctionDescriptor);
	}
	
	public void addVnfdToNsd(Resource vnfResource){
		checkRequestor();
		
		
		VirtualNetworkFunctionDescriptor foundVnfd = null;

			StmtIterator propertyIterator = vnfResource.listProperties(RDF.type);
			for(Statement p : propertyIterator.toList()){
				String uri = null;
				if(p.getObject().isLiteral()) {uri = p.getObject().asLiteral().getString();}
				if(p.getObject().isResource()) {uri = p.getObject().asResource().getURI();}
				String[] tmpArray = uri.split("#");
				uri = tmpArray[1];
				
				if (vnfdMap.containsKey(uri)){
					VirtualNetworkFunctionDescriptor tmpVnf = vnfdMap.get(uri);
					foundVnfd = new VirtualNetworkFunctionDescriptor();
					foundVnfd.setId(tmpVnf.getId());
					
				}
			}

		if(foundVnfd != null){
			networkServiceDescriptor.getVnfd().add(foundVnfd);
		}else{
            LOGGER.log(Level.SEVERE, "Could not found correct VNFD in List");
		}
//		updateNetworkServiceDescriptor(networkServiceDescriptor, networkServiceDescriptor.getId());
	}

	public NetworkServiceDescriptor getNetworkServiceDescriptor() {
		return networkServiceDescriptor;
	}

	public NetworkServiceRecord getNetworkServiceRecord() {
		return networkServiceRecord;
	}

	public void setNetworkServiceRecord(NetworkServiceRecord networkServiceRecord) {
		this.networkServiceRecord = networkServiceRecord;
	}

	public void setNetworkServiceDescriptor(NetworkServiceDescriptor networkServiceDescriptor) {
		this.networkServiceDescriptor = networkServiceDescriptor;
	}
	
	/**
	 * The "Create*"-methods have all the same Structure (except the FiveGCore).
	 * Will be fused in to one Method, but was easier for now and made less
	 * problems
	 * 
	 * Sometimes there are 2 different create methods for the same type of
	 * Object. That is if you want to create an Instance and instant add it to
	 * an existing NetworkServiceDescriptor or if you want just the Object
	 * without added NSD
	 */

	public VirtualNetworkFunctionDescriptor createENodeB(OpenBatonService openBaton, String nsdID) {
		checkRequestor();
		ENodeB eNodeB = (ENodeB) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		/**
		 * Setting all relevant parts in the Vnfd-Object from the given EnodeB
		 * Object For detailed Information pls look into the MME class
		 */
		tmpVnfd.setConfigurations(eNodeB.getConfiguration());

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(eNodeB.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(eNodeB.getEndpoint());

		tmpVnfd.setLifecycle_event(eNodeB.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		// At the moment the name of the instance is just generated randomly
		tmpVnfd.setName(eNodeB.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(eNodeB.getType());
		tmpVnfd.setVendor(eNodeB.getVendor());
		tmpVnfd.setVersion(eNodeB.getVersion());
		tmpVnfd.setVdu(eNodeB.getVduSet());
		tmpVnfd.setVirtual_link(eNodeB.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdENodeB;

			/**
			 * Trying to create the Vnfd on the Server Method is deprecated but
			 * started to work with it and is still working
			 */
			if (nsdID != null) {
				createdENodeB = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

			} else {
				createdENodeB = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
			}
			LOGGER.log(Level.SEVERE, "MME CREATED");
			return createdENodeB;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public VirtualNetworkFunctionDescriptor createMME(OpenBatonService openBaton, String nsdID) {
		checkRequestor();
		MME mme = (MME) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(mme.getConfiguration());

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(mme.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(mme.getEndpoint());

		tmpVnfd.setLifecycle_event(mme.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(mme.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(mme.getType());
		tmpVnfd.setVendor(mme.getVendor());
		tmpVnfd.setVersion(mme.getVersion());
		tmpVnfd.setVdu(mme.getVduSet());
		tmpVnfd.setVirtual_link(mme.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdMME;
			if (nsdID != null) {
				createdMME = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

			} else {
				createdMME = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
			}
			LOGGER.log(Level.SEVERE, "MME CREATED");
			return createdMME;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public VirtualNetworkFunctionDescriptor createMME(OpenBatonService openBaton) {
		checkRequestor();
		MME mme = (MME) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(mme.getConfiguration());

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(mme.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(mme.getEndpoint());

		tmpVnfd.setLifecycle_event(mme.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(mme.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(mme.getType());
		tmpVnfd.setVendor(mme.getVendor());
		tmpVnfd.setVersion(mme.getVersion());
		tmpVnfd.setVdu(mme.getVduSet());
		tmpVnfd.setVirtual_link(mme.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdMME = nfvoRequestor.getNetworkServiceDescriptorAgent()
					.createVNFD(null, tmpVnfd);
			LOGGER.log(Level.SEVERE, "MME CREATED");
			return createdMME;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public VirtualNetworkFunctionDescriptor createDomainNameSystem(OpenBatonService openBaton, String nsdID) {
		checkRequestor();
		DomainNameSystem dns = (DomainNameSystem) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(dns.getConfiguration());

		// TODO set floatingIp & virtual_link_reference from JSON?
		// tmpVnfd.setConnection_point(null);

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(dns.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(dns.getEndpoint());

		tmpVnfd.setLifecycle_event(dns.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(dns.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(dns.getType());
		tmpVnfd.setVendor(dns.getVendor());
		tmpVnfd.setVersion(dns.getVersion());
		tmpVnfd.setVdu(dns.getVduSet());
		tmpVnfd.setVirtual_link(dns.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdDNS;
			if (nsdID != null) {
				createdDNS = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);
			} else {
				createdDNS = vnfdAgent.create(tmpVnfd);
			}
			LOGGER.log(Level.SEVERE, "MME CREATED");
			return createdDNS;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public VirtualNetworkFunctionDescriptor createGateway(OpenBatonService openBaton, String nsdID) {
		checkRequestor();
		Gateway gw = (Gateway) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(gw.getConfiguration());

		// TODO set floatingIp & virtual_link_reference from JSON?
		// tmpVnfd.setConnection_point(null);

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(gw.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(gw.getEndpoint());

		tmpVnfd.setLifecycle_event(gw.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(gw.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(gw.getType());
		tmpVnfd.setVendor(gw.getVendor());
		tmpVnfd.setVersion(gw.getVersion());
		tmpVnfd.setVdu(gw.getVduSet());
		tmpVnfd.setVirtual_link(gw.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdGateway;
			if (nsdID != null) {
				createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

			} else {
				createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
			}
			LOGGER.log(Level.SEVERE, "MME CREATED");
			return createdGateway;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void createFiveGCore(OpenBatonService openBaton) {
		checkRequestor();
		FiveGCore fiveG = (FiveGCore) openBaton;
		NetworkServiceDescriptor nsd = null;
		List<NetworkServiceDescriptor> nsdList = getAllNSDs();
		for (NetworkServiceDescriptor n : nsdList) {
			if (n.getName().contains("5G") && n.getName().contains("Core")) {
				nsd = n;
				LOGGER.log(Level.SEVERE, "FOUND NSD");
			}
		}
		NetworkServiceRecordRestAgent agent = nfvoRequestor.getNetworkServiceRecordAgent();
		try {
			NetworkServiceRecord newNsRecord = agent.create(nsd.getId());
			String test;
			test = "";
			test = "";
			test = "";
			test = "";
			test = "";
			test = "";
			test = "";

			fiveG.setNsr(newNsRecord);

			List<VirtualNetworkFunctionRecord> vnfrList = agent.getVirtualNetworkFunctionRecords(newNsRecord.getId());
			LOGGER.log(Level.SEVERE, vnfrList.toString());
			LOGGER.log(Level.SEVERE, "test");

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	public VirtualNetworkFunctionDescriptor createUe(OpenBatonService openBaton, String nsdID) {
		checkRequestor();
		UE ue = (UE) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(ue.getConfiguration());

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(ue.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(ue.getEndpoint());

		tmpVnfd.setLifecycle_event(ue.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(ue.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(ue.getType());
		tmpVnfd.setVendor(ue.getVendor());
		tmpVnfd.setVersion(ue.getVersion());
		tmpVnfd.setVdu(ue.getVduSet());
		tmpVnfd.setVirtual_link(ue.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdGateway;
			if (nsdID != null) {
				createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

			} else {
				createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
			}
			LOGGER.log(Level.SEVERE, "UE CREATED");
			return createdGateway;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public VirtualNetworkFunctionDescriptor createSgwuPgwu(OpenBatonService openBaton, String nsdID) {
		checkRequestor();
		SgwuPgwu sgwupgwu = (SgwuPgwu) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(sgwupgwu.getConfiguration());

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(sgwupgwu.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(sgwupgwu.getEndpoint());

		tmpVnfd.setLifecycle_event(sgwupgwu.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(sgwupgwu.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(sgwupgwu.getType());
		tmpVnfd.setVendor(sgwupgwu.getVendor());
		tmpVnfd.setVersion(sgwupgwu.getVersion());
		tmpVnfd.setVdu(sgwupgwu.getVduSet());
		tmpVnfd.setVirtual_link(sgwupgwu.getVirtualLinkSet());

		try {
			VirtualNetworkFunctionDescriptor createdGateway;
			if (nsdID != null) {
				createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

			} else {
				createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
			}
			LOGGER.log(Level.SEVERE, "SgwuPgwu CREATED");
			return createdGateway;

		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Instead of creating VNFs in each "create*"-method this method will be
	 * used in the future
	 */
	public VirtualNetworkFunctionDescriptor getVnfdFromResource(OpenBatonService openBaton) {
		MME mme = (MME) openBaton;
		VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();

		tmpVnfd.setConfigurations(mme.getConfiguration());

		Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
		flavourSet.add(mme.getDeploymentFlavour());
		tmpVnfd.setDeployment_flavour(flavourSet);

		tmpVnfd.setEndpoint(mme.getEndpoint());
		tmpVnfd.setLifecycle_event(mme.getLifecycleEvents());
		tmpVnfd.setCyclicDependency(true);
		tmpVnfd.setHb_version(2);

		tmpVnfd.setName(mme.getServiceName() + new Random().nextInt());

		tmpVnfd.setType(mme.getType());
		tmpVnfd.setVendor(mme.getVendor());
		tmpVnfd.setVersion(mme.getVersion());
		tmpVnfd.setVdu(mme.getVduSet());
		tmpVnfd.setVirtual_link(mme.getVirtualLinkSet());

		return tmpVnfd;
	}
	
	
//	public void createFiveGCoreWithNewNsd(OpenBatonService openBaton) {
//		// Creating the local Objects of the soon to provisioned Instances
//		MME mme = new MME(null, null);
//		ENodeB enodeb = new ENodeB(null, null);
//		Gateway gw = new Gateway(null, null);
//		DomainNameSystem dns = new DomainNameSystem(null, null);
//		UE ue = new UE(null, null);
//		SgwuPgwu sgwupgwu = new SgwuPgwu(null, null);
//
//		// Save the Instances in the FiveGCore
//		FiveGCore fiveG = (FiveGCore) openBaton;
//		fiveG.setDns(dns);
//		fiveG.setEnodeb(enodeb);
//		fiveG.setGw(gw);
//		fiveG.setMme(mme);
//		fiveG.setUe(ue);
//		fiveG.setSgwuPgwu(sgwupgwu);
//
//		// Creating empty NetworkServiceDescriptor
//		NetworkServiceDescriptor nsd = createNetworkServiceDescriptor(null);
//
//		// Creating VNFs and adding them to the created NSD
//		VirtualNetworkFunctionDescriptor vnfMME = createMME(mme, nsd.getId());
//		VirtualNetworkFunctionDescriptor vnfdns = createDomainNameSystem(dns, nsd.getId());
//		VirtualNetworkFunctionDescriptor vnfenodeB = createENodeB(enodeb, nsd.getId());
//		VirtualNetworkFunctionDescriptor vnfGw = createGateway(gw, nsd.getId());
//		VirtualNetworkFunctionDescriptor vnfUe = createUe(ue, nsd.getId());
//		VirtualNetworkFunctionDescriptor vnfSgwuPgwu = createSgwuPgwu(sgwupgwu, nsd.getId());
//
//		// Also saving the VNFDs in the FiveGCore Object
//		fiveG.setDnsVnf(vnfdns);
//		fiveG.setEnodebVnf(vnfenodeB);
//		fiveG.setGwVnf(vnfGw);
//		fiveG.setMmeVnf(vnfMME);
//		fiveG.setUeVnf(vnfUe);
//		fiveG.setSgwupgwuVnf(vnfSgwuPgwu);
//
//		/**
//		 * Setting the intern dependencys of the Instances(MME,EnodeB...) in the
//		 * NetworkServiceDescriptor(FiveGCore) It's always: Source -> Target [+
//		 * some configuration parameters you may set for this Dependency] These
//		 * are the communicationlines you see when using "Show Graph" in the
//		 * OpenBaton-Gui
//		 */
//		Set<VNFDependency> vnfDependencysSet = new HashSet<VNFDependency>();
//		Set<String> parameterSet = new HashSet<String>();
//		VNFDependency vnfDependency = new VNFDependency();
//		vnfDependency.setSource(vnfenodeB);
//		vnfDependency.setTarget(vnfUe);
//		parameterSet.add("var_net_c_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_c");
//		parameterSet.add("net_c_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfenodeB);
//		vnfDependency.setTarget(vnfMME);
//		parameterSet.add("var_net_c_network");
//		parameterSet.add("var_net_d_network");
//		parameterSet.add("var_name");
//		parameterSet.add("var_range");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_c");
//		parameterSet.add("net_c_floatingIp");
//		parameterSet.add("net_d");
//		parameterSet.add("net_d_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfdns);
//		vnfDependency.setTarget(vnfUe);
//		parameterSet.add("realm");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfdns);
//		vnfDependency.setTarget(vnfMME);
//		parameterSet.add("realm");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfdns);
//		vnfDependency.setTarget(vnfenodeB);
//		parameterSet.add("realm");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfGw);
//		vnfDependency.setTarget(vnfSgwuPgwu);
//		vnfDependencysSet.add(vnfDependency);
//		parameterSet.add("var_net_a_network");
//		parameterSet.add("net_a");
//		parameterSet.add("net_a_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfMME);
//		vnfDependency.setTarget(vnfenodeB);
//		vnfDependencysSet.add(vnfDependency);
//		parameterSet.add("var_net_d_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_d");
//		parameterSet.add("net_d_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfMME);
//		vnfDependency.setTarget(vnfSgwuPgwu);
//		vnfDependencysSet.add(vnfDependency);
//		parameterSet.add("var_ofp_transport");
//		parameterSet.add("var_ofp_port");
//		parameterSet.add("var_mgmt_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_d");
//		parameterSet.add("net_d_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfMME);
//		vnfDependency.setTarget(vnfdns);
//		parameterSet.add("var_name");
//		parameterSet.add("var_mgmt_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfSgwuPgwu);
//		vnfDependency.setTarget(vnfMME);
//		parameterSet.add("var_net_a_network");
//		parameterSet.add("var_net_d_network");
//		parameterSet.add("var_datapath_id");
//		parameterSet.add("net_a");
//		parameterSet.add("net_d");
//		parameterSet.add("net_d_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfSgwuPgwu);
//		vnfDependency.setTarget(vnfGw);
//		parameterSet.add("var_net_a_network");
//		parameterSet.add("var_static_num");
//		parameterSet.add("net_a");
//		parameterSet.add("net_a_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//		vnfDependencysSet.add(vnfDependency);
//
//		vnfDependency = new VNFDependency();
//		parameterSet = new HashSet<String>();
//		vnfDependency.setSource(vnfUe);
//		vnfDependency.setTarget(vnfenodeB);
//		parameterSet.add("var_net_c_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_c");
//		parameterSet.add("net_c_floatingIp");
//		vnfDependency.setParameters(parameterSet);
//		vnfDependencysSet.add(vnfDependency);
//		vnfDependencysSet.add(vnfDependency);
//
//		// refresh the lokal NSD object
//		nsd = getNetworkServiceDescriptor(nsd.getId());
//		// Set the above parameters
//		nsd.setVnf_dependency(vnfDependencysSet);
//
//		// VirtualLinkDescriptors for 5G-Core (more parameters)
//		Set<VirtualLinkDescriptor> vldSet = new HashSet<VirtualLinkDescriptor>();
//		VirtualLinkDescriptor vld = new VirtualLinkDescriptor();
//		vld.setName("mgmt");
//		vldSet.add(vld);
//
//		vld = new VirtualLinkDescriptor();
//		vld.setName("net_a");
//		vldSet.add(vld);
//
//		vld = new VirtualLinkDescriptor();
//		vld.setName("net_c");
//		vldSet.add(vld);
//
//		vld = new VirtualLinkDescriptor();
//		vld.setName("net_d");
//		vldSet.add(vld);
//
//		nsd.setVld(vldSet);
//
//		// Update the NetworkServiceDescriptor on the Server with new
//		// Dependencys and VLDs
//		NetworkServiceDescriptor updatedNSD = updateNetworkServiceDescriptor(nsd, nsd.getId());
//
//		// Updating the local NSD in the FiveGCore Object
//		fiveG.setNsd(updatedNSD);
//
//	}

}
