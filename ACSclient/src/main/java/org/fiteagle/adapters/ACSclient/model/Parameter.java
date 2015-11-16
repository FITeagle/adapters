package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;

public class Parameter implements Serializable{
  
  private int id;
  private String name;
  private String value;
  private boolean writable;
  private String last_update;
  
  public void setId(int id){
    this.id = id;
  }
  
  public int getId(){
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
  
  public void setWritable(boolean writable){
    this.writable = writable;
  }
  
  public boolean getWritable(){
    return this.writable;
  }
  
  public void setLast_update(String last_update){
    this.last_update = last_update;
  }
  
  public String getLast_update(){
    return this.last_update;
  }
  
}
