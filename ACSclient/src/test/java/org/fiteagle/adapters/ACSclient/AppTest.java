package org.fiteagle.adapters.ACSclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.jena.atlas.json.io.parser.JSONParser;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Unit test for simple App.
 */
public class AppTest {
 
   

  @SuppressWarnings("deprecation")
  @Test
  public void testList() throws Exception, ClientProtocolException{
    
    ClientRequest request = new ClientRequest("http://localhost:8080/ACSclient/pyacs/test");
    request.accept("application/json");
    
    ClientResponse<String> response = request.get(String.class);
    
   
    System.out.println(response.getEntity());
    
    JSONObject json = (JSONObject) new JSONObject(response.getEntity());
    System.out.println("json object " + json.length() +  " " + json.getString("FirstName"));
    
    
//    final JsonReader jsonReader = Json
//        .createReader(new ByteArrayInputStream(response.getEntity().getBytes()));
//
//        final JsonObject jsonObject = jsonReader.readObject();
//        if(jsonObject.containsValue("myName")){
//          System.out.println("jsonObject ");
//        }
        
    
//    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
//    
//
//      String output;
//      System.out.println("Output from Server .... \n");
//      while ((output = br.readLine()) != null) {
//        System.out.println(output);
//      }
    
     }

  
//  @SuppressWarnings("deprecation")
//  @Test
//  public void listDevices() throws Exception, ClientProtocolException{
//    ClientRequest request = new ClientRequest("http://localhost:8080/ACSclient/pyacs/listDevices");
//    request.accept("text/html");
//    
//    ClientResponse<String> response = request.get(String.class);
//    
//    int re = response.getResponseStatus().getStatusCode();
//    System.out.println("adf " + re);
//   
//    
//    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
//    
//
//      String output;
//      System.out.println("Output from Server .... \n");
//      while ((output = br.readLine()) != null) {
//        System.out.println(output);
//      }
//  }
  

}
