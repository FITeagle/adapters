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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;
import org.hornetq.utils.json.JSONArray;
import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class SshServiceAdapter extends AbstractAdapter {
  
  private static final List<Property> sshServiceControlProperties = new ArrayList<Property>();
  
  private final HashMap<String, SshService> instanceList = new HashMap<String, SshService>();
  
  private static Logger LOGGER  = Logger.getLogger(SshServiceAdapter.class.toString());
  
  private SshParameter sshParameters;
  

  public SshServiceAdapter(Model adapterModel, Resource adapterABox) {
    
    this.uuid = UUID.randomUUID().toString();
    this.adapterTBox = adapterModel;
    
    parseSshParameters(adapterABox);
    
    Model model = ModelFactory.createDefaultModel();
    Resource adapterInstance = model.createResource(adapterABox.getURI());
    
    this.adapterABox = adapterInstance;
    Resource adapterType =getAdapterClass();
    this.adapterABox.addProperty(RDF.type,adapterType);
    this.adapterABox.addProperty(RDFS.label,  this.adapterABox.getLocalName());
    this.adapterABox.addProperty(RDFS.comment, "A ssh service adapter that can create and manage SSH services to different machines.");

    NodeIterator resourceIterator = this.adapterTBox.listObjectsOfProperty(Omn_lifecycle.implements_);
    if (resourceIterator.hasNext()) {
        Resource resource = resourceIterator.next().asResource();

        this.adapterABox.addProperty(Omn_lifecycle.canImplement, resource);
        ResIterator propertiesIterator = adapterTBox.listSubjectsWithProperty(RDFS.domain, resource);
        while (propertiesIterator.hasNext()) {
            Property p = adapterTBox.getProperty(propertiesIterator.next().getURI());
            sshServiceControlProperties.add(p);
        }
    }
  }

  
  private void parseSshParameters(Resource adapterABox){
    
    sshParameters = new SshParameter();
    
    Model model = ModelFactory.createDefaultModel();
    
    StmtIterator stmtIterator = adapterABox.getModel().listStatements();
    
    while(stmtIterator.hasNext()){
      
      Statement statement = stmtIterator.next();
      
      switch(statement.getPredicate().getLocalName()){
        
        case ISshService.COMPONENT_ID:
          sshParameters.setComponentID(statement.getString());
          break;
          
        case ISshService.IP:
          sshParameters.setIP(statement.getString());
          break;
          
        case ISshService.USERNAME:
          sshParameters.setAccessUsername(statement.getString());
          break;
          
        case ISshService.PRIVATE_KEY_PATH:
          sshParameters.setPrivateKeyPath(statement.getString());
          break;
          
        case ISshService.PRIVATE_KEY_PASSWORD:
          sshParameters.setPrivateKeyPassword(statement.getString());
          break;
          
        case ISshService.PASSWORD:
          sshParameters.setPassword(statement.getString());
          break;
          
      }
    }
      
  }
  
  public SshParameter getSshParameters(){
    return this.sshParameters;
  }
	
//	private static void createPropertiesFile() {
//		File file = new File(IConfig.PROPERTIES_DIRECTORY
//				+ "/SshServiceAdapter.properties");
//
//		if (!file.exists()) {
//			Config config = new Config("SshServiceAdapter");
//
//			Map<String, Object> propertiesMap = new HashMap<String, Object>();
//	    propertiesMap.put(IConfig.KEY_HOSTNAME, IConfig.DEFAULT_HOSTNAME);
//	    propertiesMap.put(IConfig.LOCAL_NAMESPACE, IConfig.LOCAL_NAMESPACE_VALUE);
//	    propertiesMap.put(IConfig.RESOURCE_NAMESPACE,IConfig.RESOURCE_NAMESPACE_VALUE);
//	    
//	    List<Map<String, String>> adapterInstancesList = new LinkedList<Map<String, String>>();
//	    Map<String, String> adapterInstanceMap = new HashMap<String, String>();
//	    adapterInstanceMap.put(ISshService.COMPONENT_ID, OntologyModelUtil.getResourceNamespace()+ ISshService.DEFAULT_ADAPTER_INSTANCE);
//	    adapterInstanceMap.put(ISshService.USERNAME, "");
//	    adapterInstanceMap.put(ISshService.PASSWORD, "");
//	    adapterInstanceMap.put(ISshService.IP, ISshService.LOCALHOST_IP);
//	    adapterInstanceMap.put(ISshService.PRIVATE_KEY_PATH, "");
//	    adapterInstanceMap.put(ISshService.PRIVATE_KEY_PASSWORD, "");
//	    
//	    adapterInstancesList.add(adapterInstanceMap);
//	    
//	    propertiesMap.put(ISshService.ADAPTER_INSTANCES, adapterInstancesList);
//	    
//	    Properties property = new Properties();
//	    property.putAll(propertiesMap);
//	    config.writeProperties(property);
//	    }
//		
//		}


	

//	protected Model parseToModel(SshService sshService) {
//		Resource resource = ModelFactory.createDefaultModel().createResource(
//				sshService.getInstanceName());
//		resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
//		resource.addProperty(RDF.type, Omn.Resource);
//		resource.addProperty(RDFS.label, resource.getLocalName());
//		resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);
//
//		return resource.getModel();
//	}


//	@Override
//	public Model updateInstance(String instanceURI, Model configureModel)
//			throws InvalidRequestException, ProcessingException {
//
//		Model model = ModelFactory.createDefaultModel();
//		ResIterator resIteratorKey = configureModel
//				.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
//		while (resIteratorKey.hasNext()) {
//
//			Resource resource = resIteratorKey.nextResource();
//			model.add(createResponse(resource, instanceList.get(instanceURI), instanceList.get(instanceURI).getSshParameter().getIP()));
//		}
//
//		return model;
//	}
	
  @Override
  public Model updateInstance(String instanceURI, Model configureModel) {
   
      return ModelFactory.createDefaultModel();
  }
  
  

	@Override
	public Model createInstance(String instanceURI, Model modelCreate)
			throws ProcessingException, InvalidRequestException {

		SshService sshService = new SshService(this, instanceURI);
		
		StmtIterator iter = modelCreate.listStatements();
    while(iter.hasNext()){
      sshService.updateProperty(iter.next());
    }
		instanceList.put(instanceURI, sshService);
		
		sshService.addSshAccess();
		return createResponse(sshService);
		
		
		
//		String pubKey = "";
//		String userName = "";
//		String adapterInstance = "";
//		String ip = "";
//		Model result = ModelFactory.createDefaultModel();
//
//		Resource resource = null;
//
//		ResIterator resIteratorKey = modelCreate
//				.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
//		if (!resIteratorKey.hasNext())
//			throw new InvalidRequestException("statements are missing ");
//		while (resIteratorKey.hasNext()) {
//
//			resource = resIteratorKey.nextResource();
//			if (!resource.hasProperty(Omn_service.publickey))
//				throw new InvalidRequestException("public key is missing ");
//			else {
//				pubKey = resource.getProperty(Omn_service.publickey)
//						.getLiteral().getString();
//				System.out.println("public key is " + pubKey);
//			}
//			if (!resource.hasProperty(Omn_service.username))
//				throw new InvalidRequestException("user name is missing ");
//			else {
//				userName = resource.getProperty(Omn_service.username)
//						.getLiteral().getString();
//			}
//			adapterInstance = resource.getProperty(Omn_lifecycle.implementedBy).getObject().asResource().getURI();
//
//			sshService.addSshAccess(userName, pubKey);
//
//		}
//		return createResponse(sshService);
	}

	private Model createResponse(SshService sshservice) {
		Model result = ModelFactory.createDefaultModel();
		Resource res = result.createResource(sshservice.getInstanceName());
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
		login.addProperty(Omn_service.hostname, sshParameters.getIP());

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
  public Resource getAdapterABox() {
      return adapterABox;
  }

  @Override
  public Model getAdapterDescriptionModel() {
      return adapterTBox;
  }

  @Override
  public void updateAdapterDescription() {
  }
  
	@Override
	public Model getInstance(String instanceURI)
			throws InstanceNotFoundException {
//		SshService sshService = instanceList.get(instanceURI);
//		if (sshService == null) {
//			throw new InstanceNotFoundException("Instance " + instanceURI
//					+ " not found");
//		}
//		return parseToModel(sshService);
	  return ModelFactory.createDefaultModel();
	}
  
  
	@Override
	public Model getAllInstances() throws InstanceNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		for (String uri : instanceList.keySet()) {
			model.add(getInstance(uri));
		}
		return model;
	}

	
  @Override
  public void refreshConfig() throws ProcessingException {
      for (String key : instanceList.keySet()){
        instanceList.get(key).refreshConfig();
        }

  }

  @Override
  public String getId() {
      return this.uuid;
  }

  @Override
  public void shutdown() {

  }

  @Override
  public void configure(Config configuration) {

  }
	
}
