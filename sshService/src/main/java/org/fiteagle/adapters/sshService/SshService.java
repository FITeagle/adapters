package org.fiteagle.adapters.sshService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import net.schmizz.sshj.SSHClient;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;

public class SshService {
	protected SshServiceAdapter owningAdapter;
	private String instanceName;
	private List<String> ipStrings = new ArrayList<>();

//	private SSHClient client;
	private Config config;
	private String password;

	public static Map<String, AbstractAdapter> adapterInstances;

	public SshService() {
		config = new Config("PhysicalNodeAdapter-1");
	}

	public String getInstanceName() {
		// TODO Auto-generated method stub
		return instanceName;
	}

	public List<String> getPossibleAccesses() {
		if (ipStrings.isEmpty()) {
			return null;
		}
		return ipStrings;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output.toString();
	}

	private String setNewUser(String newUsername) {
		if (password == null) {
			password = config.getProperty("password");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output.toString();
	}
	
	private String setNewUserMac(String newUsername) {

		if (password == null) {
			password = config.getProperty("password");
		}
		String mypassword = password;
		String fullname = "AlexNeu";
		String userpwd = "password";
		String username = newUsername;
		String output = "";
		
		Log.info("SSH", "Create new user entry");
		String cmd3String = "echo '"+ mypassword +"' | sudo -kS dscl . create /Users/" + username;
		String[] cmd3 = { "/bin/sh", "-c", cmd3String };
    Log.info("SSH-USERENTRY", executeCommand(cmd3));
		
		Log.info("SSH", "Set shell property to bash");
		String cmd4String = "echo '"+ mypassword +"' | sudo -kS dscl . create /Users/"+ username +" UserShell /bin/bash";
		String[] cmd4 = { "/bin/sh", "-c", cmd4String };
    Log.info("SSH-SHELL", executeCommand(cmd4));
		
		Log.info("SSH", "Set users full name");
		String cmd5String = "echo '"+ mypassword +"' | sudo -kS dscl . create /Users/" + username + " RealName \"" + fullname + "\"";
		String[] cmd5 = { "/bin/sh", "-c", cmd5String };
    Log.info("SSH-FULLNAME", executeCommand(cmd5));
		
		Log.info("SSH", "Set users UniqueID");
		String cmd6String = "echo '"+ mypassword +"' | sudo -kS dscl . create /Users/" + username + " UniqueID \"$(($(dscl . -list /Users UniqueID | awk '{print $2}' | sort -ug | tail -1)+1))\"";
		String[] cmd6 = { "/bin/sh", "-c", cmd6String };
		Log.info("SSH-UNIQUEID", executeCommand(cmd6));
		
		Log.info("SSH", "Set users primary group");
		String cmd7String = "echo '"+ mypassword +"' | sudo -kS dscl . create /Users/" + username + " PrimaryGroupID 1000";
		String[] cmd7 = { "/bin/sh", "-c", cmd7String };
    Log.info("SSH-GROUP", executeCommand(cmd7));
		
		Log.info("SSH", "Set users home directory");
		String cmd8String = "echo '"+ mypassword +"' | sudo -kS dscl . create /Users/" + username + " NFSHomeDirectory /Users/"+username;
		String[] cmd8 = { "/bin/sh", "-c", cmd8String };
    Log.info("SSH-HOMEDIRECTORY", executeCommand(cmd8));
		
		Log.info("SSH", "Set users password");
		String cmd9String = "echo '"+ mypassword +"' | sudo -kS dscl . passwd /Users/" + username + " " + userpwd;
		String[] cmd9 = { "/bin/sh", "-c", cmd9String };
    Log.info("SSH-USERPW", executeCommand(cmd9));
		
		return output;
	}

	public void addSshAccess(String newUser, String publicKey) {
		if (password == null) {
			password = config.getProperty("password");
		}

		String addSshString = "echo '" + password
				+ "' | sudo -kS mkdir -pm 0777 ~/../" + newUser + "/.ssh";
		String[] addSshCMD = { "/bin/sh", "-c", addSshString };

		String addKeysString = "echo " + publicKey + " >> ~/../" + newUser
				+ "/.ssh/authorized_keys";
		String[] addSshKeyCMD = { "/bin/sh", "-c", addKeysString };

		String chMod600 = "echo '" + password + "' | sudo -kS chmod 600 ~/../"
				+ newUser + "/.ssh/authorized_keys";
		String[] chMod600CMD = { "/bin/sh", "-c", chMod600 };

		String chMod700 = "echo '" + password + "' | sudo -kS chmod 700 ~/../"
				+ newUser + "/.ssh";
		String[] chMod700CMD = { "/bin/sh", "-c", chMod700 };

		String chOwnString = "echo '" + password + "' | sudo -kS chown -R "
				+ newUser + " ~/../" + newUser + "/.ssh";
		String[] chOwnStringCMD = { "/bin/sh", "-c", chOwnString };

		String chOwnStringMac = "echo '" + password + "' | sudo -kS chown -Rv "
				+ newUser + " ~/../" + newUser + "/.ssh";
		String[] chOwnStringMacCMD = { "/bin/sh", "-c", chOwnStringMac };
		
		if(executeCommand("uname -s").contains("Linux")){
			Log.info("SSH", "Creating new User for SSH");
			setNewUser(newUser);

			Log.info("SSH", "Creating .ssh folder");
			executeCommand(addSshCMD);

			Log.info("SSH", "Adding Public Key to 'authorized_keys'");
			executeCommand(addSshKeyCMD);

			Log.info("SSH", "Changing file and directory rights");
			executeCommand(chMod600CMD);
			executeCommand(chMod700CMD);
			executeCommand(chOwnStringCMD);
			
			
		}else if (executeCommand("uname -s").contains("Darwin")) {
			// Log.fatal("MAC", "Mac is not supported by now");
			Log.info("SSH", "Creating new User for SSH");
			setNewUserMac(newUser);

			Log.info("SSH", "Creating .ssh folder");
			executeCommand(addSshCMD);

			Log.info("SSH", "Adding Public Key to 'authorized_keys'");
			executeCommand(addSshKeyCMD);

			Log.info("SSH", "Changing file and directory rights");
			executeCommand(chMod600CMD);
			executeCommand(chMod700CMD);
			executeCommand(chOwnStringMacCMD);
		}else{
			Log.fatal("SSH", "Your OS is not supported yet");
		}


		// String rootstring = "echo '" + password + "' | sudo -kSs";
		// String [] rootCMD ={"/bin/sh","-c",rootstring};

		// try {
		// p =
		// Runtime.getRuntime().exec("dpkg -s openssh-server | grep -c installed");
		// p.waitFor();
		// BufferedReader reader =
		// new BufferedReader(new InputStreamReader(p.getInputStream()));
		//
		// String line = "";
		// while ((line = reader.readLine())!= null) {
		// output.append(line + "\n");
		// }
		//
		// Log.fatal("OPENSSH-Server",output.toString());
		//
		// if(output.toString().equals("1")){
		//
		// p = Runtime.getRuntime().exec("echo '" + password +
		// "' | sudo -kS adduser "+newUser);
		// p.waitFor();
		// BufferedReader reader2 =
		// new BufferedReader(new InputStreamReader(p.getInputStream()));
		//
		// String line2 = "";
		// while ((line = reader.readLine())!= null) {
		// output2.append(line + "\n");
		// }
		// System.out.print(output.toString());
		//
		//
		// p = Runtime.getRuntime().exec("echo '" + password +
		// "' | sudo -kS mkdir -p /home/"+newUser+"/.ssh | sudo bash -c 'echo "
		// + publicKey + " >> /home/"
		// + newUser + "/.ssh/authorized_keys'");
		// p.waitFor();
		// BufferedReader reader3 =
		// new BufferedReader(new InputStreamReader(p.getInputStream()));
		//
		// String line3 = "";
		// while ((line = reader.readLine())!= null) {
		// output2.append(line + "\n");
		// }
		// System.out.print(output.toString());
		// }else{
		// Log.fatal("SSH",
		// "No OpenSSH-Server found. Please install it first, with:");
		// Log.fatal("SSH", "$sudo apt-get install openssh-server");
		// }

		// Command com2 =
		// session.exec("dpkg -s openssh-server | grep -c installed");
		//
		// if(IOUtils.readFully(com2.getInputStream())
		// .toString().equals("1")){
		//
		// Command com3 = session.exec("echo '" + password +
		// "' | sudo -kS adduser "+newUser);
		// System.out.println(IOUtils.readFully(com3.getInputStream())
		// .toString());
		// System.out.println("\n **exit status: " + com3.getExitStatus());
		//
		// Command com4 = session.exec("echo '" + password +
		// "' | sudo -kS mkdir -p /home/"+newUser+"/.ssh |sudo bash -c 'echo " +
		// publicKey + " >> /home/"
		// + newUser + "/.ssh/authorized_keys'");
		// System.out.println(IOUtils.readFully(com4.getInputStream())
		// .toString());
		// com4.join(5, TimeUnit.SECONDS);
		// System.out.println("\n **exit status: " + com4.getExitStatus());
		//
		// }else{
		// Log.fatal("SSH",
		// "No OpenSSH-Server found. Please install it first, with:");
		// Log.fatal("SSH", "$sudo apt-get install openssh-server");
		// }

	}

	public void deleteSshAccess(String ip, PublicKey publicKey) {

	}

	public void configureSshAccess() {

	}

}
