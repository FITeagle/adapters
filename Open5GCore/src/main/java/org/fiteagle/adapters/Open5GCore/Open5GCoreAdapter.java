package org.fiteagle.adapters.Open5GCore;

import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ejb.EJB;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.Open5GCore.dm.Open5GCoreAdapterMDBSender;
import org.fiteagle.api.core.Config;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class Open5GCoreAdapter extends AbstractAdapter {
    private static final Logger LOGGER = Logger.getLogger(Open5GCoreAdapter.class.toString());
    protected Open5GCoreClient openstackClient;

    private Open5GCoreAdapterMDBSender listener;

  	@EJB
  	Open5GCoreAdapterControl openstackAdapterControler;

    private transient final HashMap<String, Open5GCoreClient> instanceList = new HashMap<String, Open5GCoreClient>();

    
    public Open5GCoreAdapter(final Model adapterModel, final Resource adapterABox) {
    	super();
    	this.uuid = UUID.randomUUID().toString();
    	this.adapterTBox = adapterModel;
    	this.adapterABox = adapterABox;
    	final Resource adapterType = this.getAdapterClass();
        this.adapterABox.addProperty(RDF.type,adapterType);
        this.adapterABox.addProperty(RDFS.label,  this.adapterABox.getLocalName());
        this.adapterABox.addProperty(RDFS.comment, "5G Core server");
        
        this.adapterABox.addProperty(Omn_lifecycle.canImplement, Omn_domain_pc.PC);
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

	@Override
	public void updateAdapterDescription() throws ProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Model updateInstance(String instanceURI, Model configureModel)
			throws InvalidRequestException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model createInstance(String instanceURI, Model newInstanceModel)
			throws ProcessingException, InvalidRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException,
			ProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Model getInstance(String instanceURI)
			throws InstanceNotFoundException, ProcessingException,
			InvalidRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException,
			ProcessingException {
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

	public void setListener(Open5GCoreAdapterMDBSender mdbSender) {
		// TODO Auto-generated method stub
		this.listener = mdbSender;
	}

  

}
