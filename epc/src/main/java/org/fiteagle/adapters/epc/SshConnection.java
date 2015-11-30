package org.fiteagle.adapters.epc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Helper methods
 * 
 * @author robynloughnane
 *
 */
public class SshConnection {

	private Session session;
	private ChannelExec commandChannel;
	private String ip;
	private String password;
	private String username;

	private static final Logger LOGGER = Logger.getLogger(SshConnection.class
			.toString());

	public SshConnection() {
		this.session = null;
		this.commandChannel = null;
		this.ip = null;
		this.password = null;
		this.username = null;
	}

	public SshConnection(String ip, String password, String username) {
		this.session = null;
		this.commandChannel = null;
		this.ip = ip;
		this.password = password;
		this.username = username;
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
	 * Creates an SSH connection with the given username, password and host IP
	 * address
	 * 
	 * @param username
	 * @param password
	 * @param ip
	 */
	void getConnection() {

		if (ip == null || ip == "") {
			LOGGER.log(Level.SEVERE, "No ip address was provided.");
			return;
		}

		if (password == null || password == "") {
			LOGGER.log(Level.SEVERE, "No password was provided.");
			return;
		}

		if (username == null || username == "") {
			LOGGER.log(Level.SEVERE, "No username was provided.");
			return;
		}

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
	public int executeCommand(String command) {

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
	 * Getters and setters
	 * 
	 */
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public ChannelExec getCommandChannel() {
		return commandChannel;
	}

	public void setCommandChannel(ChannelExec commandChannel) {
		this.commandChannel = commandChannel;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}