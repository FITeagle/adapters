package org.fiteagle.adapters.ACSclient.model;

import java.io.Serializable;
import java.util.List;

public class Command implements Serializable{
  
  private String id;
  private String action;
  private String result;
  private DeviceCharacteristics device;
  private String job;
  private String created;
  private boolean started;
  private boolean finished;
  private List<Parameter> parameters;
  
  
  
}
