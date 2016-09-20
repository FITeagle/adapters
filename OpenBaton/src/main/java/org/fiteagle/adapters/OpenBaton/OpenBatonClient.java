package org.fiteagle.adapters.OpenBaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.atlas.lib.RandomLib;
import org.fiteagle.adapters.OpenBaton.Model.DomainNameSystem;
import org.fiteagle.adapters.OpenBaton.Model.ENodeB;
import org.fiteagle.adapters.OpenBaton.Model.FiveGCore;
import org.fiteagle.adapters.OpenBaton.Model.Gateway;
import org.fiteagle.adapters.OpenBaton.Model.MME;
import org.fiteagle.adapters.OpenBaton.Model.OpenBatonService;
import org.fiteagle.adapters.OpenBaton.Model.SgwuPgwu;
import org.fiteagle.adapters.OpenBaton.Model.UE;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor;
import org.openbaton.catalogue.mano.common.VNFDeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.InternalVirtualLink;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VNFDependency;
import org.openbaton.catalogue.mano.descriptor.VirtualLinkDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.VNFPackage;
import org.openbaton.catalogue.security.Key;
import org.openbaton.catalogue.security.Project;
import org.openbaton.sdk.NFVORequestor;
import org.openbaton.sdk.api.exception.SDKException;
import org.openbaton.sdk.api.rest.KeyAgent;
import org.openbaton.sdk.api.rest.NetworkServiceDescriptorRestAgent;
import org.openbaton.sdk.api.rest.NetworkServiceRecordRestAgent;
import org.openbaton.sdk.api.rest.VNFPackageAgent;
import org.openbaton.sdk.api.rest.VirtualLinkRestAgent;
import org.openbaton.sdk.api.rest.VirtualNetworkFunctionDescriptorRestAgent;

import com.google.common.annotations.Beta;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

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
	private HashMap<String,Key> keyMap = new HashMap<>();



	private NFVORequestor nfvoRequestor;
	private VirtualNetworkFunctionDescriptorRestAgent vnfdAgent;
	private NetworkServiceDescriptorRestAgent nsdAgent;
	private NetworkServiceRecordRestAgent nsrAgent;
	private KeyAgent keyAgent;

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

	public void checkRequestor() {
		if (nfvoRequestor == null) {
			nfvoRequestor = new NFVORequestor(username, password, projectId, false, nfvoIp, nfvoPort, version);
			nsdAgent = nfvoRequestor.getNetworkServiceDescriptorAgent();
			vnfdAgent = nfvoRequestor.getVirtualNetworkFunctionDescriptorAgent();
			nsrAgent = nfvoRequestor.getNetworkServiceRecordAgent();
			keyAgent = nfvoRequestor.getKeyAgent();
		}
		if(!nfvoRequestor.getProjectId().equals(projectId) || !nsdAgent.getProjectId().equals(projectId) || !vnfdAgent.getProjectId().equals(projectId) || !nsrAgent.getProjectId().equals(projectId) || !keyAgent.getProjectId().equals(projectId)){
			nfvoRequestor = null;
			nfvoRequestor = new NFVORequestor(username, password, projectId, false, nfvoIp, nfvoPort, version);
			nfvoRequestor.setProjectId(projectId);
			nsdAgent = nfvoRequestor.getNetworkServiceDescriptorAgent();
			nsdAgent.setProjectId(projectId);
			vnfdAgent = nfvoRequestor.getVirtualNetworkFunctionDescriptorAgent();
			vnfdAgent.setProjectId(projectId);
			nsrAgent = nfvoRequestor.getNetworkServiceRecordAgent();
			nsrAgent.setProjectId(projectId);
			keyAgent = nfvoRequestor.getKeyAgent();
			keyAgent.setProjectId(projectId);

		}		
	}
	
	private void checkRequestorWithoutId() {
		if (nfvoRequestor == null) {
			nfvoRequestor = new NFVORequestor(username, password, null, false, nfvoIp, nfvoPort, version);
		}
	}

	public Key uploadSshKey(String experimenterName, String publicKey){
		try {
			if(!checkIfPubkeyAllreadyExists(experimenterName)){
				Key key = keyAgent.importKey(experimenterName, publicKey);
				keyMap.put(key.getName(), key);
				return key;
			}else{
				return keyMap.get(experimenterName);
			}
			
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public boolean checkIfPubkeyAllreadyExists(String experimenterName){
		
		 try {
			List<Key> keyList = keyAgent.findAll();
			for(Key k : keyList){
				keyMap.put(k.getName(), k);
			}
			return keyMap.containsKey(experimenterName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public NetworkServiceRecord createNsdAndNsr(){

			createNetworkServiceDescriptor();
			networkServiceRecord = createNetworkServiceRecord(networkServiceDescriptor.getId());
			
			return networkServiceRecord;
	}
	
	public NetworkServiceRecord createNsdAndNsr(String experimenterKeyName){

		createNetworkServiceDescriptor();
		networkServiceRecord = createNetworkServiceRecord(networkServiceDescriptor.getId(),experimenterKeyName);
		
		return networkServiceRecord;
}
	
	public NetworkServiceRecord createNetworkServiceRecord(String nsdId) {
		try {
			ArrayList<String> keyArray = new ArrayList<>();
//			keyArray.add(e)
			networkServiceRecord = nsrAgent.create(nsdId, null, keyArray , null);
			return networkServiceRecord;
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public NetworkServiceRecord createNetworkServiceRecord(String nsdId, String experimenterKeyName) {
		try {
			ArrayList<String> keyArray = new ArrayList<>();
			keyArray.add(experimenterKeyName);
			networkServiceRecord = nsrAgent.create(nsdId, null, keyArray , null);
			return networkServiceRecord;
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public NetworkServiceDescriptor createLocalNetworkServiceDescriptor (){
	networkServiceDescriptor = new NetworkServiceDescriptor();
	networkServiceDescriptor.setName("Test-"+  new Random().nextInt());
	networkServiceDescriptor.setVendor("vendor");
	networkServiceDescriptor.setEnabled(true);
	networkServiceDescriptor.setVersion("demo");

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
	
	private void addVirtualLinksToTheNsd() {
		Set<VirtualLinkDescriptor> virtualLinkSet = networkServiceDescriptor.getVld();
		if(virtualLinkSet == null) virtualLinkSet = new HashSet<VirtualLinkDescriptor>();
		HashMap<String,VirtualLinkDescriptor> virtualLinkMap = new HashMap<String,VirtualLinkDescriptor>();
		
		for(VirtualLinkDescriptor vld : virtualLinkSet){
			virtualLinkMap.put(vld.getName(), vld);
		}
		
		if(vnfdMap == null){
			updateVnfdsList();
			for(VirtualNetworkFunctionDescriptor s : vnfdList){
				Set<InternalVirtualLink> vlList = s.getVirtual_link();
				if(vlList != null){
					for(InternalVirtualLink v : vlList){
						VirtualLinkDescriptor tmpVld = new VirtualLinkDescriptor();
						tmpVld.setName(v.getName());
						if(!virtualLinkMap.containsKey(tmpVld.getName())){
							virtualLinkSet.add(tmpVld);
							virtualLinkMap.put(tmpVld.getName(), tmpVld);
						}
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
						if(!virtualLinkMap.containsKey(tmpVld.getName())){
							virtualLinkSet.add(tmpVld);
							virtualLinkMap.put(tmpVld.getName(), tmpVld);
						}
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
		project.setId("123456");
		try {
			Project response = nfvoRequestor.getProjectAgent().create(project);
			return response.getId();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String createNewProjectOnServer(String projectName){
		checkRequestor();
		Project project = new Project();
		project.setName(projectName);
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
		
		try {
			NetworkServiceDescriptor nsd = nsdAgent.findById(nsdId);
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

	public void stopNetworkServiceRecord(){
		try {
			nsrAgent.delete(networkServiceRecord.getId());
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopNetworkServiceRecord(String nsrId){
		try {
			nsrAgent.delete(nsrId);
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteNetworkServiceDescriptor(String nsrId){
		try {
			nsdAgent.delete(nsrId);
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteNetworkServiceDescriptor(){
		try {
			nsdAgent.delete(networkServiceDescriptor.getId());
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
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
	
	public List<Project> getAllProjectsFromServer(){
		try {
			return nfvoRequestor.getProjectAgent().findAll();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	@Beta
	public NetworkServiceRecord updateNetworkServiceRecord(NetworkServiceRecord nsr) {
		checkRequestor();
		try {
			networkServiceRecord = nsrAgent.findById(nsr.getId());
			return networkServiceRecord;
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
			VNFPackageAgent agent = this.nfvoRequestor.getVNFPackageAgent();
			VNFPackage createdPackage = agent.create(fileNameWithDirectory);
			String fileName = null ;
			for(VirtualNetworkFunctionDescriptor vnfd : vnfdAgent.findAll()){
				if (vnfd.getVnfPackageLocation().equals(createdPackage.getId())){
					String[] nameArray = fileNameWithDirectory.split("/");
					fileName =  nameArray[nameArray.length -1 ];
//					vnfd.setName(fileName);
					vnfdMap.put(fileName, vnfd);
					break;
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
		String foundVnfdUri = null; 
		
			// List all Statements with the Property "type"
			StmtIterator propertyIterator = vnfResource.listProperties(RDF.type);
			for(Statement p : propertyIterator.toList()){
				String uri = null;
				if(p.getObject().isLiteral()) {uri = p.getObject().asLiteral().getString();}
				if(p.getObject().isResource()) {uri = p.getObject().asResource().getURI();}
				String[] tmpArray = uri.split("#");
				String name = tmpArray[1];
				
				//If the Objectname of the Statement is in our vnfd-Map -> set foundVnfd
				if (vnfdMap.containsKey(name)){
					VirtualNetworkFunctionDescriptor tmpVnf = vnfdMap.get(name);
					foundVnfd = new VirtualNetworkFunctionDescriptor();
					foundVnfd.setId(tmpVnf.getId());
				}
				// If the slivertype was not set correctly and type is raw-pc, try to find the real type in Database
				else if (name.equals("raw-pc")) {
					Statement tmpStatement = vnfResource.getProperty(Omn_lifecycle.hasComponentID);
					String componentURI = null;
					if(tmpStatement.getObject().isLiteral()) {componentURI = tmpStatement.getObject().asLiteral().getString();}
					if(tmpStatement.getObject().isResource()) {componentURI = tmpStatement.getObject().asResource().getURI();}
					
					String componentName = null;
					tmpArray = componentURI.split("\\+");
					componentName = tmpArray[tmpArray.length-1];
					
					Model vnfd = TripletStoreAccessor.getResource(Omn.NS + componentName);
					//If this resource exists , try to find the vnfd-id
					if(vnfd != null){
						if(vnfd.contains(null, Omn_lifecycle.hasID)){
							String vnfdId = vnfd.getProperty(null, Omn_lifecycle.hasID).getObject().asLiteral().getString();
//							String vnfdId = vnfd.listProperties(Omn_lifecycle.hasID).next().getObject().asLiteral().toString();
				            VirtualNetworkFunctionDescriptor tmpVnfd = getVirtualNetworkFunctionDescriptor(vnfdId);
				            if(tmpVnfd != null){
				            	vnfdMap.put(Omn.NS+componentName, tmpVnfd);	
				            	foundVnfd = new VirtualNetworkFunctionDescriptor();
								foundVnfd.setId(tmpVnfd.getId());
				            }
						}
					}
					
				}
				//If it was not found in the map, check RDF-Database for the vnfd
				else{
					Model vnfd = TripletStoreAccessor.getResource(uri);
					if(vnfd.contains(null, Omn_lifecycle.hasID)){
						String vnfdId = vnfd.getProperty(null, Omn_lifecycle.hasID).getObject().asLiteral().getString();
//						String vnfdId = vnfd.listProperties(Omn_lifecycle.hasID).next().getObject().asLiteral().toString();
			            VirtualNetworkFunctionDescriptor tmpVnfd = getVirtualNetworkFunctionDescriptor(vnfdId);
			            if(tmpVnfd != null){
			            	vnfdMap.put(uri, tmpVnfd);	
			            	foundVnfd = new VirtualNetworkFunctionDescriptor();
							foundVnfd.setId(tmpVnfd.getId());
			            }
					}
		           }
			}

		if(foundVnfd != null){
			networkServiceDescriptor.getVnfd().add(foundVnfd);
		}else{
            LOGGER.log(Level.SEVERE, "Could not found correct VNFD in List");
//            Resource vnfd = ModelFactory.createDefaultModel().createResource(foundVnfdUri);
//            String vnfdId = vnfd.listProperties(Omn_lifecycle.hasID).next().getObject().asLiteral().toString();
//            VirtualNetworkFunctionDescriptor tmpVnfd = getVirtualNetworkFunctionDescriptor(vnfdId);
//            
//            if(tmpVnfd != null){
//            	vnfdMap.put(key, tmpVnfd);
//            }
		}
//		updateNetworkServiceDescriptor(networkServiceDescriptor, networkServiceDescriptor.getId());
	}
	public VirtualNetworkFunctionDescriptor getVirtualNetworkFunctionDescriptor(String id){
		try {
			return vnfdAgent.findById(id);
		} catch (ClassNotFoundException | SDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
//		checkRequestor();
//		FiveGCore fiveG = (FiveGCore) openBaton;
//		NetworkServiceDescriptor nsd = null;
//		List<NetworkServiceDescriptor> nsdList = getAllNSDs();
//		for (NetworkServiceDescriptor n : nsdList) {
//			if (n.getName().contains("5G") && n.getName().contains("Core")) {
//				nsd = n;
//				LOGGER.log(Level.SEVERE, "FOUND NSD");
//			}
//		}
//		NetworkServiceRecordRestAgent agent = nfvoRequestor.getNetworkServiceRecordAgent();
//		try {
//			NetworkServiceRecord newNsRecord = agent.create(nsd.getId());
//			String test;
//			test = "";
//			test = "";
//			test = "";
//			test = "";
//			test = "";
//			test = "";
//			test = "";
//
//			fiveG.setNsr(newNsRecord);
//
//			List<VirtualNetworkFunctionRecord> vnfrList = agent.getVirtualNetworkFunctionRecords(newNsRecord.getId());
//			LOGGER.log(Level.SEVERE, vnfrList.toString());
//			LOGGER.log(Level.SEVERE, "test");
//
//		} catch (SDKException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

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
	
	

}
