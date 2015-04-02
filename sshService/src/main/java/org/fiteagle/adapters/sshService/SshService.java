package org.fiteagle.adapters.sshService;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.hp.hpl.jena.rdf.model.AnonId;

public class SshService {
	  protected SshServiceAdapter owningAdapter;
	  private String instanceName;
	  private List<String> ipStrings = new ArrayList<>();

	public static Map<String, AbstractAdapter> adapterInstances;
	 
	public SshService () {

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
	
	public void addSshAccess(String ip,String publicKey){
	Log.fatal("IP",ip);
	Log.fatal("publicKey",publicKey);

	}
	
	public void deleteSshAccess(String ip, PublicKey publicKey){
		
	}
	
	public void configureSshAccess(){
		
	}
}
