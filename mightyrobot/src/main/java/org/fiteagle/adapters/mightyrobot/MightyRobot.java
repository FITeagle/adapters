package org.fiteagle.adapters.mightyrobot;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public class MightyRobot{

	@GET
	@Path("/description.ttl")
	@Produces("text/plain")
	public String getDescription(){
		
		return MRHandler.getDescription();
	
	} 
	
	@GET
	@Path("/instances.ttl")
	@Produces("text/plain")
	public String getInstances(){
		
		return MRHandler.getInstances();

	}
	
	@GET
	@Path("/instance/{instanceName}/description.ttl")
	@Produces("text/plain")
	public String getInstanceDescription(@PathParam("instanceName") String instanceName){
		
		return "Description for instance " + instanceName + " is\n" + MRHandler.getInstanceDescription(instanceName);
		
	}
	
	@POST
	@Path("/instance/{instanceName}")
	public String provisionInstance(@PathParam("instanceName") String instanceName){

		
		return (MRHandler.provisionInstance(instanceName)) ? 
				"Instance " + instanceName + " provisioned.\n" : 
				"Instance " + instanceName + " not provisioned. Invalid name or already exists.\n";

	}
	
	@PUT
	@Path("/instance/{instanceName}/description.ttl")
	@Produces("text/plain")
	public String putInstanceDescription(@PathParam("instanceName") String instanceName, @FormParam("Description") String description){
		
		return MRHandler.putInstanceDescription(instanceName, description);
		
	}	
	
	@DELETE
	@Path("/instance/{instanceName}")
	public String deleteInstance(@PathParam("instanceName") String instanceName){

		return (MRHandler.deleteInstance(instanceName)) ?
			"Instance " + instanceName + " deleted.\n" : 
			"Instance " + instanceName + " not deleted. Does not exist.\n";

	}
}
