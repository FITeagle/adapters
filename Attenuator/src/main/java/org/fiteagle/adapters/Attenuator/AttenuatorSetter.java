package org.fiteagle.adapters.Attenuator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import info.openmultinet.ontology.vocabulary.Acs;
import info.openmultinet.ontology.vocabulary.Epc;

import org.fiteagle.adapters.Exceptions.ConfigureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;


public class AttenuatorSetter implements Runnable{
  
  private Attenuator attenuator;
  private final String attenuator_value;
  
  private Logger LOGGER  = LoggerFactory.getLogger(this.getClass().getName());
  
  public AttenuatorSetter(Attenuator attenuator, Model configureModel){
   this.attenuator = attenuator; 
   this.attenuator_value = parseConfigureModel(configureModel);
   
  }
  
  public String parseConfigureModel(Model configureModel) {
    
    if(configureModel.contains((Resource) null, Epc.attenuator)){
      return configureModel.getProperty((Resource) null, Epc.attenuator).getString(); 
    }
    else 
      throw new ConfigureException("Configure model doesn't contain attenuator property");
  }
  
  
  @Override
  public void run(){
    
    Socket attenuator_socket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    
    String configureResoponse = null;
    
    try {
      attenuator_socket = new Socket(this.attenuator.get_attenuator_url(), Integer.parseInt(this.attenuator.get_attenuator_port()));
      out = new PrintWriter(attenuator_socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(attenuator_socket.getInputStream()));
      
      out.println("SA -R " + this.attenuator.get_attenuator_id() + " " + attenuator_value);
      
      configureResoponse = in.readLine();
      
      out.close();
      in.close();
      attenuator_socket.close();
      
  } catch (IOException e) {
   LOGGER.error("Attenuator couldn't be configured !");
  }
    
    LOGGER.info("Attenuator confiugration response: " + configureResoponse);
    
    String expectedResponse = "Atten #" + this.attenuator.get_attenuator_id() + " = " + attenuator_value + "dB";
    if(!configureResoponse.contains(expectedResponse)){
      throw new ConfigureException("Attenuator " + this.attenuator.get_attenuator_id() + " couldn't be assigned to " + attenuator_value + "dB");
    }

  }
  
  
  
}
