package org.fiteagle.adapters.epc;

import info.openmultinet.ontology.vocabulary.Epc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class EpcGeneric {
	/**
	 * This class is the generic class from which all EPC resources inherit.
	 * 
	 * Any EPC resource can create an SSH connection to run a script to update
	 * rate, delay and packet loss
	 * 
	 */

	private final transient EpcAdapter owningAdapter;
	private final String instanceName;
	private Session session;
	private ChannelExec commandChannel;

	private String label;
	private int delayCode;
	private int packetlossCode;
	private int rateCode;

	private static final Logger LOGGER = Logger.getLogger(EpcGeneric.class
			.toString());

	/**
	 * Constructor method
	 * 
	 * @param owningAdapter
	 * @param instanceName
	 */
	public EpcGeneric(final EpcAdapter owningAdapter, final String instanceName) {
		this.owningAdapter = owningAdapter;
		this.instanceName = instanceName;

		this.delayCode = -1;
		this.rateCode = -1;
		this.packetlossCode = -1;

		this.session = null;
		this.commandChannel = null;
		this.label = null;
	}

	/**
	 * Updates rate, delay, and packtloss of current EPC resource
	 * 
	 * @param rateCode
	 * @param delayCode
	 * @param packetlossCode
	 */
	public void updateRateDelayPacktloss(int rateCode, int delayCode,
			int packetlossCode) {
		LOGGER.log(Level.INFO, "updateRateDelayPacktloss");

		String rateCodeString = Integer.toString(rateCode);
		String delayCodeString = Integer.toString(delayCode);
		String packetlossCodeString = Integer.toString(packetlossCode);

		String command = "net-fuseco.sh " + rateCodeString + " "
				+ delayCodeString + " " + packetlossCodeString;

		int status = executeCommand(command);
		if (status == 0) {
			this.rateCode = rateCode;
			this.delayCode = delayCode;
			this.packetlossCode = packetlossCode;
		}
	}

	public void updateRateDelayPacktloss() {
		updateRateDelayPacktloss(this.getRateCode(), this.getDelayCode(),
				this.getPacketlossCode());
	}

	/**
	 * Run the script to stop a running instance of an EPC resource
	 */
	public void stopInstance() {
		LOGGER.log(Level.INFO, "stopInstance");
		String command = "net-fuseco.sh stop";
		executeCommand(command);
	}

	/**
	 * Checks whether there is a connected session
	 * 
	 * @return
	 */
	protected boolean isSessionConnected() {
		boolean connected = false;

		if (session != null) {
			if (session.isConnected()) {
				connected = true;
			}
		}
		return connected;
	}

	/**
	 * Creates an SSH connection with the default username, password and IP
	 * address values
	 */
	protected void getConnection() {
		String password = "TEST_PW";
		String username = "TEST-USERNAME";
		String ip = "TEST-IP";

		getConnection(username, password, ip);
	}

	/**
	 * Creates an SSH connection with the given username, password and host IP
	 * address
	 * 
	 * @param username
	 * @param password
	 * @param ip
	 */
	void getConnection(String username, String password, String ip) {

		System.out.println("getConnection...");

		// kil current connection before making new one
		if (this.isSessionConnected()) {
			this.killConnection();
		}

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, ip);
			Properties properties = new Properties();
			properties.put("StrictHostKeyChecking", "no");
			session.setConfig(properties);
			session.setPassword(password);
			session.connect();
		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE, "could not connect to SSH-Server");
		}

		System.out.println("isConnected: "
				+ Boolean.toString(isSessionConnected()));
	}

	/**
	 * Executes the given command over the current connection, if it exists, or
	 * creates a new connection if it doesn't
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws JSchException
	 */
	protected int executeCommand(String command) {

		int status = -1;

		System.out.println("executeCommand...");
		if (!this.isSessionConnected()) {
			this.getConnection();
		}

		try {
			commandChannel = (ChannelExec) session.openChannel("exec");
		} catch (JSchException e1) {
			LOGGER.log(Level.SEVERE, "could not open channel");
		}

		InputStream in = null;
		try {
			in = commandChannel.getInputStream();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"could not get input stream from SSH-Server");
			return status;
		}

		OutputStream out = null;
		try {
			out = commandChannel.getOutputStream();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"could not get output stream from SSH-Server");
			return status;
		}

		commandChannel.setCommand(command);
		try {

			commandChannel.connect();
		} catch (JSchException e) {
			LOGGER.log(Level.SEVERE,
					"could not connect to exec channel via SSH-Server");
			return status;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = "";
		int index = 0;

		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(++index + " : " + line);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "could not read command");
			return status;
		}

		try {
			in.close();
			out.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (commandChannel.isClosed()) {
			status = commandChannel.getExitStatus();

			if (status != 0) {
				LOGGER.log(Level.SEVERE, "exit status for executeCommand \""
						+ command + "\": " + status);
			}
		}

		commandChannel.disconnect();

		return status;
	}

	/**
	 * Kills the current connection of this EPC resource
	 */
	public void killConnection() {
		System.out.println("killConnection...");

		if (commandChannel != null && commandChannel.isConnected()) {
			commandChannel.disconnect();
		}

		if (session != null && session.isConnected()) {
			session.disconnect();
		}
	}

	/**
	 * Methods to be overridden/expanded in subclasses
	 * 
	 * @param epcResource
	 */
	public void updateInstance(Resource epcResource) {

		if (epcResource.hasProperty(Epc.delayCode)) {
			int delay = epcResource.getProperty(Epc.delayCode).getObject()
					.asLiteral().getInt();

			this.setDelayCode(delay);
		}

		if (epcResource.hasProperty(Epc.rateCode)) {
			int rate = epcResource.getProperty(Epc.rateCode).getObject()
					.asLiteral().getInt();

			this.setRateCode(rate);
		}

		if (epcResource.hasProperty(Epc.packetlossCode)) {
			int packetLoss = epcResource.getProperty(Epc.packetlossCode)
					.getObject().asLiteral().getInt();

			this.setPacketlossCode(packetLoss);
		}
	}

	public void parseToModel(Resource resource) {
		if (this.getLabel() != null) {
			resource.addLiteral(RDFS.label, this.getLabel());
		}
		resource.addLiteral(Epc.delayCode, this.getDelayCode());
		resource.addLiteral(Epc.packetlossCode, this.getPacketlossCode());
		resource.addLiteral(Epc.rateCode, this.getRateCode());
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public String getInstanceName() {
		return this.instanceName;
	}

	public EpcAdapter getOwningAdapter() {
		return this.owningAdapter;
	}

	public int getDelayCode() {
		return this.delayCode;
	}

	public void setDelayCode(int delayCode) {
		this.delayCode = delayCode;
	}

	public int getPacketlossCode() {
		return this.packetlossCode;
	}

	public void setPacketlossCode(int packetlossCode) {
		this.packetlossCode = packetlossCode;
	}

	public int getRateCode() {
		return this.rateCode;
	}

	public void setRateCode(int rateCode) {
		this.rateCode = rateCode;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}