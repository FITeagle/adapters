package org.fiteagle.adapter.fuseco;

/**
 * defines adapter status
 * @author alaa.alloush
 *
 */
public enum AdapterStatus {

Available("available"),Reserved("reserved"),InUse("inUse");
	
	private String status;

	private AdapterStatus(String status){
		this.status = status;
	}
	
	@Override 
	public String toString(){
		return this.status;
	}

}
