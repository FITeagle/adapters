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
  
}
