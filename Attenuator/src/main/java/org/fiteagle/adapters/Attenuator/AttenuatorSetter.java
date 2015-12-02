package org.fiteagle.adapters.Attenuator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

import info.openmultinet.ontology.vocabulary.Acs;
import info.openmultinet.ontology.vocabulary.Epc;

import org.apache.commons.net.telnet.TelnetClient;
import org.fiteagle.adapters.Exceptions.ConfigureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;


public class AttenuatorSetter implements Runnable{
  
  private Attenuator attenuator;
  private final int attenuator_value;
  private Resource adapterInstanceName;
  
  private Logger LOGGER  = LoggerFactory.getLogger(this.getClass().getName());
  
  public AttenuatorSetter(Attenuator attenuator, Model configureModel, Resource adapterInstance){
   this.attenuator = attenuator; 
   this.adapterInstanceName = adapterInstance;
   this.attenuator_value = parseConfigureModel(configureModel);
   LOGGER.info("requested new value for attenuator is " + this.attenuator_value);
  }
  
  public int parseConfigureModel(Model configureModel) {
    
    if(configureModel.contains((Resource) null, Epc.attenuator)){
      String value = configureModel.getProperty(adapterInstanceName, Epc.attenuator).getString(); 
      return Integer.parseInt(value);
    }
    else 
      throw new ConfigureException("Configure model doesn't contain attenuator property");
  }
  
  
  @Override
  public void run(){
    
    String configureResoponse = null;
    String attenuator_url = this.attenuator.get_attenuator_url();
    int attenuator_port = Integer.parseInt(this.attenuator.get_attenuator_port());
    int attenuator_id = Integer.parseInt(this.attenuator.get_attenuator_id());
    
    try {
      
      LOGGER.info("establishing Telnet connection with " + attenuator_url + ":" + attenuator_port + " ...");
      TelnetClient telnet = new TelnetClient();
      telnet.connect(attenuator_url, attenuator_port);
      
      if(telnet.isConnected()){
        LOGGER.info("Telnet connection has been established");
        BufferedReader telnetIN = new BufferedReader(new InputStreamReader(telnet.getInputStream()));
        PrintStream telnetOUT= new PrintStream(telnet.getOutputStream());
        
        String configCommand = "SA " + attenuator_id + " " + attenuator_value;
        LOGGER.info("configuring attenuator: " + configCommand + "  ...");
        telnetOUT.println(configCommand);
        telnetOUT.flush();
        
        try {
          Thread.sleep(1000);
          } catch (InterruptedException e) {
          e.printStackTrace();
          }
        
        String readAttenuator = "RA " + attenuator_id;
        telnetOUT.println(readAttenuator);
        telnetOUT.flush();
        
        configureResoponse = telnetIN.readLine();
        LOGGER.info("Configuration response: " + configureResoponse);
        
        LOGGER.info("closing Telnet connection ...");
        telnetOUT.close();
        telnetIN.close();
      } else
        LOGGER.error("Telnet connection couldn't be established");
      
      telnet.disconnect();
      
  } catch (IOException e) {
   LOGGER.error("Telnet connection with Attenuator couldn't be established !");
  }
    
    String expectedResponse = "Atten #" + attenuator_id + " = " + attenuator_value + "dB";
    if(!configureResoponse.contains(expectedResponse)){
      throw new ConfigureException("Attenuator " + attenuator_id + " couldn't be assigned to " + attenuator_value + "dB");
    }

  }
  
  
  
}
