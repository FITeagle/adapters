package org.fiteagle.adapters.epc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class EpcImplementable {

	private final transient EpcAdapter owningAdapter;
	private final String instanceName;
	
	private JSch jsch;
	private ChannelExec commandChannel;
	
	private static final Logger LOGGER = Logger.getLogger(Epc.class.toString());


	@SuppressWarnings("PMD.DoNotUseThreads")
	private transient Thread thread;
	private final static String THREAD_FACTORY = "java:jboss/ee/concurrency/factory/default";
	private transient ManagedThreadFactory threadFactory;

	public EpcImplementable(final EpcAdapter owningAdapter,
			final String instanceName) {

		this.owningAdapter = owningAdapter;
		this.instanceName = instanceName;
	}

	public String getInstanceName() {
		return this.instanceName;
	}

	public void updateProperty(final Statement configureStatement) {

	}

	public void updateInstance(Resource epcResource) {
		LOGGER.log(Level.INFO,"################## executing command");
		jsch = new JSch();
		
		String rateCode = "";
		String delayCode = "";
		String lossCode = "";

		if(commandChannel == null ){
			commandChannel = getConnection();
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		commandChannel.setOutputStream(stream);
		String command = "net-fuseco.sh " + rateCode + " "+delayCode + " " + lossCode ;
		commandChannel.setCommand(command);
		executeCommand(commandChannel);
	}

	public void parseToModel(Resource resource) {

	}
	
	private ChannelExec getConnection() {
		try {
			Session session = jsch.getSession("TEST-USERNAME","TEST-IP");
			Properties prop = new Properties();
			prop.put("StrictHostKeyChecking", "no");
			session.setConfig(prop);
			session.setPassword("TEST_PW");
			session.connect();
			ChannelExec commandChannel = (ChannelExec) session.openChannel("exec");
			
			return commandChannel;
		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, "could not connect to SSH-Server");
		}	
		return null;
	}
	
	private void executeCommand(ChannelExec channel) {

		try {
			if(!channel.isConnected()){
				channel.connect();
			}
			InputStream in = channel.getInputStream();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) break;
					LOGGER.log(Level.INFO, new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					LOGGER.log(Level.INFO, "exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}


//			channel.disconnect();

		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
		}

	}
	
	
	
	public void stopInstance(){
		if(commandChannel== null){
			commandChannel	= getConnection();
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		commandChannel.setOutputStream(stream);
		String command = "net-fuseco.sh stop" ;
		commandChannel.setCommand(command);
		executeCommand(commandChannel);
		
		
	}
	
	

}
