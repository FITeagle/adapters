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
	
	
  @Override
  public Model updateInstance(String instanceURI, Model configureModel) {
   
    Model model = ModelFactory.createDefaultModel();
    model.add(createResponse(instanceList.get(instanceURI)));
    return model;

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
