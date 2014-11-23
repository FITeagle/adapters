package org.fiteagle.adapters.openmtc.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;

public class OpenMTCClient {

  private static Logger LOGGER = Logger.getLogger(OpenMTCClient.class.toString());
  
  private static OpenMTCClient instance;
  
  public static OpenMTCClient getInstance() {
    if(instance == null){
      instance = new OpenMTCClient();
    }
    return instance;
  }

  public void setUpConnection(Model createModel) {
    LOGGER.log(Level.INFO, "Setting up connection..");
    
  }
  
}
