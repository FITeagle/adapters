package org.fiteagle.adapters.sshService;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author AlaaAlloush
 */
public class SSHConnector {

    private static Logger LOGGER = Logger.getLogger(SSHConnector.class.toString());

    private JSch jsch;

    private SshParameter sshParameter;

    private List<String> usernames = new ArrayList<>();
    ;

    private List<String> publicKeys = new ArrayList<>();

    public SSHConnector(List<String> usernames, List<String> publicKeys, SshParameter sshParameter) {

        this.sshParameter = sshParameter;
        this.jsch = new JSch();

        setUsernames(usernames);
        setPublicKeys(publicKeys);

    }

    public List<String> getUsernames() {
        return this.usernames;
    }

    private void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getPublicKeys() {
        return this.publicKeys;
    }

    private void setPublicKeys(List<String> publicKeys) {
        this.publicKeys = publicKeys;
    }

    public void setParameterTEST(List<String> usernames, List<String> publicKeys, SshParameter sshParameter) {

        this.sshParameter = sshParameter;
        this.jsch = new JSch();

        setUsernames(usernames);
        setPublicKeys(publicKeys);

    }

    public void createUserAccount() {
        com.jcraft.jsch.Session session = null;
        String password = null;
        try {
            session = jsch.getSession(sshParameter.getAccessUsername(), sshParameter.getIP(), Integer.parseInt(sshParameter.getPort()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            if (sshParameter.getPrivateKeyPath().isEmpty() || sshParameter.getPrivateKeyPassword().isEmpty()) {
                // Authentication via username and password
                session.setPassword(sshParameter.getPassword());
                password = sshParameter.getPassword();

            } else {
                // Authentication via private key
                jsch.addIdentity(sshParameter.getPrivateKeyPath(), sshParameter.getPrivateKeyPassword());

            }

            session.connect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // createUserAccount
            for (String newUser : this.getUsernames()) {
                ChannelExec channel_createUserAccount = (ChannelExec) session.openChannel("exec");
                channel_createUserAccount.setOutputStream(stream);
                String command = "";
                if (password != null) {
                    command = "echo " + password + "| sudo -S adduser -gecos \"" + newUser + " "
                            + newUser + ", test@test.test\" -disabled-password "
                            + newUser + "";
                    LOGGER.log(Level.INFO, "executing command: " + command);
                    channel_createUserAccount.setCommand(command);
                } else {
                    channel_createUserAccount.setCommand("sudo -S adduser -gecos \"" + newUser + " "
                            + newUser + ", test@test.test\" -disabled-password "
                            + newUser + " ");
                }

                executeCommand(channel_createUserAccount);

                //add group experimenters

                ChannelExec channelNewGroup = (ChannelExec) session.openChannel("exec");

                channelNewGroup.setOutputStream(stream);

                if (password != null) {
                    command = "sleep 1; echo " + password + "| sudo -S addgroup experimenters";
                    LOGGER.log(Level.INFO, "executing command: " + command);
                    channelNewGroup.setCommand(command);
                } else {
                    channelNewGroup.setCommand("sudo -S addgroup experimenters");
                }
                executeCommand(channelNewGroup);


                //add User to group experimenters

                ChannelExec channelAddGroup = (ChannelExec) session.openChannel("exec");

                channelAddGroup.setOutputStream(stream);

                if (password != null) {
                    command = "sleep 1; echo " + password + "| sudo -S adduser "+newUser+" experimenters";
                    LOGGER.log(Level.INFO, "executing command: " + command);
                    channelAddGroup.setCommand(command);
                } else {
                    channelAddGroup.setCommand("sudo -S adduser "+newUser+" experimenters");
                }
                executeCommand(channelAddGroup);

                // createUserSSHDirectory
                ChannelExec channel_createUserSSHDirectory = (ChannelExec) session.openChannel("exec");
                channel_createUserSSHDirectory.setOutputStream(stream);
                if (password != null) {
                    command = "sleep 1; echo " + password + "| sudo -S mkdir /home/" + newUser + "/.ssh";
                    LOGGER.log(Level.INFO, "executing command: " + command);
                    channel_createUserSSHDirectory.setCommand(command);
                } else {
                    channel_createUserSSHDirectory.setCommand("sudo -S mkdir /home/" + newUser + "/.ssh");
                }
                executeCommand(channel_createUserSSHDirectory);


                // createAuthorizedKeysFile
                ChannelExec channel_createAuthorizedKeysFile = (ChannelExec) session.openChannel("exec");
                channel_createAuthorizedKeysFile.setOutputStream(stream);
                if (password != null) {
                    command = "sleep 1; echo " + password + "| sudo -S touch /home/" + newUser
                            + "/.ssh/authorized_keys";
                    LOGGER.log(Level.INFO, "executing command: " + command);
                    channel_createAuthorizedKeysFile.setCommand(command);
                } else {
                    channel_createAuthorizedKeysFile.setCommand("sudo -S touch /home/" + newUser
                            + "/.ssh/authorized_keys");
                }
                executeCommand(channel_createAuthorizedKeysFile);


                //changeOwnerOfUserHome
                ChannelExec channel_changeOwnerOfUserHome = (ChannelExec) session.openChannel("exec");
                channel_changeOwnerOfUserHome.setOutputStream(stream);
                if (password != null) {
                    command = "sleep 1; echo " + password + "| sudo -S chown -R " + newUser + ":" + newUser
                            + " /home/" + newUser + "/.ssh";
                    LOGGER.log(Level.INFO, "executing command: " + command);
                    channel_changeOwnerOfUserHome.setCommand(command);
                } else {
                    channel_changeOwnerOfUserHome.setCommand("sudo -S chown -R " + newUser + ":" + newUser
                            + " /home/" + newUser + "/.ssh");
                }
                executeCommand(channel_changeOwnerOfUserHome);


                // addSSHKeys
                for (String sshKey : this.getPublicKeys()) {
                    ChannelExec channel_addSSHKey = (ChannelExec) session.openChannel("exec");
                    channel_addSSHKey.setOutputStream(stream);
                    if (password != null) {
                        command = "sleep 1; echo " + password + "| sudo -S bash -c 'echo " + sshKey + " >> /home/"
                                + newUser + "/.ssh/authorized_keys'";
                        LOGGER.log(Level.INFO, "executing command: " + command);
                        channel_addSSHKey.setCommand(command);
                    } else {
                        channel_addSSHKey.setCommand("sudo -S bash -c 'echo " + sshKey + " >> /home/"
                                + newUser + "/.ssh/authorized_keys'");
                    }
                    executeCommand(channel_addSSHKey);
                }
            }
        } catch (JSchException ex) {
            LOGGER.log(Level.SEVERE, " problem by creating this user ", ex);
        } finally {
            if (session != null)
                session.disconnect();
        }
    }


    public void deleteUserAccount() {

        com.jcraft.jsch.Session session = null;
        String password = null;
        try {

            session = jsch.getSession(sshParameter.getAccessUsername(), sshParameter.getIP(), Integer.parseInt(sshParameter.getPort()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            if (sshParameter.getPrivateKeyPath().isEmpty() || sshParameter.getPrivateKeyPassword().isEmpty()) {
                session.setPassword(sshParameter.getPassword());
                password = sshParameter.getPassword();
            } else {
                jsch.addIdentity(sshParameter.getPrivateKeyPath(), sshParameter.getPrivateKeyPassword());
            }

            session.connect();

            // lockAccount
            for (String newUser : this.getUsernames()) {
                ChannelExec channel_lockAccount = (ChannelExec) session.openChannel("exec");
                channel_lockAccount.setOutputStream(stream);
                if (password != null) {
                    channel_lockAccount.setCommand("echo " + password + "| sudo -S passwd -l " + newUser + "");
                } else {
                    channel_lockAccount.setCommand("sudo -S passwd -l " + newUser + "");
                }

                executeCommand(channel_lockAccount);


                // killAllUserProcesses
                ChannelExec channel_killAllUserProcesses = (ChannelExec) session.openChannel("exec");
                channel_killAllUserProcesses.setOutputStream(stream);
                if (password != null) {
                    channel_killAllUserProcesses.setCommand("echo " + password + "| sudo -S killall -KILL -u " + newUser + "");
                } else {
                    channel_killAllUserProcesses.setCommand("sudo -S killall -KILL -u " + newUser + "");
                }
                executeCommand(channel_killAllUserProcesses);


                // deleteUser
                ChannelExec channel_deleteUser = (ChannelExec) session.openChannel("exec");
                channel_deleteUser.setOutputStream(stream);
                if (password != null) {
                    channel_deleteUser.setCommand("echo " + password + "| sudo -S userdel -r " + newUser + "");
                } else {
                    channel_deleteUser.setCommand("sudo -S userdel -r " + newUser + "");
                }

                executeCommand(channel_deleteUser);


                // DeleteUserDirectory
                ChannelExec channel_deleteUserDirectory = (ChannelExec) session.openChannel("exec");
                channel_deleteUserDirectory.setOutputStream(stream);
                if (password != null) {
                    channel_deleteUserDirectory.setCommand("echo " + password + "| sudo -S rm -R /home/" + newUser + "");
                } else {
                    channel_deleteUserDirectory.setCommand("sudo -S rm -R /home/" + newUser + "");
                }
                executeCommand(channel_deleteUserDirectory);

            }
        } catch (JSchException ex) {
            LOGGER.log(Level.SEVERE, " problem by deleting the user ", ex);
        } finally {
            if (session != null)
                session.disconnect();
        }
    }

    private void executeCommand(ChannelExec channel) {
        try {
            channel.connect();
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


            channel.disconnect();

        } catch (JSchException e) {
            LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
        }

    }


}
