package org.fiteagle.abstractAdapter.dm;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.fiteagle.abstractAdapter.AbstractAdapter;

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

/**
 * subclasses must be annotated as
 * 
 * @Path("/") for this to work!
 */
public abstract class AbstractAdapterREST {
    private AbstractAdapter abstractAdapter;

    @PostConstruct
    public void setup() throws NamingException {
        // this.abstractAdapterEJB = (IAbstractAdapter) new InitialContext().lookup("java:module/AbstractAdapter");
        this.abstractAdapter = handleSetup();
    }

    /**
     * Subclasses must return the desired adapter singleton for use in this context
     */
    public abstract AbstractAdapter handleSetup();

    public static final String UPLOADED_FILE_PARAMETER_NAME = "file";

    // The language in which to write the model is specified by the lang argument. Predefined values are
    // "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3". The default value, represented by null, is "RDF/XML".

    // TODO!!!!!
    // replace form upload

    @GET
    @Path("description.ttl")
    @Produces("text/turtle")
    public String getDescriptionTurtle() {
        return abstractAdapter.getAdapterDescription(AbstractAdapter.PARAM_TURTLE);
    }

    @GET
    @Path("description.rdf")
    @Produces("application/rdf+xml")
    public String getDescriptionRDF() {
        return abstractAdapter.getAdapterDescription(AbstractAdapter.PARAM_RDFXML);
    }
    
    /**
     * Only for JavaScript Weblclient
     * 
     * @return
     */
    @GET
    @Path("description.rdf-text")
    @Produces("text/html")
    public String getDescriptionRDFAsText() {
        return abstractAdapter.getAdapterDescription(AbstractAdapter.PARAM_RDFXML);
    }

    @GET
    @Path("description.ntriple")
    @Produces("application/n-triples")
    public String getDescriptionNTRIPLE() {
        return abstractAdapter.getAdapterDescription(AbstractAdapter.PARAM_NTRIPLE);
    }

    @GET
    @Path("instances.ttl")
    @Produces("text/turtle")
    public String getAllInstancesTurtle() {
        return abstractAdapter.getAllInstances(AbstractAdapter.PARAM_TURTLE);
    }

    @GET
    @Path("instances.rdf")
    @Produces("application/rdf+xml")
    public String getAllInstancesRDF() {
        return abstractAdapter.getAllInstances(AbstractAdapter.PARAM_RDFXML);
    }
    
    @GET
    @Path("instances.rdf-text")
    @Produces("text/html")
    public String getAllInstancesRDFAsText() {
        return abstractAdapter.getAllInstances(AbstractAdapter.PARAM_RDFXML);
    }

    @GET
    @Path("instances.ntriple")
    @Produces("application/n-triples")
    public String getAllInstancesNTRIPLE() {
        return abstractAdapter.getAllInstances(AbstractAdapter.PARAM_NTRIPLE);
    }

    @POST
    @Path("instance/{instanceName}")
    @Produces("text/html")
    public String createInstance(@PathParam("instanceName") String instanceName) {
        if (abstractAdapter.createInstance(instanceName)) {
            return "Created instance number : " + instanceName;
        }
        return "Invalid instance number";
    }

    @DELETE
    @Path("instance/{instanceName}")
    @Produces("text/html")
    public String terminateInstance(@PathParam("instanceName") String instanceName) {
        if (abstractAdapter.terminateInstance(instanceName)) {
            return "Terminated instance number : " + instanceName;
        }
        return "Invalid instance number";
    }

    @GET
    @Path("instance/{instanceName}/description.ttl")
    @Produces("text/turtle")
    public String monitorInstanceTurtle(@PathParam("instanceName") String instanceName) {
        return abstractAdapter.monitorInstance(instanceName, AbstractAdapter.PARAM_TURTLE);
    }

    @GET
    @Path("instance/{instanceName}/description.rdf")
    @Produces("application/rdf+xml")
    public String monitorInstanceRDF(@PathParam("instanceName") String instanceName) {
        return abstractAdapter.monitorInstance(instanceName, AbstractAdapter.PARAM_RDFXML);
    }
    
    @GET
    @Path("instance/{instanceName}/description.rdf-text")
    @Produces("text/html")
    public String monitorInstanceRDFAsText(@PathParam("instanceName") String instanceName) {
        return abstractAdapter.monitorInstance(instanceName, AbstractAdapter.PARAM_RDFXML);
    }

    @GET
    @Path("instance/{instanceName}/description.ntriple")
    @Produces("application/n-triples")
    public String monitorInstanceNTRIPLE(@PathParam("instanceName") String instanceName) {
        return abstractAdapter.monitorInstance(instanceName, AbstractAdapter.PARAM_NTRIPLE);
    }
    
    // TODO: Parse string and send rdf Model to RDF Handler class!
    
//    @PUT
//    @Path("instance/{instanceName}/description.ttl")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    @Produces("text/html")
//    public String controlInstanceTurtle(final String controlInput) {
//        return abstractAdapter.configureInstance(controlInput, AbstractAdapter.PARAM_TURTLE, "");
//    }
//
//
//    @PUT
//    @Path("instance/{instanceName}/description.rdf")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    @Produces("text/html")
//    public String controlInstanceRDF(final String controlInput) {
//        return abstractAdapter.configureInstance(controlInput, AbstractAdapter.PARAM_RDFXML, "");
//    }
//
//    @PUT
//    @Path("instance/{instanceName}/description.ntriple")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    @Produces("text/html")
//    public String controlInstanceNTRIPLE(final String controlInput) {
//        return abstractAdapter.configureInstance(controlInput, AbstractAdapter.PARAM_NTRIPLE, "");
//    }
 
    
    // TODO: Optional: Re-implement using RDF Handler class
//    /**
//     * Control adapter's instance property directly via REST
//     * Good for testing
//     * 
//     * @param instanceName
//     * @param paramProperty
//     * @param paramValue
//     * @return
//     */
//    @PUT
//    @Path("instance/{instanceName}/{paramProperty}/{paramValue}")
//    @Produces("text/html")
//    public String controlInstanceProperty(@PathParam("instanceName") String instanceName,  @PathParam("paramProperty") String paramProperty,  @PathParam("paramValue") String paramValue) {
//        String controlString = "@prefix :      <http://fiteagle.org/ontology/adapter/motor#> .\n";
//        controlString += "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n";
//        controlString += "@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n";
//        controlString += ":m" + instanceName + "     a             :MotorResource ;\n";
//        controlString += "rdfs:label    \"" + instanceName + "\" ;\n";
//        controlString += ":" + paramProperty + "          \"" + paramValue + "\"^^xsd:long .\n";
//        return abstractAdapter.configureInstance(controlString, AbstractAdapter.PARAM_TURTLE, "");
//    }
}
