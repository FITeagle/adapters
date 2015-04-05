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
	  private Preferences preferences;
	  private String password;

	public static Map<String, AbstractAdapter> adapterInstances;
	 
	
	public SshService () {
		this.preferences = Preferences.userNodeForPackage(getClass());
		addPreferences("password", "123456");
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
	

	 
	
	public void addSshAccess(String newUser,String publicKey){
		
		if(password == null){
			password = getPreferences(password);
		}
		
		try {
			Session session = client.startSession();

			try {
				Command com2 = session.exec("dpkg -s openssh-server | grep -c installed");
				
				if(IOUtils.readFully(com2.getInputStream())
						.toString().equals("1")){	
					
					Command com3 = session.exec("echo '" + password + "' | sudo -kS adduser "+newUser);
					System.out.println(IOUtils.readFully(com3.getInputStream())
							.toString());
					System.out.println("\n **exit status: " + com3.getExitStatus());
	
					Command com4 = session.exec("echo '" + password + "' | sudo -kS mkdir -p /home/"+newUser+"/.ssh |sudo bash -c 'echo " + publicKey + " >> /home/"
							+ newUser + "/.ssh/authorized_keys'");
					System.out.println(IOUtils.readFully(com4.getInputStream())
							.toString());
					com4.join(5, TimeUnit.SECONDS);
					System.out.println("\n **exit status: " + com4.getExitStatus());
	
				}else{
					Log.fatal("SSH", "No OpenSSH-Server found. Please install it first, with:");
					Log.fatal("SSH", "$sudo apt-get install openssh-server");
				}
				
			} finally {
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
		}
	
	public void deleteSshAccess(String ip, PublicKey publicKey){
		
	}
	
	public void configureSshAccess(){
		
	}
	
	private String getPreferences(String key){
		return preferences.get(key, null);
	}
	private void addPreferences(String key,String value){
		preferences.put(key, value);
	}
	
}
