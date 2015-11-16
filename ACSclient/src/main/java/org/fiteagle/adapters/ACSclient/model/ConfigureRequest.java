package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigureRequest implements Serializable{
  
  private List<Integer> devices = new ArrayList<Integer>();
  private List<Parameter> parameters = new ArrayList<Parameter>();
  
  
  public void setDevices(int deviceId){
    this.devices.add(deviceId);
  }
  
  public List<Integer> getDevicesIDs(){
    return this.devices;
  }
  
  public void setParameters(Parameter parameter){
    this.parameters.add(parameter);
  }
  
  public List<Parameter> getParameters(){
    return this.parameters;
  }
  
}
