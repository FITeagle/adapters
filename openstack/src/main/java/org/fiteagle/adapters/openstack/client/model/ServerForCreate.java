package org.fiteagle.adapters.openstack.client.model;

/**
 * this class is a copy of com.woorea.openstack.nova.model.ServerForCreate class from woorea client, which has small extensions.
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

import com.woorea.openstack.nova.model.PersonalityFile;
@JsonIgnoreProperties(ignoreUnknown = true) //changed
@JsonRootName("server")
public class ServerForCreate implements Serializable {
	
  private static final long serialVersionUID = 113539200883523167L;

  public static final class SecurityGroup implements Serializable {
		
    private static final long serialVersionUID = -4920416303308070588L;
    private String name;

		public SecurityGroup() {
		}
		
		public SecurityGroup(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
//	--------------------changed------------
	public static final class Network implements Serializable { 
		
    private static final long serialVersionUID = 581280677901622617L;

    private String uuid;
		
		private String fixed_ip;
		
		private String port;

		public Network() {
		}
		
		public Network(String uuid, String fixed_ip, String port) {
			this.uuid = uuid;
			this.fixed_ip = fixed_ip;
			this.port = port;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getFixed_ip() {
			return fixed_ip;
		}

		public void setFixed_ip(String fixed_ip) {
			this.fixed_ip = fixed_ip;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}
		
	}
	
//	-------------------/changed------------------
	
	private String name;
	
	private String adminPass;
	
	private String imageRef;
	
	private String flavorRef;
	
	private String accessIPv4;
	
	private String accessIPv6;
	
	private Integer min;
	
	private Integer max;
	
	private String diskConfig;
	
	@JsonProperty("key_name")
	private String keyName;
	
	private List<PersonalityFile> personality = new ArrayList<PersonalityFile>();
	
	private Map<String, String> metadata = new HashMap<String, String>();
	
	@JsonProperty("security_groups")
	private List<SecurityGroup> securityGroups;
	
	@JsonProperty("networks")		//changed
	private List<Network> networks;	//changed
	
	@JsonProperty("user_data")
	private String userData;
	
	@JsonProperty("availability_zone")
	private String availabilityZone;

	@JsonProperty("config_drive")
	private boolean configDrive;

	public ServerForCreate(){
	}

	public ServerForCreate(String name, String flavorRef, String imageRef, String keyName){
	  this.name = name;
	  this.flavorRef = flavorRef;
	  this.imageRef = imageRef;
	  this.keyName = keyName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdminPass() {
		return adminPass;
	}

	public void setAdminPass(String adminPass) {
		this.adminPass = adminPass;
	}

	public String getImageRef() {
		return imageRef;
	}

	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}

	public String getFlavorRef() {
		return flavorRef;
	}

	public void setFlavorRef(String flavorRef) {
		this.flavorRef = flavorRef;
	}

	public String getAccessIPv4() {
		return accessIPv4;
	}

	public void setAccessIPv4(String accessIPv4) {
		this.accessIPv4 = accessIPv4;
	}

	public String getAccessIPv6() {
		return accessIPv6;
	}

	public void setAccessIPv6(String accessIPv6) {
		this.accessIPv6 = accessIPv6;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public String getDiskConfig() {
		return diskConfig;
	}

	public void setDiskConfig(String diskConfig) {
		this.diskConfig = diskConfig;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public List<PersonalityFile> getPersonality() {
		return personality;
	}

	public void setPersonality(List<PersonalityFile> personality) {
		this.personality = personality;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public List<SecurityGroup> getSecurityGroups() {
		if(securityGroups == null) {
			securityGroups = new ArrayList<SecurityGroup>();
		}
		return securityGroups;
	}

	
//	----------------------changed----------------------
	public List<Network> getNetworks() {
		if(networks == null) {
			networks = new ArrayList<Network>();
		}
		return networks;
	}
	
//	----------------------/changed----------------------
	
	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public boolean isConfigDrive() {
		return configDrive;
	}

	public void setConfigDrive(boolean configDrive) {
		this.configDrive = configDrive;
	}
}
