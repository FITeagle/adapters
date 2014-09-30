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

	@JsonProperty("images")
	private List<Image> list;

	/**
	 * @return the list
	 */
	public List<Image> getList() {
		return list;
	}
	
	@Override
	public Iterator<Image> iterator() {
		return list.iterator();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Images [list=" + list + "]";
	}

}

