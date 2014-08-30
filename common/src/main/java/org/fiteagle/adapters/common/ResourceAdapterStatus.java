package org.fiteagle.adapters.common;

public enum ResourceAdapterStatus {

	Available("available"), Reserved("reserved"), InUse("inUse");

	private String status;

	private ResourceAdapterStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.status;
	}
	
}
