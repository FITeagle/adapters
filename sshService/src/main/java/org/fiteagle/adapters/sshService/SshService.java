package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.batch.api.chunk.CheckpointAlgorithm;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.jcraft.jsch.*;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import info.openmultinet.ontology.vocabulary.Omn_service;

public class SshService implements Runnable{
	protected SshServiceAdapter owningAdapter;
	private String instanceName;
	private List<String> publicKeys = new ArrayList<>();
	private List<String> username;

	private Config config;
	private String password;
	private static Boolean sudoPW;
	

	public static Map<String, AbstractAdapter> adapterInstances;

	public SshService(SshServiceAdapter owningAdapter, String instanceName) {
	  
		this.username = new ArrayList<>();
		this.owningAdapter = owningAdapter;
		this.instanceName = instanceName;
		
	}
	
	@Override
	public void run(){
	  addSshAccess();
	}

  public void updateProperty(Statement configureStatement) {
    
    if(configureStatement.getSubject().getURI().equals(instanceName)){
      
      if(Omn_service.publickey.getLocalName().equals(configureStatement.getPredicate().getLocalName())){
        this.setPossibleAccesses(configureStatement.getString());
      }
      
      if(Omn_service.username.getLocalName().equals(configureStatement.getPredicate().getLocalName())){
        this.setUsername(configureStatement.getString().toLowerCase());
      }
      
    }
  }
  
  
  
	public String getInstanceName() {
		return instanceName;
	}
	
	public void refreshConfig(){
		config = new Config("SshService");
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
	
	public SshServiceAdapter getSshServiceAdapter(){
	  return this.owningAdapter;
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
		checkSudoPW();

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

		checkSudoPW();


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
	

	private void createRemoteUser()  {
	  
    SSHConnector connector = new SSHConnector(this.getUsernames(), this.getPossibleAccesses(), owningAdapter.getSshParameters());
    connector.createUserAccount();

    
	}
	
	public void addSshAccess()  {
	  
	  if(ISshService.LOCAL_HOST.equals(owningAdapter.getSshParameters().getIP()) || ISshService.LOCALHOST_IP.equals(owningAdapter.getSshParameters().getIP())){
	    createLocalUser();	    
	  } else {
      createRemoteUser();
	  }
	  
	}
	
	public void createSSHtest(SSHConnector connector){
	  connector.createUserAccount();
	}
   
	private void createLocalUser(){

		String[] addSshCMD ;
		String[] chMod600CMD;
		String[] chMod700CMD;
		String[] chOwnStringCMD;
		String[] chOwnStringMacCMD;
		
		sudoPW = true;
    checkSudoPW();
    
    for(String newUser : this.getUsernames()) {
      
		if(sudoPW){
			String addSshString = "echo '" + password
					+ "' | sudo -kS mkdir -pm 0777 ~/../" + newUser
					+ "/.ssh";
			String [] addSshCMDTmp = { "/bin/sh", "-c", addSshString };
			addSshCMD = addSshCMDTmp;
			
			
			String chMod600 = "echo '" + password + "' | sudo -kS chmod 600 ~/../"
					+ newUser + "/.ssh/authorized_keys";
			String [] chMod600CMDtmp = { "/bin/sh", "-c", chMod600 };
			chMod600CMD=chMod600CMDtmp;
			
			String chMod700 = "echo '" + password + "' | sudo -kS chmod 700 ~/../"
					+ newUser + "/.ssh";
			String [] chMod700CMDtmp = { "/bin/sh", "-c", chMod700 };
			chMod700CMD=chMod700CMDtmp;
			
			String chOwnString = "echo '" + password + "' | sudo -kS chown -R "
					+ newUser + " ~/../" + newUser
					+ "/.ssh";
			String [] chOwnStringCMDtmp = { "/bin/sh", "-c", chOwnString };
			chOwnStringCMD=chOwnStringCMDtmp;
			
			String chOwnStringMac = "echo '" + password + "' | sudo -kS chown -Rv "
					+ newUser + " ~/../" + newUser
					+ "/.ssh";
			String [] chOwnStringMacCMDtmp = { "/bin/sh", "-c", chOwnStringMac };
			chOwnStringMacCMD=chOwnStringMacCMDtmp;
			
		}else{
			String addSshString = "sudo -n mkdir -pm 0777 ~/../" + newUser
					+ "/.ssh";
			String [] addSshCMDTmp = { "/bin/sh", "-c", addSshString };
			addSshCMD = addSshCMDTmp;

			
			String chMod600 = "sudo -n chmod 600 ~/../"
					+ newUser + "/.ssh/authorized_keys";
			String [] chMod600CMDtmp = { "/bin/sh", "-c", chMod600 };
			chMod600CMD=chMod600CMDtmp;
			
			String chMod700 = "sudo -n chmod 700 ~/../"
					+ newUser + "/.ssh";
			String [] chMod700CMDtmp = { "/bin/sh", "-c", chMod700 };
			chMod700CMD=chMod700CMDtmp;
			
			String chOwnString = "sudo -n chown -R "
					+ newUser + " ~/../" + newUser
					+ "/.ssh";
			String [] chOwnStringCMDtmp = { "/bin/sh", "-c", chOwnString };
			chOwnStringCMD=chOwnStringCMDtmp;
			
			String chOwnStringMac = "sudo -n chown -Rv "
					+ newUser + " ~/../" + newUser
					+ "/.ssh";
			String [] chOwnStringMacCMDtmp = { "/bin/sh", "-c", chOwnStringMac };
			chOwnStringMacCMD=chOwnStringMacCMDtmp;
			
			
			
			
		}
		


		if (executeCommand("uname -s").contains("Linux")) {
			
			
			Log.info("SSH", "Creating new User for SSH");
			setNewUserLinux(newUser);
				
			Log.info("SSH", "Creating .ssh folder");
			executeCommand(addSshCMD);

			addSshKeys(newUser);

			Log.info("SSH", "Changing file and directory rights");
			executeCommand(chMod600CMD);
			executeCommand(chMod700CMD);
			executeCommand(chOwnStringCMD);

		} else if (executeCommand("uname -s").contains("Darwin")) {
			Log.info("SSH", "Creating new User for SSH");
			setNewUserMac(newUser);

			Log.info("SSH", "Creating .ssh folder");
			executeCommand(addSshCMD);

			addSshKeys(newUser);

			Log.info("SSH", "Changing file and directory rights");
			executeCommand(chMod600CMD);
			executeCommand(chMod700CMD);
			executeCommand(chOwnStringMacCMD);
		} else {
			Log.fatal("SSH", "Your OS is not supported yet");
		}
    }
	}

	private void addSshKeys(String newUser){
	  
	  String[] addSshKeyCMD;
	  Log.info("SSH", "Adding Public Keys to 'authorized_keys'");
	  
	  for(String publicKey : this.getPossibleAccesses()){
	    String addKeysString = "echo " + publicKey + " >> ~/../"
          + newUser + "/.ssh/authorized_keys";
      String [] addSshKeyCMDtmp = { "/bin/sh", "-c", addKeysString };
      addSshKeyCMD=addSshKeyCMDtmp;
      
      executeCommand(addSshKeyCMD);
	  }
	}
	
	private void deleteLocalUser(String username) {
		checkSudoPW();	
		
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
	    if(ISshService.LOCAL_HOST.equals(owningAdapter.getSshParameters().getIP()) || ISshService.LOCALHOST_IP.equals(owningAdapter.getSshParameters().getIP())){
	      deleteLocalUser(username.toLowerCase());
	    }
	    else {
	      deleteRemoteUser(username.toLowerCase());
	    }
	  }
	}
	
	  private void deleteRemoteUser(String username){
	    
	    SSHConnector connector = new SSHConnector(this.getUsernames(), this.getPossibleAccesses(), owningAdapter.getSshParameters());
	    connector.deleteUserAccount();

	  }

	  
	private void checkSudoPW(){
	  
	    if (executeCommand("sudo -n echo 'ok'").contains("sudo")){
	      setPassword();
	    } else { 
	      sudoPW = false;
	    }
	  
	}

	private void setPassword(){
	  try {
	    
	    sudoPW = true;
      if (password == null) {
      String passwordProperty = owningAdapter.getSshParameters().getPassword();
      if(passwordProperty.isEmpty() || passwordProperty == null){
        Log.fatal("SSH", "Could not find Sudo-Passwort");
        Log.fatal("SSH",
            "Please add password in ~/.fiteagle/SshService.properties");
      }
      else this.password = passwordProperty;
    }
	    
	  } catch (IllegalArgumentException e) {
      Log.fatal("SSH", "Could not find Sudo-Passwort");
      Log.fatal("SSH",
          "Please add password in ~/.fiteagle/SshServiceAdapter.properties");
    }
	}
	
	
	public SshService(){
	  
	}
	
}
