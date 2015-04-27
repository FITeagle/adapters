package org.fiteagle.adapters.sshService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;

public class SshService {
	protected SshServiceAdapter owningAdapter;
	private String instanceName;
	private List<String> ipStrings = new ArrayList<>();
	private List<String> username;

	private Config config;
	private String password;

	public static Map<String, AbstractAdapter> adapterInstances;

	public SshService(SshServiceAdapter owningAdapter) {
		config = new Config("PhysicalNodeAdapter-1");
		this.username = new ArrayList<>();
		this.owningAdapter = owningAdapter;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public List<String> getUsernames() {
		return this.username;
	}

	private void setUsername(String username) {
		this.username.add(username);
	}

	public List<String> getPossibleAccesses() {
		if (ipStrings.isEmpty()) {
			return null;
		}
		return ipStrings;
	}

	private void setPossibleAccesses(String publickey) {
		this.ipStrings.add(publickey);
	}

	private String executeCommand(String[] command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	private String executeCommand(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	private String setNewUserLinux(String newUsername) {
		try {
			if (password == null) {
				password = config.getProperty("password");
			}
		} catch (IllegalArgumentException e) {
			Log.fatal("SSH", "Could not find Sudo-Passwort");
			Log.fatal("SSH",
					"Please add password in ~/.fiteagle/PhysicalNodeAdapter-1.properties");
		}

		StringBuffer output = new StringBuffer();
		Process p;
		try {
			String setPWString = "echo '" + password + "' | sudo -kS passwd "
					+ newUsername;
			String addUserString = "echo '" + password
					+ "' | sudo -kS adduser --disabled-password --gecos \"\" "
					+ newUsername;
			String[] addUserCMD = { "/bin/sh", "-c", addUserString };
			String[] setPWCMD = { "/bin/sh", "-c", setPWString, newUsername,
					newUsername };

			p = Runtime.getRuntime().exec(addUserCMD);
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	private String setNewUserMac(String newUsername) {
		try {
			if (password == null) {
				password = config.getProperty("password");
			}
		} catch (IllegalArgumentException e) {
			Log.fatal("SSH", "Could not find Sudo-Passwort");
			Log.fatal("SSH",
					"Please add password in ~/.fiteagle/PhysicalNodeAdapter-1.properties");
		}

		String mypassword = password;
		String fullname = "AlexNeu";
		String userpwd = "password";
		String username = newUsername;
		String output = "";

		Log.info("SSH", "Create new user entry");
		String cmd3String = "echo '" + mypassword
				+ "' | sudo -kS dscl . create /Users/" + username;
		String[] cmd3 = { "/bin/sh", "-c", cmd3String };
		Log.info("SSH-USERENTRY", executeCommand(cmd3));

		Log.info("SSH", "Set shell property to bash");
		String cmd4String = "echo '" + mypassword
				+ "' | sudo -kS dscl . create /Users/" + username
				+ " UserShell /bin/bash";
		String[] cmd4 = { "/bin/sh", "-c", cmd4String };
		Log.info("SSH-SHELL", executeCommand(cmd4));

		Log.info("SSH", "Set users full name");
		String cmd5String = "echo '" + mypassword
				+ "' | sudo -kS dscl . create /Users/" + username
				+ " RealName \"" + fullname + "\"";
		String[] cmd5 = { "/bin/sh", "-c", cmd5String };
		Log.info("SSH-FULLNAME", executeCommand(cmd5));

		Log.info("SSH", "Set users UniqueID");
		String cmd6String = "echo '"
				+ mypassword
				+ "' | sudo -kS dscl . create /Users/"
				+ username
				+ " UniqueID \"$(($(dscl . -list /Users UniqueID | awk '{print $2}' | sort -ug | tail -1)+1))\"";
		String[] cmd6 = { "/bin/sh", "-c", cmd6String };
		Log.info("SSH-UNIQUEID", executeCommand(cmd6));

		Log.info("SSH", "Set users primary group");
		String cmd7String = "echo '" + mypassword
				+ "' | sudo -kS dscl . create /Users/" + username
				+ " PrimaryGroupID 1000";
		String[] cmd7 = { "/bin/sh", "-c", cmd7String };
		Log.info("SSH-GROUP", executeCommand(cmd7));

		Log.info("SSH", "Set users home directory");
		String cmd8String = "echo '" + mypassword
				+ "' | sudo -kS dscl . create /Users/" + username
				+ " NFSHomeDirectory /Users/" + username;
		String[] cmd8 = { "/bin/sh", "-c", cmd8String };
		Log.info("SSH-HOMEDIRECTORY", executeCommand(cmd8));

		Log.info("SSH", "Set users password");
		String cmd9String = "echo '" + mypassword
				+ "' | sudo -kS dscl . passwd /Users/" + username + " "
				+ userpwd;
		String[] cmd9 = { "/bin/sh", "-c", cmd9String };
		Log.info("SSH-USERPW", executeCommand(cmd9));

		return output;
	}

	public void addSshAccess(String newUser, String publicKey) {
		try {
			if (password == null) {
				password = config.getProperty("password");
			}
		} catch (IllegalArgumentException e) {
			Log.fatal("SSH", "Could not find Sudo-Passwort");
			Log.fatal("SSH",
					"Please add password in ~/.fiteagle/PhysicalNodeAdapter-1.properties");
		}

		this.setUsername(newUser.toLowerCase());
		this.setPossibleAccesses(publicKey);

		String addSshString = "echo '" + password
				+ "' | sudo -kS mkdir -pm 0777 ~/../" + newUser.toLowerCase()
				+ "/.ssh";
		String[] addSshCMD = { "/bin/sh", "-c", addSshString };

		String addKeysString = "echo " + publicKey + " >> ~/../"
				+ newUser.toLowerCase() + "/.ssh/authorized_keys";
		String[] addSshKeyCMD = { "/bin/sh", "-c", addKeysString };

		String chMod600 = "echo '" + password + "' | sudo -kS chmod 600 ~/../"
				+ newUser.toLowerCase() + "/.ssh/authorized_keys";
		String[] chMod600CMD = { "/bin/sh", "-c", chMod600 };

		String chMod700 = "echo '" + password + "' | sudo -kS chmod 700 ~/../"
				+ newUser.toLowerCase() + "/.ssh";
		String[] chMod700CMD = { "/bin/sh", "-c", chMod700 };

		String chOwnString = "echo '" + password + "' | sudo -kS chown -R "
				+ newUser.toLowerCase() + " ~/../" + newUser.toLowerCase()
				+ "/.ssh";
		String[] chOwnStringCMD = { "/bin/sh", "-c", chOwnString };

		String chOwnStringMac = "echo '" + password + "' | sudo -kS chown -Rv "
				+ newUser.toLowerCase() + " ~/../" + newUser.toLowerCase()
				+ "/.ssh";
		String[] chOwnStringMacCMD = { "/bin/sh", "-c", chOwnStringMac };

		if (executeCommand("uname -s").contains("Linux")) {
			Log.info("SSH", "Creating new User for SSH");
			setNewUserLinux(newUser.toLowerCase());

			Log.info("SSH", "Creating .ssh folder");
			executeCommand(addSshCMD);

			Log.info("SSH", "Adding Public Key to 'authorized_keys'");
			executeCommand(addSshKeyCMD);

			Log.info("SSH", "Changing file and directory rights");
			executeCommand(chMod600CMD);
			executeCommand(chMod700CMD);
			executeCommand(chOwnStringCMD);

		} else if (executeCommand("uname -s").contains("Darwin")) {
			Log.info("SSH", "Creating new User for SSH");
			setNewUserMac(newUser.toLowerCase());

			Log.info("SSH", "Creating .ssh folder");
			executeCommand(addSshCMD);

			Log.info("SSH", "Adding Public Key to 'authorized_keys'");
			executeCommand(addSshKeyCMD);

			Log.info("SSH", "Changing file and directory rights");
			executeCommand(chMod600CMD);
			executeCommand(chMod700CMD);
			executeCommand(chOwnStringMacCMD);
		} else {
			Log.fatal("SSH", "Your OS is not supported yet");
		}

	}

	private void deleteUserAccount(String username) {

		if (executeCommand("uname -s").contains("Linux")) {
			String deleteUserLinux = "echo '" + password
					+ "' | sudo -kS deluser --remove-home "
					+ username.toLowerCase();
			String[] deleteUserLinuxCMD = { "/bin/sh", "-c", deleteUserLinux };

			Log.info("SSH Delete User", executeCommand(deleteUserLinuxCMD));
		} else if (executeCommand("uname -s").contains("Darwin")) {
			Log.info("SSH Delete User",
					executeCommand("/usr/bin/dscl . -search /Users name "
							+ username.toLowerCase()));

			String deleteUserMac = "echo '" + password
					+ "' | sudo -kS /usr/bin/dscl . -delete \"/Users/"
					+ username.toLowerCase() + "\"";
			String[] deleteUserMacCMD = { "/bin/sh", "-c", deleteUserMac };
			Log.info("SSH Delete User", executeCommand(deleteUserMacCMD));

			String deleteUserMacHomedirectory = "echo '" + password
					+ "' | sudo -kS /bin/rm -rf \"/Users/"
					+ username.toLowerCase() + "\"";
			String[] deleteUserMacHomedirectoryCMD = { "/bin/sh", "-c",
					deleteUserMacHomedirectory };
			Log.info("SSH Delete Homedirectory",
					executeCommand(deleteUserMacHomedirectoryCMD));
		} else {
			Log.fatal("SSH", "Can't delete User on this OS");
		}
	}

	public void deleteSshAccess() {
		try {
			if (password == null) {
				password = config.getProperty("password");
			}
		} catch (IllegalArgumentException e) {
			Log.fatal("SSH", "Could not find Sudo-Passwort");
			Log.fatal("SSH",
					"Please add password in ~/.fiteagle/PhysicalNodeAdapter-1.properties");
		}

		for (String username : this.getUsernames()) {
			deleteUserAccount(username.toLowerCase());
		}
	}

}
