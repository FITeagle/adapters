package org.fiteagle.adapters.ACSclient;

import java.util.Properties;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hornetq.utils.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Path("/api")
public class ACSserverTest {
 
  @GET
  @Path("/listDevices")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public Response devicesTest(){
    return Response.status(200).entity("List Devices").build();

  }
  
  @GET
  @Path("/devices")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("application/json")
  public Response restTest(){
//    return Response.status(200).entity("Hello").build();
    Properties property = new Properties();
    property.put("FirstName", "myName");
//  String str = new StringBuffer("FirstName :").append("myName").toString();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonString = gson.toJson(property);

    return Response.status(200).entity(jsonString).build();
  }
  
  

}
