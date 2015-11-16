package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;

public class DeviceCharacteristics implements Serializable{
  
  private String url;
  private Registration registration;
  private String manufacturer;
  private String oui;
  private String product_class;
  private String serial_number;
  
  
  public void setURL(String url){
    this.url = url;
  }
  
  public String getURL(){
    return this.url;
  }
  
  public void setRegistration(Registration registration){
    this.registration = registration;
  }
  
  public Registration getRegistration(){
    return this.registration;
  }
  
  public void setManufacturer(String manufacturer){
    this.manufacturer = manufacturer;
  }
  
  public String getManufacturer(){
    return this.manufacturer;
  }
  
  public void setOUI(String oui){
    this.oui = oui;
  }
  
  public String getOUI(){
    return this.oui;
  }
  
  public void setProduct_class(String product_class){
    this.product_class = product_class;
  }
  
  public String getProduct_class(){
    return this.product_class;
  }
  
  public void setSerial_number(String serial_number){
    this.serial_number = serial_number;
  }
  
  public String getSerial_number(){
    return this.serial_number;
  }
}