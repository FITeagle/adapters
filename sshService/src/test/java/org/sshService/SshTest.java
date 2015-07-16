
package org.sshService;

import static org.junit.Assert.*;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.jena.atlas.logging.Log;
import org.easymock.EasyMock;
import org.easymock.EasyMock.*;
import org.fiteagle.adapters.sshService.SSHConnector;
import org.fiteagle.adapters.sshService.SshParameter;
import org.fiteagle.adapters.sshService.SshService;
import org.fiteagle.adapters.sshService.SshServiceAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jcraft.jsch.*;
//import com.fasterxml.jackson.databind.Module.SetupContext;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.jcraft.jsch.JSchException;

public class SshTest {
  
	  private static SshServiceAdapter adapter;
	  private String adapterInstance;
	  private SSHConnector sshConnector;
	  private SshParameter sshParameter;
	  

	@Before
	public void setup() throws Exception{
	  Model model = ModelFactory.createDefaultModel();
	  Resource resource = model.createResource("afdfhad");
	  resource.addProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		adapter = new SshServiceAdapter(model, resource);
		adapterInstance = "test_ComponentID";
		sshConnector = EasyMock.createMock(SSHConnector.class);

		sshParameter = new SshParameter();
    sshParameter.setAccessUsername("testAccessUserName");
    sshParameter.setComponentID("test_ComponentID");
    sshParameter.setIP("xxx.xxx.xxx.xxx");
    sshParameter.setPassword("test_password");
    sshParameter.setPrivateKeyPassword("test_privateKeyPassword");
    sshParameter.setPrivateKeyPath("test_privateKeyPath");
    
    List<String> usernames = new LinkedList<String>();
    usernames.add("testUser");
    
    List<String> publicKeys = new LinkedList<String>();
    publicKeys.add("ssh-rsa jhadhruihjkghljwerhjbnsdje");
    
    
    sshConnector.setParameterTEST(usernames, publicKeys, sshParameter);
    
    sshConnector.createUserAccount();
    EasyMock.expectLastCall();
    EasyMock.replay(sshConnector);
	}
	
	
  @Test
  public void testCreateRemoteUser(){
    
    
    SshService sshService = new SshService(this.adapter, adapterInstance);
    
    sshService.createSSHtest(sshConnector);
    

  }
	
	

	
}

