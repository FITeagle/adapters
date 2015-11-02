package org.fiteagle.adapters.epc;

public class IpAddress {

	private String address;
	private String netmask;
	private String type;

	public IpAddress(final String address, final String netmask,
			final String type) {

		this.setAddress(address);
		this.setNetmask(netmask);
		this.setType(type);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
