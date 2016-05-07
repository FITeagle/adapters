package org.fiteagle.adapters.OpenBaton;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import org.fiteagle.adapters.OpenBaton.Model.DomainNameSystem;
import org.fiteagle.adapters.OpenBaton.Model.ENodeB;
import org.fiteagle.adapters.OpenBaton.Model.FiveGCore;
import org.fiteagle.adapters.OpenBaton.Model.Gateway;
import org.fiteagle.adapters.OpenBaton.Model.MME;
import org.fiteagle.adapters.OpenBaton.Model.OpenBatonGeneric;
import org.fiteagle.adapters.OpenBaton.Model.OpenBatonService;
import org.openbaton.catalogue.mano.common.LifecycleEvent;
import org.openbaton.catalogue.mano.common.VNFDeploymentFlavour;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VNFDependency;
import org.openbaton.catalogue.mano.descriptor.VNFForwardingGraphDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualLinkDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.openbaton.catalogue.nfvo.Configuration;
import org.openbaton.sdk.*;
import org.openbaton.sdk.api.exception.SDKException;
import org.openbaton.sdk.api.rest.NetworkServiceDescriptorRestAgent;
import org.openbaton.sdk.api.rest.VNFFGRestAgent;
import org.openbaton.sdk.api.rest.VirtualLinkRestAgent;
import org.openbaton.sdk.api.rest.VirtualNetworkFunctionDescriptorRestAgent;

import com.google.common.annotations.Beta;




public class OpenBatonClient {
    private static final Logger LOGGER = Logger.getLogger(OpenBatonClient.class.toString());

    
    private final OpenBatonAdapter openBatonAdapter;
    
    private String username;
    private String password;
    private String nfvoIp;
    private String nfvoPort;
    private String version;
    
    private NFVORequestor nfvoRequestor;

public OpenBatonClient(OpenBatonAdapter openBatonAdapter){
	this.openBatonAdapter = openBatonAdapter;
}

private void loadPreferences() {
	try{
		username = openBatonAdapter.getUsername();
		password = openBatonAdapter.getPassword();
		nfvoIp = openBatonAdapter.getNfvoIp();
		nfvoPort = openBatonAdapter.getNfvoPort();
		version = openBatonAdapter.getVersion();
	}catch(Exception e){
		e.printStackTrace();
	}

	
	if (username == null){
		throw new InsufficientOpenBatonPreferences("username");
	}	
	if (password == null){
		throw new InsufficientOpenBatonPreferences("password");
	}	
	if (nfvoIp == null){
		throw new InsufficientOpenBatonPreferences("nfvoIp");
	}	
	if (nfvoPort == null){
		throw new InsufficientOpenBatonPreferences("nfvoPort");
	}	
	if (version == null){
		throw new InsufficientOpenBatonPreferences("version");
	}
}

public void init(){
	loadPreferences();
	checkRequestor();
	
//	RequestFactory requestFac = RequestFactory.getInstance(username, password, nfvoIp, nfvoPort, version);
	NetworkServiceDescriptorRestAgent nsdAgend = nfvoRequestor.getNetworkServiceDescriptorAgent();
	try {
		LOGGER.log(Level.SEVERE, nsdAgend.findAll().toString());
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//	createNetworkServiceDescriptor();
}

private void checkRequestor() {
	if(nfvoRequestor == null){
		nfvoRequestor = new NFVORequestor("","","193.175.132.242","8080","1");
	}
}

public VirtualNetworkFunctionDescriptor createENodeB (OpenBatonService openBaton, String nsdID){
	checkRequestor();
	ENodeB eNodeB = (ENodeB) openBaton;
	VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();
	
	tmpVnfd.setConfigurations(eNodeB.getConfiguration());
	
	// TODO set floatingIp & virtual_link_reference from JSON?
//	tmpVnfd.setConnection_point(null);
	
	Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
	flavourSet.add(eNodeB.getDeploymentFlavour());
	tmpVnfd.setDeployment_flavour(flavourSet);
	
	tmpVnfd.setEndpoint(eNodeB.getEndpoint());
	
	tmpVnfd.setLifecycle_event(eNodeB.getLifecycleEvents());
	tmpVnfd.setCyclicDependency(true);
	tmpVnfd.setHb_version(2);
	
	tmpVnfd.setName(eNodeB.getServiceName()+ new Random().nextInt());
	
	tmpVnfd.setType(eNodeB.getType());
	tmpVnfd.setVendor(eNodeB.getVendor());
	tmpVnfd.setVersion(eNodeB.getVersion());
	tmpVnfd.setVdu(eNodeB.getVduSet());
	tmpVnfd.setVirtual_link(eNodeB.getVirtualLinkSet());
	
	try {
		VirtualNetworkFunctionDescriptor createdENodeB;
		if(nsdID != null){
			createdENodeB = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

		}else{
			createdENodeB = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
		}
		LOGGER.log(Level.SEVERE,"MME CREATED");
		return createdENodeB;

	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
}

public VirtualNetworkFunctionDescriptor createMME(OpenBatonService openBaton, String nsdID){
	checkRequestor();
	MME mme = (MME) openBaton;
	VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();
	
	tmpVnfd.setConfigurations(mme.getConfiguration());
	
	// TODO set floatingIp & virtual_link_reference from JSON?
//	tmpVnfd.setConnection_point(null);
	
	Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
	flavourSet.add(mme.getDeploymentFlavour());
	tmpVnfd.setDeployment_flavour(flavourSet);
	
	tmpVnfd.setEndpoint(mme.getEndpoint());
	
	tmpVnfd.setLifecycle_event(mme.getLifecycleEvents());
	tmpVnfd.setCyclicDependency(true);
	tmpVnfd.setHb_version(2);
	
	tmpVnfd.setName(mme.getServiceName()+ new Random().nextInt());
	
	tmpVnfd.setType(mme.getType());
	tmpVnfd.setVendor(mme.getVendor());
	tmpVnfd.setVersion(mme.getVersion());
	tmpVnfd.setVdu(mme.getVduSet());
	tmpVnfd.setVirtual_link(mme.getVirtualLinkSet());
	
	try {
		VirtualNetworkFunctionDescriptor createdMME;
		if(nsdID != null){
			createdMME = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

		}else{
			createdMME = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
		}
		LOGGER.log(Level.SEVERE,"MME CREATED");
		return createdMME;

	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	
}

public VirtualNetworkFunctionDescriptor createMME(OpenBatonService openBaton){
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
	
	tmpVnfd.setName(mme.getServiceName()+ new Random().nextInt());
	
	tmpVnfd.setType(mme.getType());
	tmpVnfd.setVendor(mme.getVendor());
	tmpVnfd.setVersion(mme.getVersion());
	tmpVnfd.setVdu(mme.getVduSet());
	tmpVnfd.setVirtual_link(mme.getVirtualLinkSet());
	
	try {
		VirtualNetworkFunctionDescriptor createdMME = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
		LOGGER.log(Level.SEVERE,"MME CREATED");
		return createdMME;

	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	
}

public VirtualNetworkFunctionDescriptor createDomainNameSystem(OpenBatonService openBaton, String nsdID){
	checkRequestor();
	DomainNameSystem dns = (DomainNameSystem) openBaton;
	VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();
	
	tmpVnfd.setConfigurations(dns.getConfiguration());
	
	// TODO set floatingIp & virtual_link_reference from JSON?
//	tmpVnfd.setConnection_point(null);
	
	Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
	flavourSet.add(dns.getDeploymentFlavour());
	tmpVnfd.setDeployment_flavour(flavourSet);
	
	tmpVnfd.setEndpoint(dns.getEndpoint());
	
	tmpVnfd.setLifecycle_event(dns.getLifecycleEvents());
	tmpVnfd.setCyclicDependency(true);
	tmpVnfd.setHb_version(2);
	
	tmpVnfd.setName(dns.getServiceName()+ new Random().nextInt());
	
	tmpVnfd.setType(dns.getType());
	tmpVnfd.setVendor(dns.getVendor());
	tmpVnfd.setVersion(dns.getVersion());
	tmpVnfd.setVdu(dns.getVduSet());
	tmpVnfd.setVirtual_link(dns.getVirtualLinkSet());
	
	try {
		VirtualNetworkFunctionDescriptor createdDNS;
		if(nsdID != null){
			createdDNS = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

		}else{
			createdDNS = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
		}
		LOGGER.log(Level.SEVERE,"MME CREATED");
		return createdDNS;

	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
}

public VirtualNetworkFunctionDescriptor createGateway (OpenBatonService openBaton, String nsdID){
	checkRequestor();
	Gateway gw = (Gateway) openBaton;
	VirtualNetworkFunctionDescriptor tmpVnfd = new VirtualNetworkFunctionDescriptor();
	
	tmpVnfd.setConfigurations(gw.getConfiguration());
	
	// TODO set floatingIp & virtual_link_reference from JSON?
//	tmpVnfd.setConnection_point(null);
	
	Set<VNFDeploymentFlavour> flavourSet = new HashSet<VNFDeploymentFlavour>();
	flavourSet.add(gw.getDeploymentFlavour());
	tmpVnfd.setDeployment_flavour(flavourSet);
	
	tmpVnfd.setEndpoint(gw.getEndpoint());
	
	tmpVnfd.setLifecycle_event(gw.getLifecycleEvents());
	tmpVnfd.setCyclicDependency(true);
	tmpVnfd.setHb_version(2);
	
	tmpVnfd.setName(gw.getServiceName()+ new Random().nextInt());
	
	tmpVnfd.setType(gw.getType());
	tmpVnfd.setVendor(gw.getVendor());
	tmpVnfd.setVersion(gw.getVersion());
	tmpVnfd.setVdu(gw.getVduSet());
	tmpVnfd.setVirtual_link(gw.getVirtualLinkSet());
	
	try {
		VirtualNetworkFunctionDescriptor createdGateway;
		if(nsdID != null){
			createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(nsdID, tmpVnfd);

		}else{
			createdGateway = nfvoRequestor.getNetworkServiceDescriptorAgent().createVNFD(null, tmpVnfd);
		}
		LOGGER.log(Level.SEVERE,"MME CREATED");
		return createdGateway;

	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
}

public void createFiveGCore(OpenBatonService openBaton){
	
	MME mme = new MME(null, null);
	ENodeB enodeb = new ENodeB(null, null);
	Gateway gw = new Gateway(null, null);
	DomainNameSystem dns = new DomainNameSystem(null, null);
	
	FiveGCore fiveG = (FiveGCore) openBaton;
	fiveG.setDns(dns);
	fiveG.setEnodeb(enodeb);
	fiveG.setGw(gw);
	fiveG.setMme(mme);
	
	// Creating empty NetworkServiceDescriptor
	NetworkServiceDescriptor nsd = createNetworkServiceDescriptor(null);
	
	// Creating VNFs and adding them to the created NSD
	VirtualNetworkFunctionDescriptor vnfMME = createMME(mme, nsd.getId());
	VirtualNetworkFunctionDescriptor vnfdns = createDomainNameSystem(dns, nsd.getId());
	VirtualNetworkFunctionDescriptor vnfenodeB = createENodeB(enodeb, nsd.getId());
	VirtualNetworkFunctionDescriptor vnfGw = createGateway(gw, nsd.getId());
	
	
	Set<VNFDependency> vnfDependencysSet = new HashSet<VNFDependency>();
	Set<String> parameterSet = new HashSet<String>();
	VNFDependency vnfDependency = new VNFDependency();
//	vnfDependency.setSource(vnfenodeB);
//	vnfDependency.setTarget(vnfUemm);
//		parameterSet.add("var_net_c_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_c");
//		parameterSet.add("net_c_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);

	vnfDependency = new VNFDependency();
	parameterSet = new HashSet<String>();
	vnfDependency.setSource(vnfenodeB);
	vnfDependency.setTarget(vnfMME);
		parameterSet.add("var_net_c_network");
		parameterSet.add("var_net_d_network");
		parameterSet.add("var_name");
		parameterSet.add("var_range");
		parameterSet.add("mgmt");
		parameterSet.add("mgmt_floatingIp");
		parameterSet.add("net_c");
		parameterSet.add("net_c_floatingIp");
		parameterSet.add("net_d");
		parameterSet.add("net_d_floatingIp");
	vnfDependency.setParameters(parameterSet);
	vnfDependencysSet.add(vnfDependency);
	
//	vnfDependency = new VNFDependency();
//	parameterSet = new HashSet<String>();
//	vnfDependency.setSource(vnfdns);
//	vnfDependency.setTarget(vnfUemm);
//		parameterSet.add("realm");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);
	
	
	vnfDependency = new VNFDependency();
	parameterSet = new HashSet<String>();
	vnfDependency.setSource(vnfdns);
	vnfDependency.setTarget(vnfMME);
		parameterSet.add("realm");
		parameterSet.add("mgmt");
		parameterSet.add("mgmt_floatingIp");
	vnfDependency.setParameters(parameterSet);
	vnfDependencysSet.add(vnfDependency);
	
	vnfDependency = new VNFDependency();
	parameterSet = new HashSet<String>();
	vnfDependency.setSource(vnfdns);
	vnfDependency.setTarget(vnfenodeB);
		parameterSet.add("realm");
		parameterSet.add("mgmt");
		parameterSet.add("mgmt_floatingIp");
	vnfDependency.setParameters(parameterSet);
	vnfDependencysSet.add(vnfDependency);
	vnfDependencysSet.add(vnfDependency);
	
//	vnfDependency = new VNFDependency();
//	parameterSet = new HashSet<String>();
//	vnfDependency.setSource(vnfGw);
//	vnfDependency.setTarget(vnfSgwuPgwu);
//	vnfDependencysSet.add(vnfDependency);
//		parameterSet.add("var_net_a_network");
//		parameterSet.add("net_a");
//		parameterSet.add("net_a_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);
//	vnfDependencysSet.add(vnfDependency);
	
	vnfDependency = new VNFDependency();
	parameterSet = new HashSet<String>();
	vnfDependency.setSource(vnfMME);
	vnfDependency.setTarget(vnfenodeB);
	vnfDependencysSet.add(vnfDependency);
		parameterSet.add("var_net_d_network");
		parameterSet.add("mgmt");
		parameterSet.add("mgmt_floatingIp");
		parameterSet.add("net_d");
		parameterSet.add("net_d_floatingIp");
	vnfDependency.setParameters(parameterSet);
	vnfDependencysSet.add(vnfDependency);
	vnfDependencysSet.add(vnfDependency);
	
//	vnfDependency = new VNFDependency();
//	parameterSet = new HashSet<String>();
//	vnfDependency.setSource(vnfMME);
//	vnfDependency.setTarget(vnfSgwuPgwu);
//	vnfDependencysSet.add(vnfDependency);
//		parameterSet.add("var_ofp_transport");
//		parameterSet.add("var_ofp_port");
//		parameterSet.add("var_mgmt_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_d");
//		parameterSet.add("net_d_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);
//	vnfDependencysSet.add(vnfDependency);
	
	vnfDependency = new VNFDependency();
	parameterSet = new HashSet<String>();
	vnfDependency.setSource(vnfMME);
	vnfDependency.setTarget(vnfdns);
		parameterSet.add("var_name");
		parameterSet.add("var_mgmt_network");
		parameterSet.add("mgmt");
		parameterSet.add("mgmt_floatingIp");
	vnfDependency.setParameters(parameterSet);
	vnfDependencysSet.add(vnfDependency);
	
//	vnfDependency = new VNFDependency();
//	parameterSet = new HashSet<String>();
//	vnfDependency.setSource(vnfSgwuPgwu);
//	vnfDependency.setTarget(vnfMME);
//		parameterSet.add("var_net_a_network");
//		parameterSet.add("var_net_d_network");
//		parameterSet.add("var_datapath_id");
//		parameterSet.add("net_a");
//		parameterSet.add("net_d");
//		parameterSet.add("net_d_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);
	
//	vnfDependency = new VNFDependency();
//	parameterSet = new HashSet<String>();
//	vnfDependency.setSource(vnfSgwuPgwu);
//	vnfDependency.setTarget(vnfGw);
//	parameterSet.add("var_net_a_network");
//	parameterSet.add("var_static_num");
//	parameterSet.add("net_a");
//	parameterSet.add("net_a_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);
//	vnfDependencysSet.add(vnfDependency);
	
//	vnfDependency = new VNFDependency();
//	parameterSet = new HashSet<String>();
//	vnfDependency.setSource(vnfUemm);
//	vnfDependency.setTarget(enodeb);
//		parameterSet.add("var_net_c_network");
//		parameterSet.add("mgmt");
//		parameterSet.add("mgmt_floatingIp");
//		parameterSet.add("net_c");
//		parameterSet.add("net_c_floatingIp");
//	vnfDependency.setParameters(parameterSet);
//	vnfDependencysSet.add(vnfDependency);
//	vnfDependencysSet.add(vnfDependency);
	
	
	
	nsd = getNetworkServiceDescriptor(nsd.getId());
	nsd.setVnf_dependency(vnfDependencysSet);
	
	// VirtualLinkDescriptor for 5G-Core
	Set<VirtualLinkDescriptor> vldSet = new HashSet<VirtualLinkDescriptor>();
	VirtualLinkDescriptor vld = new VirtualLinkDescriptor();
	vld.setName("mgmt");
	vldSet.add(vld);
	
	vld = new VirtualLinkDescriptor();
	vld.setName("net_a");
	vldSet.add(vld);
	
	vld = new VirtualLinkDescriptor();
	vld.setName("net_c");
	vldSet.add(vld);
	
	vld = new VirtualLinkDescriptor();
	vld.setName("net_d");
	vldSet.add(vld);

	nsd.setVld(vldSet);
	
	// Update the NetworkServiceDescriptor on the Server with new Dependencys and VLDs
	NetworkServiceDescriptor updatedNSD = updateNetworkServiceDescriptor(nsd, nsd.getId());
	
	fiveG.setNsd(updatedNSD);
	
}

public void createVirtualNetworkFunctionManager(){
	
}
public void createVirtualNetworkFunctionDescriptor(){
	VirtualNetworkFunctionDescriptor vnfd = new VirtualNetworkFunctionDescriptor();
}
public void createNetworkServiceRecord(){
	
}
public NetworkServiceDescriptor createNetworkServiceDescriptor(OpenBatonService openBaton){
	checkRequestor();
	NetworkServiceDescriptor networkDescriptor = new NetworkServiceDescriptor();
	networkDescriptor.setEnabled(true);
	networkDescriptor.setVendor("fokus");
	if(openBaton != null){
		networkDescriptor.setVersion(openBaton.getServiceContainer().getVersion());
		networkDescriptor.setName(openBaton.getServiceContainer().getContainerName());
		networkDescriptor.setVnfd(null);	
	}else{
		LOGGER.log(Level.WARNING,"Creating NetworkServiceDescriptor with null Object - Using default values");
		networkDescriptor.setVersion("0.1");
		networkDescriptor.setName("defaultName-" + new Random().nextInt());
		networkDescriptor.setVnfd(null);	
	}

	try {
		NetworkServiceDescriptor createdNSD = nfvoRequestor.getNetworkServiceDescriptorAgent().create(networkDescriptor);
		return createdNSD;
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
}

@Beta
public NetworkServiceDescriptor updateNetworkServiceDescriptor(NetworkServiceDescriptor nsd, String id){
	checkRequestor();

	try {
		NetworkServiceDescriptor updatedNSD = nfvoRequestor.getNetworkServiceDescriptorAgent().update(nsd, id);
		return updatedNSD;
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
}

public NetworkServiceDescriptor getNetworkServiceDescriptor(String nsdId){
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


@Beta
public NetworkServiceDescriptor createNetworkServiceDescriptor(OpenBatonService openBaton, Set<VirtualNetworkFunctionDescriptor> vnfSet){
	checkRequestor();
	NetworkServiceDescriptor networkDescriptor = new NetworkServiceDescriptor();
	networkDescriptor.setEnabled(true);
	networkDescriptor.setVendor("fokus");
	if(openBaton != null){
		networkDescriptor.setVersion(openBaton.getServiceContainer().getVersion());
		networkDescriptor.setName(openBaton.getServiceContainer().getContainerName());
		networkDescriptor.setVnfd(null);	
	}else{
		LOGGER.log(Level.WARNING,"Creating NetworkServiceDescriptor with null Object - Using default values");
		networkDescriptor.setVersion("0.1");
		networkDescriptor.setName("defaultName-" + new Random().nextInt());
		networkDescriptor.setVnfd(vnfSet);	
	}

	try {
		NetworkServiceDescriptor createdNSD = nfvoRequestor.getNetworkServiceDescriptorAgent().create(networkDescriptor);
		return createdNSD;
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
}

public VirtualNetworkFunctionDescriptor getVnfdFromResource(OpenBatonService openBaton){
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
	
	tmpVnfd.setName(mme.getServiceName()+ new Random().nextInt());
	
	tmpVnfd.setType(mme.getType());
	tmpVnfd.setVendor(mme.getVendor());
	tmpVnfd.setVersion(mme.getVersion());
	tmpVnfd.setVdu(mme.getVduSet());
	tmpVnfd.setVirtual_link(mme.getVirtualLinkSet());
	
	return tmpVnfd;
}
public void createVirtualNetworkFunctionPackage(){
	
}
public void createPoPInstance(){
	
}


public List<NetworkServiceDescriptor> getAllNSDs(){
	NetworkServiceDescriptorRestAgent nsdAgend = nfvoRequestor.getNetworkServiceDescriptorAgent();
	List<NetworkServiceDescriptor> tmpList = null;
	try {
		 tmpList =  nsdAgend.findAll();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
return tmpList;
}

public List<VirtualLinkDescriptor> getAllVnfDescriptor(){
	VirtualLinkRestAgent nsdAgend = nfvoRequestor.getVirtualLinkAgent();
	List<VirtualLinkDescriptor> tmpList = null;
	try {
		 tmpList =  nsdAgend.findAll();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
return tmpList;
	
}
public void getAllVnfManagers(){
	
}
public void getAllNSRs(){
	
}

public boolean deleteAllVnfsOfNSD(String nsdID){
	checkRequestor();
	NetworkServiceDescriptorRestAgent nsdAgend = nfvoRequestor.getNetworkServiceDescriptorAgent();
	List<VirtualNetworkFunctionDescriptor> vnfList;
	try {
		vnfList = nsdAgend.getVirtualNetworkFunctionDescriptors(nsdID);
		for (VirtualNetworkFunctionDescriptor vnf : vnfList){
			nsdAgend.deleteVirtualNetworkFunctionDescriptors(nsdID, vnf.getId());
		}
		return true;
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	
}

@Beta
public boolean deleteAllTestVNFs(){
	checkRequestor();
	VirtualNetworkFunctionDescriptorRestAgent vnfAgent = nfvoRequestor.getVirtualNetworkFunctionDescriptorRestAgent();
	try {
		List<VirtualNetworkFunctionDescriptor> vnfList = vnfAgent.findAll();
		for(VirtualNetworkFunctionDescriptor vnf : vnfList){
			if(vnf.getName().startsWith("Test")){
				vnfAgent.delete(vnf.getId());
			}
		}
		return true;
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;

}

public boolean deleteVnfOfNSD(String nsdID, String vnfID){
	checkRequestor();
	NetworkServiceDescriptorRestAgent nsdAgend = nfvoRequestor.getNetworkServiceDescriptorAgent();
	try {
			nsdAgend.deleteVirtualNetworkFunctionDescriptors(nsdID, vnfID);
			return true;
	} catch (SDKException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	
}

  public static class InsufficientOpenBatonPreferences extends RuntimeException {
    
    private static final long serialVersionUID = 6511540487288262809L;

    public InsufficientOpenBatonPreferences(String preferenceName) {
      super("Please set the preference: "+preferenceName);
    }
  }

}
