package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_resource;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class SshServiceAdapter extends AbstractAdapter {

	private Model adapterModel;
	private Resource adapterInstance;
	private static Resource adapter;

	public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();

	protected HashMap<String, SshService> instanceList = new HashMap<String, SshService>();

	static {
		Model adapterModel = OntologyModelUtil.loadModel(
				"ontologies/sshservice.ttl", IMessageBus.SERIALIZATION_TURTLE);

		ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(
				RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		if (adapterIterator.hasNext()) {
			adapter = adapterIterator.next();
		}

		createDefaultAdapterInstance(adapterModel);
	}

	private static void createDefaultAdapterInstance(Model adapterModel) {
		Resource adapterInstance = adapterModel
				.createResource(OntologyModelUtil.getResourceNamespace()
						+ "PhysicalNodeAdapter-1");
		adapterInstance.addProperty(RDF.type, adapter);
		adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
		adapterInstance.addProperty(RDFS.comment,
				"A SSH Adapter that can create and manage SSH-Accesses.");
		Resource testbed = adapterModel
				.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
		adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);

		StmtIterator resourceIterator = adapter
				.listProperties(Omn_lifecycle.implements_);
		if (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next().getObject()
					.asResource();

			adapterInstance.addProperty(Omn_lifecycle.canImplement, resource);
			ResIterator propertiesIterator = adapterModel
					.listSubjectsWithProperty(RDFS.domain, resource);
			while (propertiesIterator.hasNext()) {
				Property p = adapterModel.getProperty(propertiesIterator.next()
						.getURI());
			}
		}

		new SshServiceAdapter(adapterInstance, adapterModel);
	}

	private SshServiceAdapter(Resource adapterInstance, Model adapterModel) {

		createDefaultConfiguration(adapterInstance.getLocalName());


		Config config = new Config(adapterInstance.getLocalName());
    
		String password = "aA21!7&8*";

		config.setNewProperty("password", password);

		this.adapterInstance = adapterInstance;
		this.adapterModel = adapterModel;
		adapterInstances.put(adapterInstance.getURI(), this);

	}


	public SshServiceAdapter(String adapterInstanceName) {
		createDefaultConfiguration(adapterInstanceName);
		// TODO Auto-generated constructor stub
	}

	protected Model parseToModel(SshService sshService) {
		Resource resource = ModelFactory.createDefaultModel().createResource(
				sshService.getInstanceName());
		resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
		resource.addProperty(RDF.type, Omn.Resource);
		resource.addProperty(RDFS.label, resource.getLocalName());
		resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);

		return resource.getModel();
	}

	@Override
	public Resource getAdapterInstance() {
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
		
	  Model model = ModelFactory.createDefaultModel();
	  ResIterator resIteratorKey = configureModel.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
	  while (resIteratorKey.hasNext()) {

      Resource resource = resIteratorKey.nextResource();
      model.add(createResponse(resource, instanceList.get(instanceURI)));
	  }
	  
		return model;
	}

	@Override
	public Model createInstance(String instanceURI, Model newInstanceModel)
			throws ProcessingException, InvalidRequestException {
	  
	  SshService sshService = new SshService(this);
	  
		String pubKey = "";
		String userName = "";
		Model result = ModelFactory.createDefaultModel();
		
		Resource resource = null;
		
		ResIterator resIteratorKey = newInstanceModel.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
		if (!resIteratorKey.hasNext())
			throw new InvalidRequestException("statements are missing ");
		while (resIteratorKey.hasNext()) {

			resource = resIteratorKey.nextResource();
			if (!resource.hasProperty(Omn_service.publickey))
				throw new InvalidRequestException("public key is missing ");
			else {
				pubKey = resource
						.getProperty(Omn_service.publickey)
						.getLiteral().getString();
			}
			if (!resource.hasProperty(Omn_service.username))
				throw new InvalidRequestException("user name is missing ");
			else {
				userName = resource
						.getProperty(Omn_service.username)
						.getLiteral().getString();
			}
			
			sshService.addSshAccess(userName, pubKey);
			
		}

		instanceList.put(instanceURI, sshService);
		
		return createResponse(resource, sshService);
	}
	
	private Model createResponse(Resource resource, SshService sshservice){
	  Model result = ModelFactory.createDefaultModel();
	  Resource res = result.createResource(resource.getURI());
	  res.addProperty(RDF.type, Omn.Resource);
	  res.addProperty(RDF.type, getAdapterManagedResources().get(0));
	  Property property = res.getModel().createProperty(Omn_lifecycle.hasState.getNameSpace(),Omn_lifecycle.hasState.getLocalName());
	  property.addProperty(RDF.type, OWL.FunctionalProperty);
	  res.addProperty(property, Omn_lifecycle.Ready);
	  
	  UUID randomGenerator = UUID.randomUUID();
	  
	  String uuid = randomGenerator.toString();
	 
	  Resource login = result.createResource( OntologyModelUtil.getResourceNamespace() + "LoginService"+ uuid);
	  res.addProperty(Omn.hasService, login);
	  login.addProperty(RDF.type, Omn_service.LoginService);
	  login.addProperty(Omn_service.port, "22");
	  login.addProperty(Omn_service.hostname, "127.0.0.1");
	  
	  for (String username : sshservice.getUsernames()){
	    login.addProperty(Omn_service.username, username);
	  }
	  login.addProperty(Omn_service.authentication, "ssh-keys");
//	  for(String publickey : sshservice.getPossibleAccesses()){
//	    login.addProperty(Omn_service.authentication, publickey);
//	  }
//	  Property ip = res.getModel().createProperty("http://open-multinet.info/ontology/omn-service#", "ip");
//	  ip.addProperty(RDF.type, OWL.DatatypeProperty);
//	  res.addProperty(ip, "127.0.0.1");
	  
	  return result;
	}

	@Override
	public void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException,
			ProcessingException {
	  
	  instanceList.get(instanceURI).deleteSshAccess();
	  instanceList.remove(instanceURI);
	  
	}

	@Override
	public Model getInstance(String instanceURI)
			throws InstanceNotFoundException {
		SshService sshService = instanceList.get(instanceURI);
		if (sshService == null) {
			throw new InstanceNotFoundException("Instance " + instanceURI
					+ " not found");
		}
		return parseToModel(sshService);
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		for (String uri : instanceList.keySet()) {
			model.add(getInstance(uri));
		}
		return model;
	}

}
