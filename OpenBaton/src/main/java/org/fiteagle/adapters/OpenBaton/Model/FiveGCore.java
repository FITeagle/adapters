package org.fiteagle.adapters.OpenBaton.Model;

import java.util.UUID;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
import org.fiteagle.api.core.OntologyModelUtil;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Fiveg;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_service;
import info.openmultinet.ontology.vocabulary.OpenBaton;

public class FiveGCore extends OpenBatonService {

	// The FiveGCore Object is containing all instances of the necessary parts 
	private MME mme ;
	private ENodeB enodeb;
	private Gateway gw;
	private DomainNameSystem dns;
	private NetworkServiceDescriptor nsd;
	private SgwuPgwu sgwupgwu;
	private UE ue;
	
	// Also as VirtualNetworkFunctionDescriptors
	private VirtualNetworkFunctionDescriptor mmeVnf ;
	private VirtualNetworkFunctionDescriptor enodebVnf;
	private VirtualNetworkFunctionDescriptor gwVnf;
	private VirtualNetworkFunctionDescriptor dnsVnf;
	private VirtualNetworkFunctionDescriptor nsdVnf;
	private VirtualNetworkFunctionDescriptor sgwupgwuVnf;
	private VirtualNetworkFunctionDescriptor ueVnf;
	
	
	public FiveGCore(OpenBatonAdapter owningAdapter, String instanceUri) {
		super(owningAdapter, instanceUri);
	}
	
	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type, OpenBaton.FiveGCore);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);
		super.parseToModel(resource);

		
	    Model parsedServerModel =  resource.getModel();
	    
	    // If available there will be added a LogInService to the NSD Instance for SSH
		if(nsd != null){
			Resource loginService = parsedServerModel.createResource(OntologyModelUtil
		            .getResourceNamespace() + "LoginService" + UUID.randomUUID().toString());
		 loginService.addProperty(RDF.type, Omn_service.LoginService);
		 loginService.addProperty(Omn_service.authentication,"ssh-keys");
		 // Just using Dummy data
		 loginService.addProperty(Omn_service.username, nsd.getName());
		 loginService.addProperty(Omn_service.hostname, "192.76.87.281");
		 loginService.addProperty(Omn_service.port,"22");
		    resource.addProperty(Omn.hasService, loginService);
				}
		
	}


	public MME getMme() {
		return mme;
	}


	public void setMme(MME mme) {
		this.mme = mme;
	}


	public ENodeB getEnodeb() {
		return enodeb;
	}


	public void setEnodeb(ENodeB enodeb) {
		this.enodeb = enodeb;
	}


	public Gateway getGw() {
		return gw;
	}


	public void setGw(Gateway gw) {
		this.gw = gw;
	}


	public DomainNameSystem getDns() {
		return dns;
	}


	public void setDns(DomainNameSystem dns) {
		this.dns = dns;
	}


	public NetworkServiceDescriptor getNsd() {
		return nsd;
	}


	public void setNsd(NetworkServiceDescriptor nsd) {
		this.nsd = nsd;
	}

	public void setUe(UE ue) {
		this.ue = ue;		
	}

	public void setSgwuPgwu(SgwuPgwu sgwupgwu) {
		this.sgwupgwu = sgwupgwu;		
	}

	public SgwuPgwu getSgwupgwu() {
		return sgwupgwu;
	}

	public void setSgwupgwu(SgwuPgwu sgwupgwu) {
		this.sgwupgwu = sgwupgwu;
	}

	public VirtualNetworkFunctionDescriptor getMmeVnf() {
		return mmeVnf;
	}

	public void setMmeVnf(VirtualNetworkFunctionDescriptor mmeVnf) {
		this.mmeVnf = mmeVnf;
	}

	public VirtualNetworkFunctionDescriptor getEnodebVnf() {
		return enodebVnf;
	}

	public void setEnodebVnf(VirtualNetworkFunctionDescriptor enodebVnf) {
		this.enodebVnf = enodebVnf;
	}

	public VirtualNetworkFunctionDescriptor getGwVnf() {
		return gwVnf;
	}

	public void setGwVnf(VirtualNetworkFunctionDescriptor gwVnf) {
		this.gwVnf = gwVnf;
	}

	public VirtualNetworkFunctionDescriptor getDnsVnf() {
		return dnsVnf;
	}

	public void setDnsVnf(VirtualNetworkFunctionDescriptor dnsVnf) {
		this.dnsVnf = dnsVnf;
	}

	public VirtualNetworkFunctionDescriptor getNsdVnf() {
		return nsdVnf;
	}

	public void setNsdVnf(VirtualNetworkFunctionDescriptor nsdVnf) {
		this.nsdVnf = nsdVnf;
	}

	public VirtualNetworkFunctionDescriptor getSgwupgwuVnf() {
		return sgwupgwuVnf;
	}

	public void setSgwupgwuVnf(VirtualNetworkFunctionDescriptor sgwupgwuVnf) {
		this.sgwupgwuVnf = sgwupgwuVnf;
	}

	public VirtualNetworkFunctionDescriptor getUeVnf() {
		return ueVnf;
	}

	public void setUeVnf(VirtualNetworkFunctionDescriptor ueVnf) {
		this.ueVnf = ueVnf;
	}

	public UE getUe() {
		return ue;
	}
	
	
}
