package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;

public class Registration implements Serializable{
  
  private String unique_id;
  private String name;
  private String location;
  private String comment;
  
  public void setUnique_id(String unique_id){
    this.unique_id = unique_id;
  }
  
  public String getUnique_id(){
    return this.unique_id;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public void setLocation(String location){
    this.location = location;
  }
  
  public String getLocation(){
    return this.getLocation();
  }
  
  public void setComment(String comment){
    this.comment = comment;
  }
  
  public String getComment(){
    return this.comment;
  }
}
