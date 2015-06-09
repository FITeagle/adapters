package org.fiteagle.adapters.sshService;

import com.jcraft.jsch.*;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author AlaaAlloush
 *
 */
public class SSHConnector {
  
  private static Logger LOGGER  = Logger.getLogger(SSHConnector.class.toString());
  
  private JSch jsch;
  
  private SshParameter sshParameter;
  
  private String newUser;
 
  private String sshKey;
  
  public SSHConnector(String newUser, String sshKey, SshParameter sshParameter){
    
    this.sshParameter = sshParameter;
    this.jsch = new JSch();
    
    setNewUserName(newUser);
    setSshKey(sshKey);
    
  }
  
  
  private void setNewUserName(String newUser){
    this.newUser = newUser;
  }
  
  private void setSshKey(String sshKey){
    this.sshKey = sshKey;
  }
  
  
  public void createUserAccount(){
    com.jcraft.jsch.Session session = null;

    try { 
      jsch.addIdentity(sshParameter.getPrivateKeyPath(), sshParameter.getPrivateKeyPassword());
      session = jsch.getSession(sshParameter.getAccessUsername(), sshParameter.getIP(), 22);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();

      Properties prop = new Properties();
      prop.put("StrictHostKeyChecking", "no");
      session.setConfig(prop);
      session.connect();

      // createUserAccount
      ChannelExec channel_createUserAccount = (ChannelExec)session.openChannel("exec");
      channel_createUserAccount.setOutputStream(stream);
      channel_createUserAccount.setCommand("sudo -S adduser -gecos \"" + newUser + " "
          + newUser + ", test@test.test\" -disabled-password "
          + newUser + " ");
      executeCommand(channel_createUserAccount);

      
      // createUserSSHDirectory
      ChannelExec channel_createUserSSHDirectory = (ChannelExec)session.openChannel("exec");
      channel_createUserSSHDirectory.setOutputStream(stream);
      channel_createUserSSHDirectory.setCommand("sudo -S mkdir /home/" + newUser + "/.ssh");
      executeCommand(channel_createUserSSHDirectory);
      
      
      // createAuthorizedKeysFile
      ChannelExec channel_createAuthorizedKeysFile = (ChannelExec)session.openChannel("exec");
      channel_createAuthorizedKeysFile.setOutputStream(stream);
      channel_createAuthorizedKeysFile.setCommand("sudo -S touch /home/" + newUser
          + "/.ssh/authorized_keys");
      executeCommand(channel_createAuthorizedKeysFile);
            
      //changeOwnerOfUserHome
      ChannelExec channel_changeOwnerOfUserHome = (ChannelExec)session.openChannel("exec");
      channel_changeOwnerOfUserHome.setOutputStream(stream);
      channel_changeOwnerOfUserHome.setCommand("sudo -S chown -R " + newUser + ":" + newUser
          + " /home/" + newUser + "/.ssh");
      executeCommand(channel_changeOwnerOfUserHome);

      
      // addSSHKey
      ChannelExec channel_addSSHKey = (ChannelExec)session.openChannel("exec");
      channel_addSSHKey.setOutputStream(stream);
      channel_addSSHKey.setCommand("sudo -S bash -c 'echo " + sshKey + " >> /home/"
          + newUser + "/.ssh/authorized_keys'");
      executeCommand(channel_addSSHKey);

    }
    
    catch (JSchException ex) {
      LOGGER.log(Level.SEVERE, " problem by creating this user ", ex);
    }
    finally {
      if (session != null)
        session.disconnect();
    }
  }
  
  
  public void deleteUserAccount(){
    
    com.jcraft.jsch.Session session = null;

  try { 
    jsch.addIdentity(sshParameter.getPrivateKeyPath(), sshParameter.getPrivateKeyPassword());
    session = jsch.getSession(sshParameter.getAccessUsername(), sshParameter.getIP(), 22);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    Properties prop = new Properties();
    prop.put("StrictHostKeyChecking", "no");
    session.setConfig(prop);
    session.connect();

    // lockAccount
    ChannelExec channel_lockAccount = (ChannelExec)session.openChannel("exec");
    channel_lockAccount.setOutputStream(stream);
    channel_lockAccount.setCommand("sudo -S passwd -l " + newUser + "");
    executeCommand(channel_lockAccount);
    
    
    // killAllUserProcesses
    ChannelExec channel_killAllUserProcesses = (ChannelExec)session.openChannel("exec");
    channel_killAllUserProcesses.setOutputStream(stream);
    channel_killAllUserProcesses.setCommand("sudo -S killall -KILL -u " + newUser + "");
    executeCommand(channel_killAllUserProcesses);
    
    
    // deleteUser
    ChannelExec channel_deleteUser = (ChannelExec)session.openChannel("exec");
    channel_deleteUser.setOutputStream(stream);
    channel_deleteUser.setCommand("sudo -S userdel -r " + newUser + "");
    executeCommand(channel_deleteUser);
    

    // DeleteUserDirectory
    ChannelExec channel_deleteUserDirectory = (ChannelExec)session.openChannel("exec");
    channel_deleteUserDirectory.setOutputStream(stream);
    channel_deleteUserDirectory.setCommand("sudo -S rm -R /home/" + newUser + "");
    executeCommand(channel_deleteUserDirectory);
 
  }
  
  catch (JSchException ex) {
    LOGGER.log(Level.SEVERE, " problem by deleting the user ", ex);
  }
  finally {
    if (session != null)
      session.disconnect();
  }
  }
  
  private void executeCommand(ChannelExec channel){
    try {
      channel.connect(1000);
      java.lang.Thread.sleep(500);
      channel.disconnect();
    } catch (JSchException e) {
      LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
    }
    catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, " problem by executing this command ", e);
    }   
    
  }
  
  
}
