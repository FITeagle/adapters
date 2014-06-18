package org.fiteagle.adapters.motor.dm;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.fiteagle.adapters.motor.IMotorAdapter;

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
public class MotorAdapterREST {

	
	private static final String EJB_NAME = "java:module/MotorAdapterEJB";
	private IMotorAdapter motorLogic;
	
    public MotorAdapterREST() throws NamingException {
		super();
		this.motorLogic = (IMotorAdapter) new InitialContext().lookup(MotorAdapterREST.EJB_NAME);
	}

	public static final String UPLOADED_FILE_PARAMETER_NAME = "file";

    @GET
    @Path("description.ttl")
    @Produces("text/html")
    public String getDescription() {
        return motorLogic.getAdapterDescription("TURTLE");
    }

//    @GET
//    @Path("instances.ttl")
//    @Produces("text/html")
//    public String getAllInstances() {
//        return motorLogic.getAllMotorInstances("TURTLE");
//    }
//
//    @POST
//    @Path("instance/{instanceNumber}")
//    @Produces("text/html")
//    public String createInstance(@PathParam("instanceNumber") int instanceNumber) {
//        if (motorLogic.createMotorInstance(instanceNumber)) {
//            return "Created instance number : " + instanceNumber;
//        }
//        return "Invalid instance number";
//    }
//
//    @DELETE
//    @Path("instance/{instanceNumber}")
//    @Produces("text/html")
//    public String terminateInstance(@PathParam("instanceNumber") int instanceNumber) {
//        if (motorLogic.terminateMotorInstance(instanceNumber)) {
//            return "Terminated instance number : " + instanceNumber;
//        }
//        return "Invalid instance number";
//    }
//
//    @GET
//    @Path("instance/{instanceNumber}/description.ttl")
//    @Produces("text/html")
//    public String monitorInstance(@PathParam("instanceNumber") int instanceNumber) {
//        return motorLogic.monitorMotorInstance(instanceNumber, "TURTLE");
//    }
//
//    @PUT
//    @Path("instance/{instanceNumber}/description.ttl")
//    @Consumes("multipart/form-data")
//    public String uploadFile(MultipartFormDataInput input) {
//
//        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
//        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);
//
//        for (InputPart inputPart : inputParts) {
//            // MultivaluedMap<String, String> headers = inputPart.getHeaders();
//            // String filename = getFileName(headers);
//
//            try {
//                InputStream inputStream = inputPart.getBody(InputStream.class, null);
//
//                return motorLogic.controlMotorInstance(inputStream, "TURTLE");
//
//            } catch (IOException e) {
//                return "IO Exception";
//            }
//        }
//        return "Failure";
//    }
}
