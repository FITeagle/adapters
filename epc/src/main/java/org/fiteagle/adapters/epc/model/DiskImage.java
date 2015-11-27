package org.fiteagle.adapters.epc.model;

public class DiskImage {

	private String name;
	private String description;

	public DiskImage(final String name, final String description) {

		this.setDescription(description);
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
