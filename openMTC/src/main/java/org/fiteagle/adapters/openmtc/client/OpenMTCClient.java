package org.fiteagle.adapters.openmtc.client;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenMTCClient {

  private static Logger LOGGER = Logger.getLogger(OpenMTCClient.class.toString());
  
  private static OpenMTCClient instance;
  
  public static OpenMTCClient getInstance() {
    if(instance == null){
      instance = new OpenMTCClient();
    }
    return instance;
  }

  public void setUpConnection(Map<String, String> properties) {
    LOGGER.log(Level.INFO, "Setting up connection..");
    
  }
  
}
