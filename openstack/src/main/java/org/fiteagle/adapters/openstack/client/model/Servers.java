package org.fiteagle.adapters.openstack.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * this class is a copy of com.woorea.openstack.nova.model.Servers class from woorea client, which has small extensions.
 */

@JsonIgnoreProperties(ignoreUnknown = true) //changed
public class Servers implements Iterable<Server>, Serializable {

  private static final long serialVersionUID = -5868007654154799223L;
  @JsonProperty("servers")
  private List<Server> list = new ArrayList<>();

  public List<Server> getList() {
    return list;
  }
  
  @Override
  public Iterator<Server> iterator() {
    return list.iterator();
  }
  
  @Override
  public String toString() {
    return "Servers [list=" + list + "]";
  }

}
