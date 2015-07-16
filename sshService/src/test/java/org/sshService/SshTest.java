
package org.sshService;

import static org.junit.Assert.*;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.adapters.sshService.SshService;
import org.fiteagle.adapters.sshService.SshServiceAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;




//import com.fasterxml.jackson.databind.Module.SetupContext;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SshTest {
	  private static SshServiceAdapter adapter;
	  private Map<String, SshService> instanceList;

	@BeforeClass
	public static void setup(){
	  Model model = ModelFactory.createDefaultModel();
	  Resource resource = model.createResource("afdfhad");
	  resource.addProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		adapter = new SshServiceAdapter(model, resource);
	}
	
	@Test (expected = NullPointerException.class)
	public void testWrongPassword() {
	  
	  
		  testCreateAccess("publickey", "deploytestuser", "http://localhost/resource/PhysicalNodeAdapter-1");
		  
		  if (executeCommand("uname -s").contains("Linux")){
			  File userDirectory = new File("/home/deploytestuser/");
			  assertTrue(!userDirectory.exists());
			
		  } else if (executeCommand("uname -s").contains("Darwin")){
			  File userDirectory = new File("/Users/deploytestuser/");
			  assertTrue(!userDirectory.exists());

		  }else {
			Log.fatal("SSH-Test", "OS not supported");
		}
		  
//		  testDeleteAccess();
//		  
//		  if (executeCommand("uname -s").contains("Linux")){
//			  File userDirectory = new File("/home/deploytestuser/");
//					  
//			assertFalse(userDirectory.exists());
//		  }else if (executeCommand("uname -s").contains("Darwin")){
//			  File userDirectory = new File("/Users/deploytestuser/");
//					  
//			assertFalse(userDirectory.exists());
//		  }else {
//			Log.fatal("SSH-Test", "OS not supported");
//		}
//		  

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

	
	private void testCreateAccess(String pubKey,String username, String adapterInstance){
		SshService sshService = new SshService(this.adapter, adapterInstance);
		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(adapterInstance);
		
		Statement stmt1 = model.createLiteralStatement(resource, Omn_service.publickey, pubKey);
		sshService.updateProperty(stmt1);
		
		Statement stamt2 = model.createLiteralStatement(resource, Omn_service.username, username);
		sshService.updateProperty(stamt2);
		
		sshService.getSshServiceAdapter().getSshParameters().setIP("localhost");
		if(sshService.getSshServiceAdapter().getSshParameters().getPassword() == null){
		  sshService.getSshServiceAdapter().getSshParameters().setPassword("wrong password");
		}
		sshService.addSshAccess();
		instanceList.put(username, sshService);

	}

//	private void testDeleteAccess(){
//		SshService sshService = instanceList.get("deploytestuser");
//		sshService.deleteSshAccess();
//		instanceList.remove("deploytestuser");
//
//	}
	
}

