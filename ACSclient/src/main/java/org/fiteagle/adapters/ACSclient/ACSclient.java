package org.fiteagle.adapters.ACSclient;

import org.fiteagle.adapters.ACSclient.ACSclientAdapter;

import com.hp.hpl.jena.rdf.model.Statement;

public class ACSclient {
  
  private final transient ACSclientAdapter owningAdapter;
  private final String instanceName;
  
  public ACSclient(final ACSclientAdapter owningAdapter, final String instanceName){
    
    this.owningAdapter = owningAdapter;
    this.instanceName = instanceName;
  }
  
  public String getInstanceName() {
    return this.instanceName;
    }
  
  public void updateProperty(Statement configureStatement) {
    
  }
  
  public void terminate() {
    
  }

}
