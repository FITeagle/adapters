package org.fiteagle.adapters.epc;


public class AccessPointName {

	private String networkIdentifier;
	private String operatorIdentifier;

	public AccessPointName(final String networkIdentifier,
			final String operatorIdentifier) {

		this.networkIdentifier = networkIdentifier;
		this.operatorIdentifier = operatorIdentifier;
	}

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

	public void setNetworkIdentifier(String networkIdentifier) {
		this.networkIdentifier = networkIdentifier;
	}

	public String getOperatorIdentifier() {
		return operatorIdentifier;
	}

	public void setOperatorIdentifier(String operatorIdentifier) {
		this.operatorIdentifier = operatorIdentifier;
	}
}
