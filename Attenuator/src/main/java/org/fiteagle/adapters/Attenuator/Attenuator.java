package org.fiteagle.adapters.Attenuator;

public class Attenuator {
  
  private String attenuator_url;
  private String attenuator_port;
  private String attenuator_id;
  
  public void set_attenuator_url(String url){
    this.attenuator_url = url;
  }
  
  public String get_attenuator_url(){
    return this.attenuator_url;
  }
  
  public void set_attenuator_port(String port){
    this.attenuator_port = port;
  }
  
  public String get_attenuator_port(){
    return this.attenuator_port;
  }
  
  public void set_attenuator_id(String id){
    this.attenuator_id = id;
  }
  
  public String get_attenuator_id(){
    return this.attenuator_id;
  }
  
}
