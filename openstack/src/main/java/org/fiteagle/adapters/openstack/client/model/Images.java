package org.fiteagle.adapters.openstack.client.model;

/**
 * this class is a copy of com.woorea.openstack.nova.model.Images class from woorea client, which has small extensions.
 */

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)//changed
public class Images implements Iterable<Image>, Serializable {

  private static final long serialVersionUID = -1945698118256231004L;
  
  protected Images(){
  }
  
  public Images(List<Image> imageList){
    this.list = imageList;
  }
  
  @JsonProperty("images")
	private List<Image> list;

	public List<Image> getList() {
		return list;
	}
	
	@Override
	public Iterator<Image> iterator() {
		return list.iterator();
	}
	
	@Override
	public String toString() {
		return "Images [list=" + list + "]";
	}

}

