package org.fiteagle.adapters.ACSclient.model;

import java.util.ArrayList;
import java.util.List;

public class RequestedParameters{
  
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
