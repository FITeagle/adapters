package org.fiteagle.adapters.ACSclient;

import info.openmultinet.ontology.vocabulary.Acs;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fiteagle.adapters.ACSclient.Exceptions.BadConfigureRequest;
import org.fiteagle.adapters.ACSclient.model.ConfigureRequest;
import org.fiteagle.adapters.ACSclient.model.Device;
import org.fiteagle.adapters.ACSclient.model.JobInstance;
import org.fiteagle.adapters.ACSclient.model.Parameter;
import org.fiteagle.adapters.ACSclient.model.ParameterPlusValuesMap;
import org.fiteagle.adapters.ACSclient.model.RequestedParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ACSclient implements Runnable{
  
  private final transient ACSclientAdapter owningAdapter;
  private final String instanceName;
  private final List<Device> devices = new ArrayList<Device>();
  
  private static final String JOBS = "/jobs/";
  private static final String COMMIT = "/commit/";
  
  private Logger LOGGER  = LoggerFactory.getLogger(this.getClass().getName());
  
  public ACSclient(final ACSclientAdapter owningAdapter, final String instanceName){
    
    this.owningAdapter = owningAdapter;
    this.instanceName = instanceName;
  }
  
  public String getInstanceName() {
    return this.instanceName;
    }
  
  public void parseConfigureModel(Model configureModel) {
    
    this.devices.clear();
    
    Device device = new Device(Integer.parseInt(this.owningAdapter.getDevice()));
    
    if(configureModel.contains((Resource) null, Acs.adminState)){
      device.setParameter("adminState", configureModel.getProperty((Resource) null, Acs.adminState).getString());
    }
    
    this.devices.add(device);
    
  }
  
  
  @Override
  public void run(){
    
    ConfigureRequest configRequest = prepareConfigureRequest();
    
    String jobID = getJobID(configRequest);
    LOGGER.info("JOB ID " + jobID);
    
    RequestedParameters requestedParameters = getRequestedParameters();
    
    configureParameter(requestedParameters, jobID);

    LOGGER.info("PATCH request is successful");
    commitConfiguration(jobID);
    
    if (getConfigureStatus(jobID)){
      LOGGER.info("JOB HAS BEEN SUCCESSFULLY COMMITTED");
    }
    else{
      LOGGER.warn("Configurations have not been committed successfully");
    }
    

  }
  
  public String getJobID(ConfigureRequest configRequest){
    Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    WebTarget target = client.target(this.owningAdapter.getURL()+this.JOBS);
    
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonString = gson.toJson(configRequest);
    LOGGER.info("CONFIG REQuest " + jsonString);
    
    JobInstance jobInstance = target.request().post(Entity.json(jsonString), JobInstance.class);
    
    if(jobInstance.getID() == null || jobInstance.getID().isEmpty()){
      throw new BadConfigureRequest("No job ID has been assigned.");
    }
    return jobInstance.getID();
    
  }
  
  
  public void configureParameter(RequestedParameters requestedParameters, String jobID){
    Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    WebTarget target = client.target(this.owningAdapter.getURL()+this.JOBS+jobID+"/");
    LOGGER.info("SENDING PATCH REQUEST ...");
    Response response = target.request().method("PATCH", Entity.entity(requestedParameters, MediaType.APPLICATION_JSON));
   
  }
  
  public void commitConfiguration(String jobID){
    Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    WebTarget target = client.target(this.owningAdapter.getURL()+this.JOBS+jobID+this.COMMIT);
    LOGGER.info("committing configuration ...");
    Response response = target.request().get();
    }
  
  public boolean getConfigureStatus(String jobID){
    Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    WebTarget target = client.target(this.owningAdapter.getURL()+this.JOBS+jobID+"/");
    JobInstance jobInstance = target.request().get(JobInstance.class);
    return jobInstance.getIs_committed();
  }
  
  public ConfigureRequest prepareConfigureRequest(){
    ConfigureRequest configRequest = new ConfigureRequest();
    for(Device device : this.devices){
      configRequest.setDevices(device.getDevice());
    }
    return configRequest;
  }
  
  public RequestedParameters getRequestedParameters(){
    RequestedParameters parameters = new RequestedParameters();
    Device device = this.devices.get(0);
      for(Map.Entry<String, String> requestedParameter : device.getParameters().entrySet()){
        if(this.owningAdapter.getParametersNames().containsKey(requestedParameter.getKey())){
          ParameterPlusValuesMap parameterPlusValuesMap = this.owningAdapter.getParametersNames().get(requestedParameter.getKey());
          Parameter parameter = parameterPlusValuesMap.getParameter();
          
          if(!parameterPlusValuesMap.getValues().containsKey(requestedParameter.getValue())){
            throw new BadConfigureRequest("The requested value " + requestedParameter.getValue() + " can't be matched.");
          }
          
          parameter.setValue(parameterPlusValuesMap.getValue(requestedParameter.getValue()));
          parameter.setLast_update(getDate());
          parameters.setParameters(parameter);
        } else {
          throw new BadConfigureRequest("Parameter name " + requestedParameter.getKey() + " is not supported.");
        }
      }
   return parameters;
  }

  private String getDate(){
    Date date = new Date();
    long currentTime = date.getTime();
    Date currentDate = new Date(currentTime);
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(currentDate);
  }
  
  
}
