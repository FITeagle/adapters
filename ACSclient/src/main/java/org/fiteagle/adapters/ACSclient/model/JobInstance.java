package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;
import java.util.List;

public class JobInstance implements Serializable{
  
  private String id;
  
  private boolean is_committed;
  
  private boolean is_success;
  
  private List<Integer> devices;
  
  private List<Command> commands;
  
  private List<Parameter> parameters;
  
  private List<String> choices;
  
  private String created;
  
  private boolean started;
  
  private boolean finished;
  
  
  public void setID(String id){
    this.id = id;
  }
  
  public String getID(){
    return this.id;
  }
  
  public void setIs_committed(boolean is_committed){
    this.is_committed = is_committed;
  }
  
  public boolean getIs_committed(){
    return this.is_committed;
  }
  
  public void setIs_success(boolean is_success){
    this.is_success = is_success;
  }
  
  public boolean getIs_success(){
    return this.is_success;
  }
  
  public void setDevices(List<Integer> devices){
    this.devices = devices;
  }
  
  public List<Integer> getDevices(){
    return this.devices;
  }
  
  public void setCommands(List<Command> commands){
    this.commands = commands;
  }
  
  public List<Command> getCommands(){
    return this.commands;
  }
  
  public List<Parameter> getParameters(){
    return this.parameters;
  }
  
  public void setParameters(List<Parameter> parameters){
    this.parameters = parameters;
  }
  
  public void setChoices(List<String> choices){
    this.choices = choices;
  }
  
  public List<String> getChoices(){
    return this.choices;
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
  
}
