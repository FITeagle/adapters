package org.fiteagle.adapters.ACSclient;

import info.openmultinet.ontology.vocabulary.Acs;
import info.openmultinet.ontology.vocabulary.Omn_component;
import info.openmultinet.ontology.vocabulary.Omn_domain_geni_fire;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.fiteagle.adapters.ACSclient.ACSclientAdapter;
import org.fiteagle.adapters.ACSclient.model.ConfigureRequest;
import org.fiteagle.adapters.ACSclient.model.Device;
import org.fiteagle.adapters.ACSclient.model.JobInstance;
import org.fiteagle.adapters.ACSclient.model.Parameter;
import org.fiteagle.adapters.ACSclient.model.ParameterPlusValuesMap;
import org.fiteagle.adapters.ACSclient.model.RequestedParameters;
import org.fiteagle.api.core.IMessageBus;

import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ACSclient implements Runnable{
  
  private final transient ACSclientAdapter owningAdapter;
  private final String instanceName;
  private final List<Device> devices = new ArrayList<Device>();
  
  private static final String JOBS = "/jobs/";
  private static final String COMMIT = "/commit";
  
  public ACSclient(final ACSclientAdapter owningAdapter, final String instanceName){
    
    this.owningAdapter = owningAdapter;
    this.instanceName = instanceName;
  }
  
  public String getInstanceName() {
    return this.instanceName;
    }
  
  public void parseConfigureModel(Model configureModel) {
    StmtIterator stmtIterator = configureModel.listStatements(new SimpleSelector((Resource) null, Acs.hasDevice, (Object) null));
    while(stmtIterator.hasNext()){
      Statement hasDeviceStatement = stmtIterator.nextStatement();
      Resource device_res = hasDeviceStatement.getObject().asResource();
      
      String deviceId = parseDeviceID(device_res);
      Device device = new Device(Integer.parseInt(deviceId));
      
      List<Resource> parameters_List = parseParametersResources(device_res);
      for(Resource parameter_res : parameters_List){
        String parameter_name = parseParameterName(parameter_res);
        
        String parameter_value = parseParameterValue(parameter_res);
        
        device.setParameter(parameter_name, parameter_value);
      }
      this.devices.add(device);
    }
  }
  
  private String parseDeviceID(Resource device){
    return device.getProperty(Acs.hasAcsId).getLiteral().getString();
  }
  
  private List<Resource> parseParametersResources(Resource device){
    final List<Resource> parameters = new ArrayList<Resource>();
    
    StmtIterator paramIterator = device.listProperties(Acs.hasParameter);
    while(paramIterator.hasNext()){
      parameters.add(paramIterator.nextStatement().getObject().asResource());
    }
    return parameters;
  }
  
  private String parseParameterName(Resource parameter){
    return parameter.getProperty(Acs.hasParamName).getLiteral().getString();
  }
  
  private String parseParameterValue(Resource parameter){
    return parameter.getProperty(Acs.hasParamValue).getLiteral().getString();
  }
  
  
  public void terminate() {
    
  }
  
  @Override
  public void run(){
    ConfigureRequest configRequest = prepareConfigureRequest();
    
    String jobID = getJobID(configRequest);
    
    RequestedParameters requestedParameters = getRequestedParameters();
    
    // send pATH request
    
   
    
//    this.owningAdapter.notifyListeners(model, null, IMessageBus.TYPE_INFORM, null);
  }
  
  public String getJobID(ConfigureRequest configRequest){
    Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    WebTarget target = client.target(this.owningAdapter.getURL()+this.JOBS);
    JobInstance jobInstance = target.request().post(Entity.entity(configRequest, MediaType.APPLICATION_JSON), JobInstance.class);
    return jobInstance.getID();
    
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
          parameter.setValue(parameterPlusValuesMap.getValue(requestedParameter.getValue()));
          parameter.setLast_update(getDate());
          parameters.setParameters(parameter);
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
