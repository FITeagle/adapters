package org.fiteagle.adapters.motor;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;




//Label As a Developer, I want to play around with a Motor adapter, so that I know how to develop my own adapter.
//Description 
//See mytestbed.net/doc/omf/file.DEVELOPERS.html
//
//The adapter should:
//* Be located at github.com/fiteagle/adapters
//* Has the namespace: org.fiteagle.adapters.ADAPTERNAME.*
//* Has the delivery mechanism namespace: org.fiteagle.adapters.ADAPTERNAME.*.dm
//* Be deployable with: "cd adapters/ADAPTERNAME; mvn wildfly:deploy;"
//* Should provide a REST interface
// * Describe: HTTP GET localhost:8080/ADAPTERNAME/description.ttl
// * Provision: HTTP GET localhost:8080/ADAPTERNAME/instances.ttl
// * Provision: HTTP POST localhost:8080/ADAPTERNAME/instance/1
// * Monitor: HTTP GET localhost:8080/ADAPTERNAME/instance/1/descripti...
// * Control: HTTP PUT localhost:8080/ADAPTERNAME/instance/1/descripti...
// * Terminate: HTTP DELETE localhost:8080/ADAPTERNAME/instance/1
//Themes  
//Qualities .
//Test  cd adapters/ADAPTERNAME
//mvn test
//mvn wildfly:deploy;
//mvn site && open target/site/index.html
//curl localhost:8080/ADAPTERNAME/description.ttl



//Motor Properties
//- Name
//- Location
//- Type (Electrical, Gasoline)
//- Manufacturer
//- Status (ON/OFF)
//- Current Rotational Speed (RPM)
//- Maximum Rotational Speed
//
//Motor Control
//- Turn on
//- Set Rotational Speed
//- Turn off



@Path("/")
public class Motor {


	@GET
	@Path("description.ttl")
	@Produces("text/turtle")
	public String getDescription() {
	  

		return "# this is a complete turtle document\n@prefix foo: <http://example.org/ns#> .\n@prefix : <http://other.example.org/ns#> .\nfoo:bar foo: :\n .:bar : foo:bar .";
	}
	
	 @GET
	  @Path("instances.ttl")
	  @Produces("text/turtle")
	  public String getAllInstances() {	    

	    return "# this is a complete turtle document\n@prefix foo: <http://example.org/ns#> .\n@prefix : <http://other.example.org/ns#> .\nfoo:bar foo: :\n .:bar : foo:bar .";
	  }

	 
	 
	  @GET
	  @Path("instance/{instanceNumber}")
	  @Produces("text/html")
	  public String getSingleInstance(@PathParam("instanceNumber") int instanceNumber) {
	    return "Instance number : " + instanceNumber;
	  }
	  
	  
    @GET
    @Path("instance/{instanceNumber}/description.ttl")
    @Produces("text/html")
    public String getSingleInstanceDescription(@PathParam("instanceNumber") int instanceNumber) {
      return "Description - Instance number : " + instanceNumber;
    }
    
    @PUT
    @Path("instance/{instanceNumber}/description.ttl")
    @Produces("text/html")
    public String putSingleInstanceDescription(@PathParam("instanceNumber") int instanceNumber) {
      return "Put - Instance number : " + instanceNumber;
    }

    
    @DELETE
    @Path("instance/{instanceNumber}")
    @Produces("text/html")
    public String terminateSingleInstance(@PathParam("instanceNumber") int instanceNumber) {
      return "Terminated - Instance number : " + instanceNumber;
    }

}
