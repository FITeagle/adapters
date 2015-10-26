package org.fiteagle.adapters.openstack.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.nova.v2_0.domain.Image;

public class Images implements Serializable {

  private static final long serialVersionUID = -1945648978256231004L;
  
  protected Images(){
  }
  
  public Images(Map<String, List<Image>> imagesMap){
    this.map = imagesMap;
  }
  
  public Images(List<Image> list){
	    this.list = list;
	  }
  
//  @JsonProperty("images")
	private Map<String, List<Image>> map;
	private List<Image> list;

	public List<Image> getListForRegion(String region) {
		return map.get(region);
	}
	
	@Override
	public String toString() {
		return "Images [map=" + map + "]";
	}

	public List<Image> getList() {
		// TODO Auto-generated method stub
		return list;
	}

}