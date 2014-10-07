package org.fiteagle.adapter.fuseco;

/**
 * this class defines the name and capabilities (description) of the resource
 * @author alaa.alloush
 *
 */
public class ResourceInformation {

	private String resourceName;
	private String resourceCapability;
	
	public ResourceInformation(){
		this.resourceName = "Fuseco client";
		this.resourceCapability = "hardware";
	}
	
	public String getName() {
		return resourceName;
	}
	
	public void setName(String resourceName){
		this.resourceName = resourceName;
	}
	
	public String getResourceCapability() {
		return resourceCapability;
	}
	
	public void setresourceCapability(){
		this.resourceCapability = resourceCapability;
	}
	public String toString(){
		return "Resouce name: " + getName() + ", Resource capability: " + getResourceCapability() ;
	}
}
