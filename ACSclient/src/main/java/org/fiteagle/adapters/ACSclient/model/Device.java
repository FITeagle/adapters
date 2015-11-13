package org.fiteagle.adapters.ACSclient.model;

import java.util.HashMap;
import java.util.Map;

public class Device {
  
  private final int device;
  
  /**
   * this Map contains requested parameters' names and their requested values.
   */
  private final Map<String, String> parameters = new HashMap<String, String>();
  
  public Device(final int device){
    this.device = device;
  }
  
  public int getDevice(){
    return this.device;
  }
  
  public void setParameter(String parameter, String value){
    this.parameters.put(parameter, value);
  }
  
  public Map<String, String> getParameters(){
    return this.parameters;
  }
}
