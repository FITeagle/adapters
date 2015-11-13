package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;

public class Parameter implements Serializable{
  
  private String id;
  private String name;
  private String value;
  private String writable;
  private String last_update;
  
  public void setId(String id){
    this.id = id;
  }
  
  public String getId(){
    return this.id;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public String getName(){
    return this.name;
  }
  
  public void setValue(String value){
    this.value= value;
  }
  
  public String getValue(){
    return this.value;
  }
  
  public void setWritable(String writable){
    this.writable = writable;
  }
  
  public String getWritable(){
    return this.writable;
  }
  
  public void setLast_update(String last_update){
    this.last_update = last_update;
  }
  
  public String getLast_update(){
    return this.last_update;
  }
  
}
