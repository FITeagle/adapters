package org.fiteagle.adapters.common;

import java.util.prefs.Preferences;

public class SSHDeployAdapterConfiguration {

	private static SSHDeployAdapterConfiguration sshDeployAdapterConfig;
	private Preferences preferences;

	public SSHDeployAdapterConfiguration() {
		this.preferences = Preferences.userNodeForPackage(getClass());

	}

	public static SSHDeployAdapterConfiguration getInstance() {
		if (sshDeployAdapterConfig == null)
			sshDeployAdapterConfig = new SSHDeployAdapterConfiguration();
		return sshDeployAdapterConfig;
	}

	private void addPreference(String key, String value) {
		preferences.put(key, value);
	}

	private String getPreference(String key) {
		return preferences.get(key, null);
	}

	
	public void setEpcClientIP(String ip) {
		addPreference("epcClientIP", ip);
	}
	public String getEpcClientIP() {
		return getPreference("epcClientIP");
	}
	public void removeEpcClientIP() {
		preferences.remove("epcClientIP");
	}
	
	
	public void setEpcServerIP(String ip) {
		addPreference("epcServerIP", ip);
	}
	public String getEpcServerIP() {
		return getPreference("epcServerIP");
	}
	public void removeEpcServerIP() {
		preferences.remove("epcServerIP");
	}
	
	
	public void setEpcClientUsername(String username) {
		addPreference("epcClientUsername", username);
	}
	public String getEpcClientUsername() {
		return getPreference("epcClientUsername");
	}
	public void removeEpcClientUsername() {
		preferences.remove("epcClientUsername");
	}
	
	
	public void setEpcServerUsername(String username) {
		addPreference("epcServerUsername", username);
	}
	public String getEpcServerUsername() {
		return getPreference("epcServerUsername");
	}
	public void removeEpcServerUsername() {
		preferences.remove("epcServerUsername");
	}
	
	
	public void setEpcClientPassword(String passwords) {
		addPreference("epcClientPassword", passwords);
	}
	public String getEpcClientPassword() {
		return getPreference("epcClientPassword");
	}
	public void removeEpcClientPassword() {
		preferences.remove("epcClientPassword");
	}
	
	
	public void setEpcServerPassword(String passwords) {
		addPreference("epcServerPassword", passwords);
	}
	public String getEpcServerPassword(){
		return getPreference("epcServerPassword");
	}
	public void removeEpcServerPassword(){
		preferences.remove("epcServerPassword");
	}
	
	
	public void setEpcClientPort(String ports) {
		addPreference("epcClientPort", ports);
	}
	public String getEpcClientPort() {
		return getPreference("epcClientPort");
	}
	public void removeEpcClientPort() {
		preferences.remove("epcClientPort");
	}
	
	
	public void setEpcServerPort(String port) {
		addPreference("epcServerPort", port);
	}
	public String getEpcServerPort() {
		return getPreference("epcServerPort");
	}
	public void removeEpcServerPort() {
		preferences.remove("epcServerPort");
	}
	
	
/*	
	public void setSsh_keys(String sshKeys) {
		addPreference("ssh_keys", sshKeys);
	}

	public void setCountries(String countries) {
		addPreference("countries", countries);
	}

	public void setLatitudes(String latitudes) {
		addPreference("latitudes", latitudes);
	}

	public void setLongitues(String longitudes) {
		addPreference("longitudes", longitudes);
	}

	public void setHardwareTypes(String hardwareTypes) {
		addPreference("hardware_types", hardwareTypes);
	}

	public String getHardwareTypes() {
		return getPreference("hardware_types");
	}

	public String getSsh_keys() {
		return getPreference("ssh_keys");
	}

	public String getCountries() {
		return getPreference("countries");
	}

	public String getLatitudes() {
		return getPreference("latitudes");
	}

	public String getLongitues() {
		return getPreference("longitudes");
	}

	public void removeHardwareTypes() {
		preferences.remove("hardware_types");
	}


	public void removeSsh_keys() {
		preferences.remove("ssh_keys");
	}

	public void removeCountries() {
		preferences.remove("countries");
	}

	public void removeLatitudes() {
		preferences.remove("latitudes");
	}

	public void removeLongitues() {
		preferences.remove("longitudes");
	}

	*/
	
}
