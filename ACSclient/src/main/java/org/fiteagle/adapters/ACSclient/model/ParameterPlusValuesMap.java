package org.fiteagle.adapters.ACSclient.model;

import java.util.HashMap;

public class ParameterPlusValuesMap {
  
  private Parameter parameter;
  private HashMap<String, String> values = new HashMap<String, String>();
  

  public void setParameter(Parameter parameter){
    this.parameter = parameter;
  }
  
  public Parameter getParameter(){
    return this.parameter;
  }
  
  public void setValue(String key, String value){
    this.values.put(key, value);
  }
  
  public HashMap<String, String> getValues(){
    return this.values;
  }
  
  public String getValue(String key){
    return this.values.get(key);
  }
  
}
