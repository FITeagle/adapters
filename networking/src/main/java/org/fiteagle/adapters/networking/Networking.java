package org.fiteagle.adapters.networking;

import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Statement;

public class Networking {
  
private static final Logger LOGGER = Logger.getLogger(Networking.class.toString());
  
  private final NetworkingAdapter owningAdapter;
  private final String linkName;
  
  public Networking(NetworkingAdapter owningAdapter, String linkName){
    
    this.owningAdapter = owningAdapter;
    this.linkName = linkName;
    
  }
  
  public String getLinkName(){
    return this.linkName;
  }
  
  public void updateProperty(Statement configureStatement) {
    
  }
  
  public void terminate() {
    
  }
}
