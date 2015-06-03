package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_resource;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
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
	protected List physicalNodeList = new ArrayList();


	static{
	  

		Model adapterModel = OntologyModelUtil.loadModel(
				"ontologies/sshservice.ttl", IMessageBus.SERIALIZATION_TURTLE);

		ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(
				RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		if (adapterIterator.hasNext()) {
			adapter = adapterIterator.next();
		}

		createDefaultAdapterInstance(adapterModel);
	}
	
	private static String checkNodeName(){
		
		return "1";
	}

	private static void createDefaultAdapterInstance(Model adapterModel) {
	  
	  createPropertiesFile();
	  Config config = new Config("SshServiceAdapter");
	  
	  for(Map.Entry<Object, Object> entry : config.readProperties().entrySet()){
	    if(entry.getKey().toString().equals(ISshService.COMPONENT_ID)){
	      
        String componentIdProperty = (String) entry.getValue();
	      if(componentIdProperty.contains(",")){
	        String[] compIds = componentIdProperty.split("\\,");

	        for(int counter = 0; counter < compIds.length; counter++){
	          String component_id = compIds[counter];
	          System.out.println("counter is " + counter + " component_id " + component_id);
	          createAdapterInstance(component_id, adapterModel);

	        }
	       
        } else {
          createAdapterInstance(componentIdProperty, adapterModel);
        }
	        
	      
	    }
	  }
	  

	}

	private static void createAdapterInstance(String component_id, Model adapterModel){
	  Resource adapterInstancee = adapterModel.createResource(component_id);
    adapterInstancee.addProperty(RDF.type, adapter);
    adapterInstancee.addProperty(RDFS.label, adapterInstancee.getLocalName());
    adapterInstancee.addProperty(RDFS.comment,
        "A SSH Adapter that can create and manage SSH-Accesses.");
    Resource testbed = adapterModel
        .createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
    adapterInstancee.addProperty(Omn_federation.partOfFederation, testbed);

    StmtIterator resourceIterator = adapter
        .listProperties(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
      Resource resource = resourceIterator.next().getObject()
          .asResource();

      adapterInstancee.addProperty(Omn_lifecycle.canImplement, resource);
      ResIterator propertiesIterator = adapterModel
          .listSubjectsWithProperty(RDFS.domain, resource);
      while (propertiesIterator.hasNext()) {
        Property p = adapterModel.getProperty(propertiesIterator.next()
            .getURI());
      }
    }
 
    new SshServiceAdapter(adapterInstancee, adapterModel);
    
	}
	
	private static void createPropertiesFile() {
		File file = new File(IConfig.PROPERTIES_DIRECTORY
				+ "/SshServiceAdapter.properties");

		if (!file.exists()) {
//			createDefaultConfiguration("SshServiveAdapter");
			Config config = new Config("SshServiceAdapter");

			config.createPropertiesFile();
			config.deleteProperty(ISshService.PASSWORD);
			config.setNewProperty(ISshService.PASSWORD, "");
			config.setNewProperty(ISshService.IP, ISshService.LOCALHOST_IP);
			config.setNewProperty(ISshService.PRIVATE_KEY_PATH, "");
			config.setNewProperty(ISshService.PRIVATE_KEY_PASSWORD, "");
			config.setNewProperty(ISshService.USERNAME, "");
			config.setNewProperty(ISshService.COMPONENT_ID, OntologyModelUtil.getResourceNamespace()+ ISshService.DEFAULT_ADAPTER_INSTANCE);
		}

			}

	
	private SshServiceAdapter(Resource adapterInstance, Model adapterModel){
		this.adapterInstance = adapterInstance;
		this.adapterModel = adapterModel;
		adapterInstances.put(adapterInstance.getURI(), this);

	}

	public SshServiceAdapter() {
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
		ResIterator resIteratorKey = configureModel
				.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
		while (resIteratorKey.hasNext()) {

			Resource resource = resIteratorKey.nextResource();
			model.add(createResponse(resource, instanceList.get(instanceURI), instanceList.get(instanceURI).getSshParameter().getIP()));
		}

		return model;
	}

	@Override
	public Model createInstance(String instanceURI, Model newInstanceModel)
			throws ProcessingException, InvalidRequestException {

		SshService sshService = new SshService(this);

		String pubKey = "";
		String userName = "";
		String adapterInstance = "";
		String ip = "";
		Model result = ModelFactory.createDefaultModel();

		Resource resource = null;

		ResIterator resIteratorKey = newInstanceModel
				.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
		if (!resIteratorKey.hasNext())
			throw new InvalidRequestException("statements are missing ");
		while (resIteratorKey.hasNext()) {

			resource = resIteratorKey.nextResource();
			if (!resource.hasProperty(Omn_service.publickey))
				throw new InvalidRequestException("public key is missing ");
			else {
				pubKey = resource.getProperty(Omn_service.publickey)
						.getLiteral().getString();
				System.out.println("public key is " + pubKey);
			}
			if (!resource.hasProperty(Omn_service.username))
				throw new InvalidRequestException("user name is missing ");
			else {
				userName = resource.getProperty(Omn_service.username)
						.getLiteral().getString();
			}
			adapterInstance = resource.getProperty(Omn_lifecycle.implementedBy).getObject().asResource().getURI();

			ip = sshService.addSshAccess(userName, pubKey, adapterInstance);

		}

		instanceList.put(instanceURI, sshService);

		return createResponse(resource, sshService, ip);
	}

	private Model createResponse(Resource resource, SshService sshservice, String ip) {
		Model result = ModelFactory.createDefaultModel();
		Resource res = result.createResource(resource.getURI());
		res.addProperty(RDF.type, Omn.Resource);
		res.addProperty(RDF.type, getAdapterManagedResources().get(0));
		Property property = res.getModel().createProperty(
				Omn_lifecycle.hasState.getNameSpace(),
				Omn_lifecycle.hasState.getLocalName());
		property.addProperty(RDF.type, OWL.FunctionalProperty);
		res.addProperty(property, Omn_lifecycle.Ready);

		UUID randomGenerator = UUID.randomUUID();

		String uuid = randomGenerator.toString();

		Resource login = result.createResource(OntologyModelUtil
				.getResourceNamespace() + "LoginService" + uuid);
		res.addProperty(Omn.hasService, login);
		login.addProperty(RDF.type, Omn_service.LoginService);
		login.addProperty(Omn_service.port, ISshService.SSH_PORT);
		login.addProperty(Omn_service.hostname, ip);

		for (String username : sshservice.getUsernames()) {
			login.addProperty(Omn_service.username, username);
		}
		login.addProperty(Omn_service.authentication, ISshService.SSH_KEYS);

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
	
	public void testCreateAccess(String pubKey,String username, String adapterInstance){
		SshService sshService = new SshService(this);
		sshService.addSshAccess(username, pubKey, adapterInstance);
		instanceList.put(username, sshService);

	}
	
	public void testDeleteAccess(){
		SshService sshService = instanceList.get("deploytestuser");
		sshService.deleteSshAccess();
		instanceList.remove("deploytestuser");

	}


	@Override
	public void refreshConfig() throws ProcessingException {
		for (String key : instanceList.keySet()){
			instanceList.get(key).refreshConfig();
		}
	}

}
