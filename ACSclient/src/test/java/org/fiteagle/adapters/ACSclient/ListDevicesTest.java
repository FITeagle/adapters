package org.fiteagle.adapters.ACSclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

public class ListDevicesTest {
  
  @SuppressWarnings("deprecation")
//  @Test
  public void listDevices() throws Exception, ClientProtocolException{
    ClientRequest request = new ClientRequest("http://localhost:8080/ACSclient/api/devices");
    request.accept("application/json");
    
    ClientResponse<String> response = request.get(String.class);
    
    int re = response.getResponseStatus().getStatusCode();
    System.out.println("response code " + re);
   
    System.out.println(response.getEntity());
    
    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
    

      String output;
      System.out.println("Output from Server .... \n");
      while ((output = br.readLine()) != null) {
        System.out.println(output);
      }
  }
}
