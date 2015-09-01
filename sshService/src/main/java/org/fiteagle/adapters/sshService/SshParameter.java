package org.fiteagle.adapters.sshService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;

import org.fiteagle.api.core.Config;
import org.hornetq.utils.json.JSONArray;
import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

/**
 * 
 * @author AlaaAlloush
 *
 */
public class SshParameter {
  
  private String componentID;
  
  private String ip;
  
  private String accessUsername;
  
  private String privateKeyPath;
  
  private String privateKeyPassword;
  
  private String password;

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  private String port;

  private static Logger LOGGER  = Logger.getLogger(SshParameter.class.toString());
  
  
  public SshParameter(){
    
  }
  
  
  public String getComponentID(){
    return this.componentID;
  }
  
  public void setComponentID(String componentID){
    this.componentID = componentID;
  }
  
  public String getIP(){
    return this.ip;
  }
  
  public void setIP(String ip){
    this.ip = ip;
  }
  
  public String getAccessUsername(){
    return this.accessUsername;
  }
  
  public void setAccessUsername(String accessUsername){
    this.accessUsername = accessUsername;
  }
  
  public String getPrivateKeyPath(){
    return this.privateKeyPath;
  }
  
  public void setPrivateKeyPath(String privateKeyPath){
    this.privateKeyPath = privateKeyPath;
  }
  
  public String getPrivateKeyPassword(){
    return this.privateKeyPassword;
  }
  
  public void setPrivateKeyPassword(String privateKeyPassword){
    this.privateKeyPassword = privateKeyPassword;
  }
  
  public String getPassword(){
    return this.password;
  }
  
  public void setPassword(String password){
    this.password = password;
  }
  
}
