package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Command implements Serializable{
  
  private Object id;
  private String action;
  private String result;
  private DeviceCharacteristics device;
  private int job;
  private String created;
  private boolean started;
  private boolean finished;
  private List<Parameter> parameters;
  
  
  public void setId(Object id){
    this.id = id;
  }
  
  public Object getID(){
    return this.id;
  }
  
  public void setAction(String action){
    this.action = action;
  }
  
  public String getAction(){
    return this.action;
  }
  
  public void setResult(String result){
    this.result= result;
  }
  
  public String getResult(){
    return this.result;
  }
  
  public void setDeviceCharacteristics(DeviceCharacteristics deviceChara){
    this.device = deviceChara;
  }
  
  public DeviceCharacteristics getDeviceCharacteristics(){
    return this.device;
  }
  
  public void setJob(int job){
    this.job = job;
  }
  
  public int getJob(){
    return this.job;
  }
  
  public void setCreated(String created){
    this.created = created;
  }
  
  public String getCreated(){
    return this.created;
  }
  
  public void setStarted(boolean started){
    this.started = started;
  }
  
  public boolean getStarted(){
    return this.started;
  }
  
  public void setFinished(boolean finished){
    this.finished = finished;
  }
  
  public boolean getFinished(){
    return this.finished;
  }
  
  public void setParameter(List<Parameter> parameter){
    this.parameters = parameter;
  }
  
  public List<Parameter> getParameters(){
    return this.parameters;
  }
  
}
