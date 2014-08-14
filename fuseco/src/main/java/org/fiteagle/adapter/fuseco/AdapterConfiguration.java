package org.fiteagle.adapter.fuseco;

import java.util.List;
import java.util.LinkedList;
import java.util.Date;
	
/**
 * defines list of users
 * @author alaa.alloush
 *
 */

public class AdapterConfiguration {

	private List<AdapterUserInfo> users;
	private Date expirationTime;
	
	public AdapterConfiguration(){
		this.users = new LinkedList<AdapterUserInfo>();
		this.expirationTime = null;
	}
	public List<AdapterUserInfo> getUsers() {
		return users;
	}
	public void setUsers(List<AdapterUserInfo> users) {
		this.users = users;
	}
	
	/**
	 * To do : method to add users to the list
	 */

}
