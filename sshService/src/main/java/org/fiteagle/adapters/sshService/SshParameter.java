package org.fiteagle.adapters.sshService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  
  private String ip;
  
  private String accessUsername;
  
  private String privateKeyPath;
  
  private String privateKeyPassword;
  
  private String password;
  
  private Config config;
  
  private static Logger LOGGER  = Logger.getLogger(SshParameter.class.toString());
  
  
  public SshParameter(String adapterInstance, Config config){
    
    this.config = config;
    this.parseAdapterInstance(adapterInstance);

  }
  
  private void parseAdapterInstance(String adapterInstance){
    String jsonProperties;
    try {
      jsonProperties = config.readJsonProperties();
      JSONObject jsonObject;
      jsonObject = new JSONObject(jsonProperties);
      JSONArray adapterInstances = jsonObject.getJSONArray(ISshService.ADAPTER_INSTANCES);
      
      for (int i = 0; i < adapterInstances.length(); i++) {
        JSONObject adapterInstanceObject = adapterInstances.getJSONObject(i);
        if(adapterInstance.equals(adapterInstanceObject.getString(ISshService.COMPONENT_ID))){
          this.accessUsername = adapterInstanceObject.getString(ISshService.USERNAME);
          this.ip = adapterInstanceObject.getString(ISshService.IP);
          this.privateKeyPath = adapterInstanceObject.getString(ISshService.PRIVATE_KEY_PATH);
          this.privateKeyPassword = adapterInstanceObject.getString(ISshService.PRIVATE_KEY_PASSWORD);
          this.password = adapterInstanceObject.getString(ISshService.PASSWORD);
          }
        }
      } catch (JSONException e) {
      LOGGER.log(Level.SEVERE, " Error by parsing properties file ", e);
      }
    }
  
  
  public String getIP(){
    return this.ip;
  }
  
  public String getAccessUsername(){
    return this.accessUsername;
  }
  
  public String getPrivateKeyPath(){
    return this.privateKeyPath;
  }
  
  public String getPrivateKeyPassword(){
    return this.privateKeyPassword;
  }
  
  public String getPassword(){
    return this.password;
  }
  
}
