package org.fiteagle.adapters.motor.dm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.fiteagle.adapters.motor.IMotorAdapter;
import org.fiteagle.adapters.motor.MotorAdapter;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

//Label 
//As a Developer, I want to play around with a Motor adapter, so that I know how to develop my own adapter.
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

@Path("/")
//@DependsOn("MotorAdapter")
public class MotorAdapterREST {
    private IMotorAdapter motorAdapterEJB;

    @PostConstruct
    public void setup() throws NamingException {
       // this.motorAdapterEJB = (IMotorAdapter) new InitialContext().lookup("java:module/MotorAdapter");
        this.motorAdapterEJB = MotorAdapter.getInstance();
    }

    public static final String UPLOADED_FILE_PARAMETER_NAME = "file";

    // The language in which to write the model is specified by the lang argument. Predefined values are
    // "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3". The default value, represented by null, is "RDF/XML".

    // TODO!!!!!
    // replace form upload
    // 2x @path no ending (ttl, rdf)

    @GET
    @Path("description.ttl")
    @Produces("text/turtle")
    public String getDescriptionTurtle() {
        return motorAdapterEJB.getAdapterDescription(MotorAdapter.PARAM_TURTLE);
    }

    @GET
    @Path("description.rdf")
    @Produces("application/rdf+xml")
    public String getDescriptionRDF() {
        return motorAdapterEJB.getAdapterDescription(MotorAdapter.PARAM_RDFXML);
    }

    @GET
    @Path("description.ntriple")
    @Produces("application/n-triples")
    public String getDescriptionNTRIPLE() {
        return motorAdapterEJB.getAdapterDescription(MotorAdapter.PARAM_NTRIPLE);
    }

    @GET
    @Path("instances.ttl")
    @Produces("text/turtle")
    public String getAllInstancesTurtle() {
        return motorAdapterEJB.getAllInstances(MotorAdapter.PARAM_TURTLE);
    }

    @GET
    @Path("instances.rdf")
    @Produces("application/rdf+xml")
    public String getAllInstancesRDF() {
        return motorAdapterEJB.getAllInstances(MotorAdapter.PARAM_RDFXML);
    }

    @GET
    @Path("instances.ntriple")
    @Produces("application/n-triples")
    public String getAllInstancesNTRIPLE() {
        return motorAdapterEJB.getAllInstances(MotorAdapter.PARAM_NTRIPLE);
    }

    @POST
    @Path("instance/{instanceNumber}")
    @Produces("text/html")
    public String createInstance(@PathParam("instanceNumber") int instanceNumber) {
        if (motorAdapterEJB.createInstance(instanceNumber)) {
            return "Created instance number : " + instanceNumber;
        }
        return "Invalid instance number";
    }

    @DELETE
    @Path("instance/{instanceNumber}")
    @Produces("text/html")
    public String terminateInstance(@PathParam("instanceNumber") int instanceNumber) {
        if (motorAdapterEJB.terminateInstance(instanceNumber)) {
            return "Terminated instance number : " + instanceNumber;
        }
        return "Invalid instance number";
    }

    @GET
    @Path("instance/{instanceNumber}/description.ttl")
    @Produces("text/turtle")
    public String monitorInstanceTurtle(@PathParam("instanceNumber") int instanceNumber) {
        return motorAdapterEJB.monitorInstance(instanceNumber, MotorAdapter.PARAM_TURTLE);
    }

    @GET
    @Path("instance/{instanceNumber}/description.rdf")
    @Produces("application/rdf+xml")
    public String monitorInstanceRDF(@PathParam("instanceNumber") int instanceNumber) {
        return motorAdapterEJB.monitorInstance(instanceNumber, MotorAdapter.PARAM_RDFXML);
    }

    @GET
    @Path("instance/{instanceNumber}/description.ntriple")
    @Produces("application/n-triples")
    public String monitorInstanceNTRIPLE(@PathParam("instanceNumber") int instanceNumber) {
        return motorAdapterEJB.monitorInstance(instanceNumber, MotorAdapter.PARAM_NTRIPLE);
    }

    @PUT
    @Path("instance/{instanceNumber}/description.ttl")
    @Consumes("multipart/form-data")
    @Produces("text/html")
    public String controlInstanceTurtle(MultipartFormDataInput input) {

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);

        for (InputPart inputPart : inputParts) {
            // MultivaluedMap<String, String> headers = inputPart.getHeaders();
            // String filename = getFileName(headers);

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                return motorAdapterEJB.controlInstance(inputStream, MotorAdapter.PARAM_TURTLE);

            } catch (IOException e) {
                return "IO Exception";
            }
        }
        return "Failure";
    }

    @PUT
    @Path("instance/{instanceNumber}/description.rdf")
    @Consumes("multipart/form-data")
    @Produces("text/html")
    public String controlInstanceRDF(MultipartFormDataInput input) {

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);

        for (InputPart inputPart : inputParts) {
            // MultivaluedMap<String, String> headers = inputPart.getHeaders();
            // String filename = getFileName(headers);

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                return motorAdapterEJB.controlInstance(inputStream, MotorAdapter.PARAM_RDFXML);

            } catch (IOException e) {
                return "IO Exception";
            }
        }
        return "Failure";
    }

    @PUT
    @Path("instance/{instanceNumber}/description.ntriple")
    @Consumes("multipart/form-data")
    @Produces("text/html")
    public String controlInstanceNTRIPLE(MultipartFormDataInput input) {

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);

        for (InputPart inputPart : inputParts) {
            // MultivaluedMap<String, String> headers = inputPart.getHeaders();
            // String filename = getFileName(headers);

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                return motorAdapterEJB.controlInstance(inputStream, MotorAdapter.PARAM_NTRIPLE);

            } catch (IOException e) {
                return "IO Exception";
            }
        }
        return "Failure";
    }
}
