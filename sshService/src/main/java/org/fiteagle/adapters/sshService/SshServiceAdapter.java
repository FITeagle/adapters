package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.adapters.sshService.*;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class SshServiceAdapter extends AbstractAdapter {
	
	  private Model adapterModel;
	  private Resource adapterInstance;
	  private static Resource adapter;
	  private SshService sshService;
	  
	  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
	  
	  protected HashMap<String, SshService> instanceList = new HashMap<String, SshService>();


	  static {
		    Model adapterModel = OntologyModelUtil.loadModel("ontologies/sshservice.ttl", IMessageBus.SERIALIZATION_TURTLE);
		    
		    ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		    if (adapterIterator.hasNext()) {
		      adapter = adapterIterator.next();
		    }
		    
		    createDefaultAdapterInstance(adapterModel);
		  }
		  
		  private static void createDefaultAdapterInstance(Model adapterModel){
		    Resource adapterInstance = adapterModel.createResource(OntologyModelUtil.getResourceNamespace()+"PhysicalNodeAdapter-1");
		    adapterInstance.addProperty(RDF.type, adapter);
		    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
		    adapterInstance.addProperty(RDFS.comment, "A SSH Adapter that can create and manage SSH-Accesses.");
		    Resource testbed = adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
		    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);

		    
		    StmtIterator resourceIterator = adapter.listProperties(Omn_lifecycle.implements_);
		    if (resourceIterator.hasNext()) {
		      Resource resource = resourceIterator.next().getObject().asResource();
		      
		      adapterInstance.addProperty(Omn_lifecycle.parentTo, resource);
		      ResIterator propertiesIterator = adapterModel.listSubjectsWithProperty(RDFS.domain, resource);
		      while (propertiesIterator.hasNext()) {
		        Property p = adapterModel.getProperty(propertiesIterator.next().getURI());
		      }
		    }
		    
		    new SshServiceAdapter(adapterInstance, adapterModel, new SshService());
		  }
		  
		  
		  private SshServiceAdapter(Resource adapterInstance, Model adapterModel,SshService sshService) {
			  super(adapterInstance.getLocalName());

			    this.adapterInstance = adapterInstance;
			    this.adapterModel = adapterModel;
			    this.sshService = sshService;
			    adapterInstances.put(adapterInstance.getURI(), this);
			    Log.fatal("NAME", OntologyModelUtil.getResourceNamespace()+"PhysicalNodeAdapter-1");
				
			    
//			    try {
//					createInstance("SSH-Adapter", testModel());
//				} catch (ProcessingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvalidRequestException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
		}
	  
	  public Model testModel(){
		  Model model = ModelFactory.createDefaultModel();
		  Resource resource = ModelFactory.createDefaultModel().createResource("TEST-Resource");
		  model.add(resource, model.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-Username>"), "TEST-USER");
		  model.add(resource, model.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-PubKey>"), "TESt-KEY");

		  return model;
	  }

	public SshServiceAdapter(String adapterInstanceName) {
		super(adapterInstanceName);
		// TODO Auto-generated constructor stub
	}

	  protected Model parseToModel(SshService sshService) {
		    Resource resource = ModelFactory.createDefaultModel().createResource(sshService.getInstanceName());
		    resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
		    resource.addProperty(RDF.type, Omn.Resource);
		    resource.addProperty(RDFS.label, resource.getLocalName());
		    resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
		    
		    return resource.getModel();
		  }
	
	
	@Override
	public Resource getAdapterInstance() {
		Log.fatal("BLA", adapterInstance.toString());
		Log.fatal("BLA", adapter.toString());
		Log.fatal("BLA", adapterModel.toString());


		return adapterInstance;
	}

	@Override
	public Resource getAdapterType() {
		return adapter;
	}

	@Override
	public Model getAdapterDescriptionModel() {
		return adapterModel;
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
	public Model createInstance(String instanceURI, Model newInstanceModel)throws ProcessingException, InvalidRequestException {
		Log.fatal("MODEL",newInstanceModel.toString());
		String pubKey = null;
		
		ResIterator resIteratorKey= newInstanceModel.listResourcesWithProperty(newInstanceModel.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-PubKey>"));
		if(!resIteratorKey.hasNext())
			throw new InvalidRequestException("Public Key is missing");
		while(resIteratorKey.hasNext()){
			Resource resource = resIteratorKey.nextResource();
			pubKey = resource.getProperty(newInstanceModel.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-PubKey>")).getLiteral().getString();
		}
		
		ResIterator resIteratorIP= newInstanceModel.listResourcesWithProperty(newInstanceModel.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-Username>"));
		if(!resIteratorIP.hasNext())
			throw new InvalidRequestException("Username is missing");
		while(resIteratorIP.hasNext()){
			Resource resource = resIteratorIP.nextResource();
			String ip = resource.getProperty(newInstanceModel.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-IP>")).getLiteral().getString();
		sshService.addSshAccess(ip, pubKey);
		}

		return null;
	}

	@Override
	public void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException,
			ProcessingException {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public Model getInstance(String instanceURI)throws InstanceNotFoundException {
    SshService sshService = instanceList.get(instanceURI);
    if(sshService == null){
      throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
    }
    return parseToModel(sshService);
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException{
	Model model = ModelFactory.createDefaultModel();
	for(String uri : instanceList.keySet()){
	      model.add(getInstance(uri));
	    }
		return model;
	}

}
