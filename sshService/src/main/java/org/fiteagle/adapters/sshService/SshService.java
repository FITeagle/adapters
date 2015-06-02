package org.fiteagle.adapters.sshService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.batch.api.chunk.CheckpointAlgorithm;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SshService {
	protected SshServiceAdapter owningAdapter;
	private String instanceName;
	private List<String> publicKeys = new ArrayList<>();
	private List<String> username;

	private Config config;
	private String password;
	private static Boolean sudoPW;
	
	private int componentID_index;
	private String ip;

	public static Map<String, AbstractAdapter> adapterInstances;

	public SshService(SshServiceAdapter owningAdapter) {
		config = new Config("SshServiceAdapter");
		this.username = new ArrayList<>();
		this.owningAdapter = owningAdapter;
	}

	public String getInstanceName() {
		return instanceName;
	}
	
	public void refreshConfig(){
		config = new Config("SshServiceAdapter");
	}

	public List<String> getUsernames() {
		return this.username;
	}

	private void setUsername(String username) {
		this.username.add(username);
	}

	public List<String> getPossibleAccesses() {
		if (publicKeys.isEmpty()) {
			return null;
		}
		return publicKeys;
	}

	private void setPossibleAccesses(String publickey) {
		this.publicKeys.add(publickey);
	}

	private String executeCommand(String[] command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			
			BufferedReader stdError = new BufferedReader(new
	                 InputStreamReader(p.getErrorStream()));

			String line = "";
			while ((line =reader.readLine()) != null) {
				output.append(line + "\n");
			}
			
            while ((line = stdError.readLine()) != null) {
                output.append(line);
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
			
			BufferedReader stdError = new BufferedReader(new
	                 InputStreamReader(p.getErrorStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			while ((line = stdError.readLine()) != null) {
                output.append(line);
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	private String setNewUserLinux(String newUsername) {
		checkSudoPW(null);

		StringBuffer output = new StringBuffer();
		Process p;
		
		try {
			if(sudoPW){
			String addUserString = "echo '" + password
					+ "' | sudo -kS adduser --disabled-password --gecos \"\" "
					+ newUsername;
			String[] addUserCMD = { "/bin/sh", "-c", addUserString };
			p = Runtime.getRuntime().exec(addUserCMD);
			p.waitFor();
			}else{
				String addUserString = "sudo -n adduser --disabled-password --gecos \"\" "
						+ newUsername;
				String[] addUserCMD = { "/bin/sh", "-c", addUserString };
				p = Runtime.getRuntime().exec(addUserCMD);
				p.waitFor();	
			}

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

	private void setNewUserMac(String newUsername) {

		checkSudoPW(null);


		String mypassword = password;
		String fullname = "AlexNeu";
		String userpwd = "password";
		String username = newUsername;
		String output = "";

		if(sudoPW){
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
		}else{
			Log.info("SSH", "Create new user entry");
			String cmd3String = "sudo -n dscl . create /Users/" + username;
			String[] cmd3 = { "/bin/sh", "-c", cmd3String };
			Log.info("SSH-USERENTRY", executeCommand(cmd3));

			Log.info("SSH", "Set shell property to bash");
			String cmd4String = "sudo -n dscl . create /Users/" + username
					+ " UserShell /bin/bash";
			String[] cmd4 = { "/bin/sh", "-c", cmd4String };
			Log.info("SSH-SHELL", executeCommand(cmd4));

			Log.info("SSH", "Set users full name");
			String cmd5String = "sudo -n dscl . create /Users/" + username
					+ " RealName \"" + fullname + "\"";
			String[] cmd5 = { "/bin/sh", "-c", cmd5String };
			Log.info("SSH-FULLNAME", executeCommand(cmd5));

			Log.info("SSH", "Set users UniqueID");
			String cmd6String = "sudo -n dscl . create /Users/"
					+ username
					+ " UniqueID \"$(($(dscl . -list /Users UniqueID | awk '{print $2}' | sort -ug | tail -1)+1))\"";
			String[] cmd6 = { "/bin/sh", "-c", cmd6String };
			Log.info("SSH-UNIQUEID", executeCommand(cmd6));

			Log.info("SSH", "Set users primary group");
			String cmd7String = "sudo -n dscl . create /Users/" + username
					+ " PrimaryGroupID 1000";
			String[] cmd7 = { "/bin/sh", "-c", cmd7String };
			Log.info("SSH-GROUP", executeCommand(cmd7));

			Log.info("SSH", "Set users home directory");
			String cmd8String = "sudo -n dscl . create /Users/" + username
					+ " NFSHomeDirectory /Users/" + username;
			String[] cmd8 = { "/bin/sh", "-c", cmd8String };
			Log.info("SSH-HOMEDIRECTORY", executeCommand(cmd8));

			Log.info("SSH", "Set users password");
			String cmd9String = "sudo -n dscl . passwd /Users/" + username + " "
					+ userpwd;
			String[] cmd9 = { "/bin/sh", "-c", cmd9String };
			Log.info("SSH-USERPW", executeCommand(cmd9));
		}
	}
	
	private void setComponentIDIndex(String adapterInstance){
	  
	  String componentIDs = config.getProperty("componentID");
    if(componentIDs.contains(",")){
      String[] componentID_array = componentIDs.split("\\,");
      for(int counter = 0; counter < componentID_array.length; counter++){
       if(adapterInstance.equals(componentID_array[counter])){
         this.componentID_index = counter;
         break;
         }
       }
      } else this.componentID_index = 0;
    
    System.out.println("INDEX IS " + componentID_index);
	}
	
	private void setIP(){
	  
	  String IPs = config.getProperty("ip");
	  if(IPs.contains(",")){
	    String[] IP_array = IPs.split("\\,");
	    this.ip = IP_array[this.componentID_index];
	  }
	  else {
	    this.ip = IPs;
	  }
	  System.out.println("IP IS " + ip);
	}

	private void createRemoteUser(String newUser, String publicKey){
	  
	  this.setUsername(newUser.toLowerCase());
    this.setPossibleAccesses(publicKey);
	  
    SSHConnector connector = new SSHConnector(ip);
    connector.connect();
    SSHClient client = connector.getSSHClient();
    
    try {
      Session session = client.startSession();
      try{
        
        checkSudoPW(session);

      }
      finally {
        session.close();
      }
    } catch (ConnectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TransportException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    connector.createUserAccount(newUser, password);
    connector.createUserSSHDirectory(newUser, password);
    connector.createAuthorizedKeysFile(newUser, password);
    connector.changeOwnerOfUserHome(newUser, password);
    connector.addSSHKey(publicKey, newUser, password);
    connector.disconnect();
    
    
	}
	
	public void addSshAccess(String newUser, String publicKey, String adapterInstance) {
	  
	  setComponentIDIndex(adapterInstance);
	  setIP();
	  
	  if("localhost".equals(this.ip) || "127.0.0.1".equals(this.ip)){
	    createLocalUser(newUser, publicKey, null);	    
	  } else {
      createRemoteUser(newUser, publicKey);
	  }
	  
	}
   
	private void createLocalUser(String newUser, String publicKey, Session session){
		
		this.setUsername(newUser.toLowerCase());
		this.setPossibleAccesses(publicKey);

		String[] addSshCMD ;
		String[] addSshKeyCMD;
		String[] chMod600CMD;
		String[] chMod700CMD;
		String[] chOwnStringCMD;
		String[] chOwnStringMacCMD;
		
		sudoPW = true;
    checkSudoPW(session);
    
		if(sudoPW){
			String addSshString = "echo '" + password
					+ "' | sudo -kS mkdir -pm 0777 ~/../" + newUser.toLowerCase()
					+ "/.ssh";
			String [] addSshCMDTmp = { "/bin/sh", "-c", addSshString };
			addSshCMD = addSshCMDTmp;

			String addKeysString = "echo " + publicKey + " >> ~/../"
					+ newUser.toLowerCase() + "/.ssh/authorized_keys";
			String [] addSshKeyCMDtmp = { "/bin/sh", "-c", addKeysString };
			addSshKeyCMD=addSshKeyCMDtmp;
			
			String chMod600 = "echo '" + password + "' | sudo -kS chmod 600 ~/../"
					+ newUser.toLowerCase() + "/.ssh/authorized_keys";
			String [] chMod600CMDtmp = { "/bin/sh", "-c", chMod600 };
			chMod600CMD=chMod600CMDtmp;
			
			String chMod700 = "echo '" + password + "' | sudo -kS chmod 700 ~/../"
					+ newUser.toLowerCase() + "/.ssh";
			String [] chMod700CMDtmp = { "/bin/sh", "-c", chMod700 };
			chMod700CMD=chMod700CMDtmp;
			
			String chOwnString = "echo '" + password + "' | sudo -kS chown -R "
					+ newUser.toLowerCase() + " ~/../" + newUser.toLowerCase()
					+ "/.ssh";
			String [] chOwnStringCMDtmp = { "/bin/sh", "-c", chOwnString };
			chOwnStringCMD=chOwnStringCMDtmp;
			
			String chOwnStringMac = "echo '" + password + "' | sudo -kS chown -Rv "
					+ newUser.toLowerCase() + " ~/../" + newUser.toLowerCase()
					+ "/.ssh";
			String [] chOwnStringMacCMDtmp = { "/bin/sh", "-c", chOwnStringMac };
			chOwnStringMacCMD=chOwnStringMacCMDtmp;
			
		}else{
			String addSshString = "sudo -n mkdir -pm 0777 ~/../" + newUser.toLowerCase()
					+ "/.ssh";
			String [] addSshCMDTmp = { "/bin/sh", "-c", addSshString };
			addSshCMD = addSshCMDTmp;

			String addKeysString = "echo " + publicKey + " >> ~/../"
					+ newUser.toLowerCase() + "/.ssh/authorized_keys";
			String [] addSshKeyCMDtmp = { "/bin/sh", "-c", addKeysString };
			addSshKeyCMD=addSshKeyCMDtmp;
			
			String chMod600 = "sudo -n chmod 600 ~/../"
					+ newUser.toLowerCase() + "/.ssh/authorized_keys";
			String [] chMod600CMDtmp = { "/bin/sh", "-c", chMod600 };
			chMod600CMD=chMod600CMDtmp;
			
			String chMod700 = "sudo -n chmod 700 ~/../"
					+ newUser.toLowerCase() + "/.ssh";
			String [] chMod700CMDtmp = { "/bin/sh", "-c", chMod700 };
			chMod700CMD=chMod700CMDtmp;
			
			String chOwnString = "sudo -n chown -R "
					+ newUser.toLowerCase() + " ~/../" + newUser.toLowerCase()
					+ "/.ssh";
			String [] chOwnStringCMDtmp = { "/bin/sh", "-c", chOwnString };
			chOwnStringCMD=chOwnStringCMDtmp;
			
			String chOwnStringMac = "sudo -n chown -Rv "
					+ newUser.toLowerCase() + " ~/../" + newUser.toLowerCase()
					+ "/.ssh";
			String [] chOwnStringMacCMDtmp = { "/bin/sh", "-c", chOwnStringMac };
			chOwnStringMacCMD=chOwnStringMacCMDtmp;
			
			
			
			
		}
		


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

	private void deleteLocalUser(String username) {
		checkSudoPW(null);	
		
		if (executeCommand("uname -s").contains("Linux")) {
			String[] deleteUserLinuxCMD ;
			if(sudoPW){
				String deleteUserLinux = "echo '" + password
						+ "' | sudo -kS deluser --remove-home "
						+ username.toLowerCase();
				String[] deleteUserLinuxCMDtmp = { "/bin/sh", "-c", deleteUserLinux };
				deleteUserLinuxCMD=deleteUserLinuxCMDtmp;
			}else{
				String deleteUserLinux = "sudo -n deluser --remove-home "
						+ username.toLowerCase();
				String[] deleteUserLinuxCMDtmp = { "/bin/sh", "-c", deleteUserLinux };
				deleteUserLinuxCMD=deleteUserLinuxCMDtmp;
			}

			Log.info("SSH Delete User", executeCommand(deleteUserLinuxCMD));
		} else if (executeCommand("uname -s").contains("Darwin")) {			
			Log.info("SSH Delete User",
					executeCommand("/usr/bin/dscl . -search /Users name "
							+ username.toLowerCase()));

			if(sudoPW){
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
			}else{
				String deleteUserMac = "sudo -n /usr/bin/dscl . -delete \"/Users/"
						+ username.toLowerCase() + "\"";
				String[] deleteUserMacCMD = { "/bin/sh", "-c", deleteUserMac };
				Log.info("SSH Delete User", executeCommand(deleteUserMacCMD));
				
				String deleteUserMacHomedirectory = "sudo -n /bin/rm -rf \"/Users/"
						+ username.toLowerCase() + "\"";
				String[] deleteUserMacHomedirectoryCMD = { "/bin/sh", "-c",
						deleteUserMacHomedirectory };
				Log.info("SSH Delete Homedirectory",
						executeCommand(deleteUserMacHomedirectoryCMD));
			}
		} else {
			Log.fatal("SSH", "Can't delete User on this OS");
		}
	}

	public void deleteSshAccess() {
	  
	  for (String username : this.getUsernames()) {
	    if("localhost".equals(this.ip) || "127.0.0.1".equals(this.ip)){
	      deleteLocalUser(username.toLowerCase());
	    }
	    else {
	      deleteRemoteUser(username.toLowerCase());
	    }
	  }
	}
	
	  private void deleteRemoteUser(String username){
	    SSHConnector connector = new SSHConnector(ip);
	    connector.connect();
	    connector.lockAccount(username, password);
	    connector.killAllUserProcesses(username, password);
	    connector.deleteUser(username, password);
	    connector.deleteUserDirectory(username, password);
	    connector.disconnect();
	  }

	  
	private void checkSudoPW(Session session){
	  
	  if(session == null){
	    if (executeCommand("sudo -n echo 'ok'").contains("sudo")){
	      setPassword();
	    } else { 
	      sudoPW = false;
	    }
	    
	  } 
	  else {
	    // TODO: remote check for password
	    try {
        Command command = session.exec("sudo -n echo 'ok'");
        command.join(5, TimeUnit.SECONDS);
        if(command.getOutputStream().toString().contains("sudo")){
          setPassword();
        } else {
          sudoPW = false;
        }
      } catch (ConnectionException | TransportException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
	  }
	  
	}

	private void setPassword(){
	  try {
	    
	    sudoPW = true;
      if (password == null) {
      String passwordProperty = config.getProperty("password");
      if(passwordProperty.isEmpty() || passwordProperty == null){
        Log.fatal("SSH", "Could not find Sudo-Passwort");
        Log.fatal("SSH",
            "Please add password in ~/.fiteagle/SshServiceAdapter.properties");
      }
      else if (passwordProperty.contains(",")){
        String[] password_array = passwordProperty.split("\\,");
        this.password = password_array[this.componentID_index];
      }
      else this.password = passwordProperty;
    }
	    
	  } catch (IllegalArgumentException e) {
      Log.fatal("SSH", "Could not find Sudo-Passwort");
      Log.fatal("SSH",
          "Please add password in ~/.fiteagle/SshServiceAdapter.properties");
    }
	}
	
}
