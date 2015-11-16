package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestedParameters implements Serializable{
  
  private List<Parameter> parameters;
  
  public RequestedParameters(){
    this.parameters = new ArrayList<Parameter>();
  }
  
  public void setParameters(Parameter parameter){
    this.parameters.add(parameter);
  }
  
  public List<Parameter> getParameters(){
    return this.parameters;
  }
  
  
}
