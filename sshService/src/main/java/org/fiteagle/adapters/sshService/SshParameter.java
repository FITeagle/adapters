package org.fiteagle.adapters.sshService;

import org.fiteagle.api.core.Config;

public class SshParameter {
  
  private String ip;
  
  private String accessUsername;
  
  private String privateKeyPath;
  
  private String privateKeyPassword;
  
  private Config config;
  
  private int componentID_index;
  
  
  public SshParameter(String adapterInstance, Config config){
    
    this.config = config;
    setComponentIDIndex(adapterInstance);
    setIP();
    setAccessUsername();
    setPrivateKeyPath();
    setPrivateKeyPassword();
    
  }
  
  private void setComponentIDIndex(String adapterInstance){
    
    String componentIDs = config.getProperty(ISshService.COMPONENT_ID);
    if(componentIDs.contains(",")){
      String[] componentID_array = componentIDs.split("\\,");
      for(int counter = 0; counter < componentID_array.length; counter++){
       if(adapterInstance.equals(componentID_array[counter])){
         this.componentID_index = counter;
         break;
         }
       }
      } else this.componentID_index = 0;
    
//    System.out.println("INDEX IS " + componentID_index);
  }
  
  public int getComponentIDIndex(){
    return this.componentID_index;
  }
  
  private void setIP(){
    
    String IPs = config.getProperty(ISshService.IP);
    if(IPs.contains(",")){
      String[] IP_array = IPs.split("\\,");
      this.ip = IP_array[this.componentID_index];
    }
    else {
      this.ip = IPs;
    }
    System.out.println("IP IS " + ip);
  }
  
  public String getIP(){
    return this.ip;
  }
  
  private void setAccessUsername(){
    String acccessUsernames = config.getProperty(ISshService.USERNAME);
    if(acccessUsernames.contains(",")){
      String[] accessUsernames_array = acccessUsernames.split("\\,");
      this.accessUsername = accessUsernames_array[this.componentID_index];
    }
    else {
      this.accessUsername = acccessUsernames;
    }
  }
  
  public String getAccessUsername(){
    return this.accessUsername;
  }
  
  private void setPrivateKeyPath(){
    String keyPaths = config.getProperty(ISshService.PRIVATE_KEY_PATH);
    if(keyPaths.contains(",")){
      String[] paths_array = keyPaths.split("\\,");
      this.privateKeyPath = paths_array[this.componentID_index];
    }
    else {
      this.privateKeyPath = keyPaths;
    }
  }
  
  public String getPrivateKeyPath(){
    return this.privateKeyPath;
  }
  
  private void setPrivateKeyPassword(){
    String keyPasswords = config.getProperty(ISshService.PRIVATE_KEY_PASSWORD);
    if(keyPasswords.contains(",")){
      String[] passwords_array = keyPasswords.split("\\,");
      this.privateKeyPassword = passwords_array[this.componentID_index];
    }
    else {
      this.privateKeyPath = keyPasswords;
    }
  }
  
  public String getPrivateKeyPassword(){
    return this.privateKeyPassword;
  }
  
}
