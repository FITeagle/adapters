//package org.sshService;
//
//import static org.junit.Assert.*;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//import org.apache.jena.atlas.logging.Log;
//import org.fiteagle.adapters.sshService.SshServiceAdapter;
//import org.fiteagle.api.core.Config;
//import org.fiteagle.api.core.IConfig;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.fasterxml.jackson.databind.Module.SetupContext;
//
//public class SshTest {
//	  private static SshServiceAdapter adapter;
//
//	@BeforeClass
//	public static void setup(){
//		adapter = new SshServiceAdapter();
//	}
//	
//	@Test
//	public void createAndDeleteSsh() {
//
//		adapter.testCreateAccess("publickey", "deploytestuser");
//		  
//		  if (executeCommand("uname -s").contains("Linux")){
//			  File userDirectory = new File("/home/deploytestuser/");
//			  File sshDirectory = new File("/home/deploytestuser/.ssh/");
//					  
//			assertTrue(userDirectory.exists());
//			assertTrue(sshDirectory.exists());
//		  }else if (executeCommand("uname -s").contains("Darwin")){
//			  File userDirectory = new File("/Users/deploytestuser/");
//			  File sshDirectory = new File("/Users/deploytestuser/.ssh/");
//					  
//			assertTrue(userDirectory.exists());
//			assertTrue(sshDirectory.exists());
//		  }else {
//			Log.fatal("SSH-Test", "OS not supported");
//		}
//		  
//		  adapter.testDeleteAccess();
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
//
//	}
//	
//
//
//	
//	private String executeCommand(String command) {
//		StringBuffer output = new StringBuffer();
//		Process p;
//		try {
//			p = Runtime.getRuntime().exec(command);
//			p.waitFor();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					p.getInputStream()));
//
//			String line = "";
//			while ((line = reader.readLine()) != null) {
//				output.append(line + "\n");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		return output.toString();
//	}
//}
