package org.fiteagle.abstractAdapter.dm;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.AdapterException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @Path("/") for this to work!
 */
public abstract class AbstractAdapterREST {
  
  protected abstract AbstractAdapter getAdapter();
  
  @GET
  @Path("")
  @Produces("text/turtle")
  public String getDescriptionTurtle() {
    return MessageUtil.serializeModel(getAdapter().getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("")
  @Produces("application/rdf+xml")
  public String getDescriptionRDF() {
    return MessageUtil.serializeModel(getAdapter().getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_RDFJSON);
  }
  
  @GET
  @Path("")
  @Produces("application/n-triples")
  public String getDescriptionNTRIPLE() {
    return MessageUtil.serializeModel(getAdapter().getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_NTRIPLE);
  }
  
  @GET
  @Path("instance")
  @Produces("text/turtle")
  public String getAllInstancesTurtle() {
    return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("instance")
  @Produces("application/rdf+xml")
  public String getAllInstancesRDF() {
    return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), IMessageBus.SERIALIZATION_RDFXML);
  }
  
  @GET
  @Path("instance")
  @Produces("application/n-triples")
  public String getAllInstancesNTRIPLE() {
    return MessageUtil.serializeModel(getAdapter().getAllInstancesModel(), IMessageBus.SERIALIZATION_NTRIPLE);
  }
  
  @PUT
  @Path("instance")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String createInstances(String rdfInput) {
    try {
      Model resultModel = getAdapter().createInstances(MessageUtil.parseSerializedModel(rdfInput, IMessageBus.SERIALIZATION_TURTLE), null);
      return MessageUtil.serializeModel(resultModel, IMessageBus.SERIALIZATION_TURTLE);
    } catch (AdapterException e) {
      return e.getMessage();
    }
  }
  
  @POST
  @Path("instance")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String configureInstances(String rdfInput) {
    try {
      Model resultModel = getAdapter().configureInstances(MessageUtil.parseSerializedModel(rdfInput, IMessageBus.SERIALIZATION_TURTLE), null);
      return MessageUtil.serializeModel(resultModel, IMessageBus.SERIALIZATION_TURTLE);
    } catch (AdapterException e) {
      return e.getMessage();
    }
  }
    
  @DELETE
  @Path("instance/{instanceName}")
  @Produces("text/html")
  public Response terminateInstance(@PathParam("instanceName") String instanceName) {
    getAdapter().deleteInstance(instanceName);
    return Response.status(Response.Status.OK.getStatusCode()).build();
  }
  
  @GET
  @Path("instance/{instanceName}")
  @Produces("text/turtle")
  public String monitorInstanceTurtle(@PathParam("instanceName") String instanceName) {
    Model model = getAdapter().getInstanceModel(instanceName);
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("instance/{instanceName}")
  @Produces("application/rdf+xml")
  public String monitorInstanceRDF(@PathParam("instanceName") String instanceName) {
    Model model = getAdapter().getInstanceModel(instanceName);
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_RDFXML);
  }
  
  @GET
  @Path("instance/{instanceName}")
  @Produces("application/n-triples")
  public String monitorInstanceNTRIPLE(@PathParam("instanceName") String instanceName) {
    Model model = getAdapter().getInstanceModel(instanceName);
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_NTRIPLE);
  }
}
