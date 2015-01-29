package org.fiteagle.adapters.tosca.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

public class ToscaClient {
  
  private static Logger LOGGER = Logger.getLogger(ToscaClient.class.toString());
  
  private static String URL = "http://localhost:8080/api/rest/";
  private static String URL_ADMIN = URL+"admin/v2/";
  private static String URL_ORCHESTRATOR = URL+"orchestrator/v2/";
  private static String URL_TOSCA = URL+"tosca/v2";
  
  public static void getTest(){
    Client client = ClientBuilder.newClient();
    String result = client.target(URL_ADMIN+"templates/")
                       .request("application/json").get(String.class); 
    System.out.println(result);
  }
  
  public ToscaClient(){
    
  }
  
  public void putTest() {
    Client client = ClientBuilder.newClient();
    
    String body = loadResource("/openMTCGateway.json");
    
    Entity<String> e = Entity.entity(body, MediaType.APPLICATION_JSON);
        
    Response result = client.target(URL_ORCHESTRATOR+"topologies/")
                       .request(MediaType.APPLICATION_JSON).post(e); 
  }
  
  protected String loadResource(String path){
    InputStream is = getClass().getResourceAsStream(path);    
    String content = null;
    try {
      content = IOUtils.toString(is, "UTF-8");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return content;
  }
}

