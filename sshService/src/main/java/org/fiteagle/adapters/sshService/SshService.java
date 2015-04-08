package org.fiteagle.adapters.sshService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import org.fiteagle.api.core.Config;

import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.SSHClient;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.hp.hpl.jena.rdf.model.AnonId;

public class SshService {
	  protected SshServiceAdapter owningAdapter;
	  private String instanceName;
	  private List<String> ipStrings = new ArrayList<>();
	  
	  private SSHClient client;
	  private Config config;
	  private String password;

	public static Map<String, AbstractAdapter> adapterInstances;
	 
	
	public SshService () {
		config = new Config("PhysicalNodeAdapter-1");
	              }

	public String getInstanceName() {
		// TODO Auto-generated method stub
		return instanceName;
	}
	
	public List<String> getPossibleAccesses(){
		if (ipStrings.isEmpty()){
			return null;
		}
		return ipStrings;
	}
	
private String executeCommand(String[] command){
	StringBuffer output = new StringBuffer();
	Process p;
	try {
		p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader reader = 
	            new BufferedReader(new InputStreamReader(p.getInputStream()));

	        String line = "";			
	        while ((line = reader.readLine())!= null) {
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

private String executeCommand(String command){
	StringBuffer output = new StringBuffer();
	Process p;
	try {
		p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader reader = 
	            new BufferedReader(new InputStreamReader(p.getInputStream()));

	        String line = "";			
	        while ((line = reader.readLine())!= null) {
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

private String setNewUser(String newUsername){
	if(password == null){
		password = config.getProperty("password");
	}
	
	StringBuffer output = new StringBuffer();
	Process p;
	try {
		String setPWString = "echo '" + password + "' | sudo -kS passwd "+newUsername;
		String addUserString ="echo '" + password + "' | sudo -kS adduser "+newUsername;
		String [] addUserCMD ={"/bin/sh","-c",addUserString,"123456","123456","","","","","","j"};
		String[] setPWCMD = {"/bin/sh","-c",setPWString,newUsername,newUsername};
		
		p = Runtime.getRuntime().exec(addUserCMD);
		p.waitFor();
		
		p = Runtime.getRuntime().exec(setPWCMD);

		BufferedReader reader = 
	            new BufferedReader(new InputStreamReader(p.getInputStream()));

	        String line = "";			
	        while ((line = reader.readLine())!= null) {
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


	 
	
	public void addSshAccess(String newUser,String publicKey){	 
		if(password == null){
			password = config.getProperty("password");
		}
		
		String [] serverCMD ={"/bin/sh","-c","dpkg -s openssh-server | grep -c installed"};
		
//		String addUserString ="echo '" + password + "' | sudo -kS adduser "+newUser;
//		String [] addUserCMD ={"/bin/sh","-c",addUserString,"123456","123456","","","","","","j"};
//		// TODO Check if User can login via SSH allthough user-account has now PW, if not, set via $passwd
		
		
		String addSshString = "echo '" + password + "' | sudo -kS mkdir -pm 0777 /home/"+newUser+"/.ssh";
		String [] addSshCMD ={"/bin/sh","-c",addSshString};
		
		String addKeysString = "echo "+publicKey+" >> /home/"+ newUser + "/.ssh/authorized_keys";
		String [] addSshKeyCMD ={"/bin/sh","-c",addKeysString};
		
		String chMod600 = "echo '" + password + "' | sudo -kS chmod 600 /home/"+newUser+"/.ssh/authorized_keys";
		String [] chMod600CMD ={"/bin/sh","-c",chMod600};
		
		String chMod700 = "echo '" + password + "' | sudo -kS chmod 700 /home/"+newUser+"/.ssh";
		String [] chMod700CMD ={"/bin/sh","-c",chMod700};
		
		String chOwnString = "echo '" + password + "' | sudo -kS chown -R "+ newUser +" /home/"+newUser+"/.ssh";
		String [] chOwnStringCMD ={"/bin/sh","-c",chOwnString};
		
//		String setPWString = "echo '" + password + "' | sudo -kS passwd "+newUser;
//		String [] setPWCMD ={"/bin/sh","-c",setPWString};
		
		Log.fatal("OPEN-SSH", executeCommand(serverCMD));
		if(executeCommand(serverCMD).contains("1")){
//			Log.fatal("ADD-USER", executeCommand(addUserCMD));
			Log.fatal("ADD-KEY", setNewUser(newUser));
			Log.fatal("ADD-KEY", executeCommand(addSshCMD));
			Log.fatal("ADD-SSH", executeCommand(addSshKeyCMD));
			Log.fatal("ADD-SSH", executeCommand(chMod600CMD));
			Log.fatal("ADD-SSH", executeCommand(chMod700CMD));
			Log.fatal("ADD-SSH", executeCommand(chOwnStringCMD));
//			Log.fatal("ADD-SSH", executeCommand(setPWCMD));
		}
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		String rootstring = "echo '" + password + "' | sudo -kSs";
//		String [] rootCMD ={"/bin/sh","-c",rootstring};
		
		
		
		
//			try {
//				p = Runtime.getRuntime().exec("dpkg -s openssh-server | grep -c installed");
//				p.waitFor();
//				BufferedReader reader = 
//                        new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//                    String line = "";			
//                    while ((line = reader.readLine())!= null) {
//                    	output.append(line + "\n");
//                    }
//                    
//                    Log.fatal("OPENSSH-Server",output.toString());
//
//    				if(output.toString().equals("1")){
//    					
//                      p = Runtime.getRuntime().exec("echo '" + password + "' | sudo -kS adduser "+newUser);
//        				p.waitFor();
//        				BufferedReader reader2 = 
//                                new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//                            String line2 = "";	
//                            while ((line = reader.readLine())!= null) {
//                            	output2.append(line + "\n");
//                            }                            
//                            System.out.print(output.toString());
//                            
//                            
//                            p = Runtime.getRuntime().exec("echo '" + password + "' | sudo -kS mkdir -p /home/"+newUser+"/.ssh | sudo bash -c 'echo " + publicKey + " >> /home/"
//        							+ newUser + "/.ssh/authorized_keys'");
//            				p.waitFor();
//            				BufferedReader reader3 = 
//                                    new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//                                String line3 = "";	
//                                while ((line = reader.readLine())!= null) {
//                                	output2.append(line + "\n");
//                                }                            
//                                System.out.print(output.toString());		
//    				}else{
//    					Log.fatal("SSH", "No OpenSSH-Server found. Please install it first, with:");
//    					Log.fatal("SSH", "$sudo apt-get install openssh-server");
//    				}

                        
                        
                        
                        
                        
                        
                        
                        
//				Command com2 = session.exec("dpkg -s openssh-server | grep -c installed");
//				
//				if(IOUtils.readFully(com2.getInputStream())
//						.toString().equals("1")){	
//					
//					Command com3 = session.exec("echo '" + password + "' | sudo -kS adduser "+newUser);
//					System.out.println(IOUtils.readFully(com3.getInputStream())
//							.toString());
//					System.out.println("\n **exit status: " + com3.getExitStatus());
//	
//					Command com4 = session.exec("echo '" + password + "' | sudo -kS mkdir -p /home/"+newUser+"/.ssh |sudo bash -c 'echo " + publicKey + " >> /home/"
//							+ newUser + "/.ssh/authorized_keys'");
//					System.out.println(IOUtils.readFully(com4.getInputStream())
//							.toString());
//					com4.join(5, TimeUnit.SECONDS);
//					System.out.println("\n **exit status: " + com4.getExitStatus());
//	
//				}else{
//					Log.fatal("SSH", "No OpenSSH-Server found. Please install it first, with:");
//					Log.fatal("SSH", "$sudo apt-get install openssh-server");
//				}
				

		}
	
	public void deleteSshAccess(String ip, PublicKey publicKey){
		
	}
	
	public void configureSshAccess(){
		
	}
	
}
