package org.fiteagle.adapters.OpenBaton.Model;

import org.fiteagle.adapters.OpenBaton.OpenBatonAdapter;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.vocabulary.Fiveg;
import info.openmultinet.ontology.vocabulary.OpenBaton;

public class FiveGCore extends OpenBatonService {

	private MME mme ;
	private ENodeB enodeb;
	private Gateway gw;
	private DomainNameSystem dns;
	private NetworkServiceDescriptor nsd;
	
	
	public FiveGCore(OpenBatonAdapter owningAdapter, String instanceUri) {
		super(owningAdapter, instanceUri);
	}
	
	@Override
	public void parseToModel(Resource resource) {
		resource.addProperty(RDF.type, OpenBaton.FiveGCore);
		resource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Omn.Resource);
		
		resource.addLiteral(Fiveg.managementInterface, "TEST-BLABLA");
		super.parseToModel(resource);

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
	
	
}
