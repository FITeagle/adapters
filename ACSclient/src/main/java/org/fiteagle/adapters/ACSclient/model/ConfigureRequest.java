package org.fiteagle.adapters.ACSclient.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigureRequest {
  
  private List<Integer> devices;
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
