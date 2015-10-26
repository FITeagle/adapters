package org.fiteagle.adapters.openstack.client.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jclouds.openstack.nova.v2_0.domain.Flavor;


public class Flavors implements Serializable{

//	@JsonProperty("flavors")
	private List<Flavor> list;
	private Map<String,List<Flavor>> map;

	/**
	 * @return the list
	 */
	public List<Flavor> getListForRegion(String region) {
		return map.get(region);
	}
	
	public Map<String,List<Flavor>> getMap() {
		return map;
	}
	
	public List<Flavor> getList() {
		return list;
	}
	
	public Flavors (Map<String,List<Flavor>> map){
	this.map = map;
	}
	
	public Flavors (List<Flavor> list){
	this.list =list;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Flavors [map=" + map + "]";
//	String answer=new String ("Flavors [map=");
//		for (String region: map.keySet()){
//		answer.concat(region).concat("\n");
//			for (Flavor flavor : map.get(region)){
//				answer.concat(flavor.toString() +"\n");	
//			}
//		}
//		answer.concat("]");
//	return answer;
	}

}
