package org.fiteagle.adapters.openstack.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.nova.v2_0.domain.Server;


//@JsonIgnoreProperties(ignoreUnknown = true) //changed
public class Servers implements Serializable {

  private static final long serialVersionUID = -5868007654154799223L;
//  @JsonProperty("servers")
  private Map<String,List<Server>> map;
  private List<Server> list;
  
  public Servers (Map<String,List<Server>> map){
	  this.map = map;
  }
  
  public Servers (List<Server> list){
	  this.list = list;
  }

  public List<Server> getList() {
    return list;
  }
  
//  @Override
//  public Iterator<Server> iterator() {
//    return list.iterator();
//  }
  
  @Override
  public String toString() {
    return "Servers [map=" + map + "]";
  }

}
