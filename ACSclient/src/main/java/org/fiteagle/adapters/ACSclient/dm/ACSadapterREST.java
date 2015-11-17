package org.fiteagle.adapters.ACSclient.dm;

import java.util.Properties;

import javax.json.JsonObject;
import javax.print.attribute.standard.Media;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;


@Path("/api")
public class ACSadapterREST {
 
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
  @Produces("text/html")
  public String restTest(){
//    return Response.status(200).entity("Hello").build();
    Properties property = new Properties();
    property.put("FirstName", "myName");
//  String str = new StringBuffer("FirstName :").append("myName").toString();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonString = gson.toJson(property);

    return jsonString;
  }
  
  
  @POST
  @Path("/jobs")
  @Consumes("application/json")
  @Produces("application/json")
  public Response configureParam(String configuration) throws JSONException{
    JSONObject json = (JSONObject) new JSONObject(configuration);
    
    Properties property = new Properties();
    property.put("first", json.getString("FirstName"));
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String jsonString = gson.toJson(property);
    
    return Response.status(200).entity(configuration).build();
    
  }
  
  

}
