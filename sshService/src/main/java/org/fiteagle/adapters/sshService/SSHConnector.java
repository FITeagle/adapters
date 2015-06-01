package org.fiteagle.adapters.sshService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;


public class SSHConnector {
  
  private String ip = "";

  private SSHClient client;


  public SSHClient getSSHClient(){
    return this.client;
  }
  
  public SSHConnector(String ip) {
    this.ip = ip;
   
  }

  public void connect() {

    client = new SSHClient();
    InetAddress host;
    try {
      host = InetAddress.getByName(ip);

      client.addHostKeyVerifier(new HostKeyVerifier() {

        public boolean verify(String arg0, int arg1, PublicKey arg2) {
          // TODO Auto-generated method stub
          return true;
        }
      });
//      client.connect(host);
      client.connect(ip);
      //client.connect(host,22);
//      client.authPassword(username, password);
//      client.authPublckey("alaafed");

      
      KeyProvider keys;
      try{
      keys=client.loadKeys("/Users/AlaaAlloush/.ssh/sshkey");
      } catch (IOException e) {
        throw new RuntimeException("Cannot read key from private key file ",e);
      }
      
      
      client.authPublickey("alaafed",keys);
      
      this.client = client;
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void createUserAccount(String newUser, String password) {
    
    try {
      Session session = client.startSession();
      Command cmd;
      try {
        if(password != null){
        cmd = session.exec("echo " + password
            + "| sudo -S adduser -gecos \"" + newUser + " "
            + newUser + ", test@test.test\" -disabled-password "
            + newUser + " ");
        }
        else {
          cmd = session.exec("echo " + password
              + "| sudo -S adduser -gecos \"" + newUser + " "
              + newUser + ", test@test.test\" -disabled-password "
              + newUser + " ");
        }
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());

        System.out.println("\n **exit status: " + cmd.getExitStatus());
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

  public void disconnect() {
    try {
      this.client.disconnect();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void createUserSSHDirectory(String newUser, String password) {

    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
        cmd = session.exec("echo " + password
            + "| sudo -S mkdir /home/" + newUser + "/.ssh");
        } else {
          cmd = session.exec("sudo -S mkdir /home/" + newUser + "/.ssh");
        }
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        System.out.println("\n **exit status: " + cmd.getExitStatus());

       
    } finally {
      session.close();
    }
    }
      catch (ConnectionException e) {
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

  public void createAuthorizedKeysFile(String newUser, String password) {

    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
        cmd = session.exec("echo " + password
            + "| sudo -S touch /home/" + newUser
            + "/.ssh/authorized_keys");
        } else {
          cmd = session.exec("sudo -S touch /home/" + newUser
              + "/.ssh/authorized_keys");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: " + cmd.getExitStatus());

    } finally {
      session.close();
    }
    }
      catch (ConnectionException e) {
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

  public void changeOwnerOfUserHome(String newUser, String password) {

    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
        cmd = session.exec("echo " + password
            + "| sudo -S chown -R " + newUser + ":" + newUser
            + " /home/" + newUser + "/.ssh");
        }
        else {
          cmd = session.exec("sudo -S chown -R " + newUser + ":" + newUser
              + " /home/" + newUser + "/.ssh");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: " + cmd.getExitStatus());

   
    } finally {
      session.close();
    }
    }
      catch (ConnectionException e) {
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

  public void addSSHKey(String sshKey, String newUser, String password) {

    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
          cmd = session.exec("echo " + password
              + "| sudo -S bash -c 'echo " + sshKey + " >> /home/"
              + newUser + "/.ssh/authorized_keys'");
        }
        else {
          cmd = session.exec("sudo -S bash -c 'echo " + sshKey + " >> /home/"
              + newUser + "/.ssh/authorized_keys'");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: " + cmd.getExitStatus());

     
    } finally {
      session.close();
    }
    }
      catch (ConnectionException e) {
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

  public void deleteUser(String username, String password) {
    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
          cmd = session.exec("echo " + password
              + "| sudo -S userdel -r " + username + "");
        }
        else {
          cmd = session.exec("sudo -S userdel -r " + username + "");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: "
            + cmd.getExitStatus());

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

  public void deleteUserDirectory(String username, String password) {
    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
          cmd = session.exec("echo " + password
              + "| sudo -S rm -R /home/" + username + "");
        }
        else {
          cmd = session.exec("sudo -S rm -R /home/" + username + "");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: "
            + cmd.getExitStatus());

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

  public void lockAccount(String username, String password) {
    try {
      Session session = client.startSession();
      try {

        Command cmd; 
        if(password != null){
          cmd = session.exec("echo " + password
              + "| sudo -S passwd -l " + username + "");
        }
        else {
          cmd = session.exec("sudo -S passwd -l " + username + "");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: "
            + cmd.getExitStatus());

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

  public void killAllUserProcesses(String username, String password) {
    try {
      Session session = client.startSession();
      try {
        Command cmd;
        if(password != null){
          cmd = session.exec("echo " + password
              + "| sudo -S killall -KILL -u " + username + "");
        }
        else {
          cmd = session.exec("sudo -S killall -KILL -u " + username + "");
        }
        System.out.println(IOUtils.readFully(cmd.getInputStream())
            .toString());
        cmd.join(5, TimeUnit.SECONDS);
        System.out.println("\n **exit status: "
            + cmd.getExitStatus());

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
  
}
