package org.fiteagle.abstractAdapter.dm;

import java.util.Map;

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
  
  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
  
  private final static String adapterInstancesPrefix = "http://federation.av.tu-berlin.de/about#";
  
  @GET
  @Path("/{adapterName}/instance")
  @Produces("text/turtle")
  public String getAllInstancesTurtle(@PathParam("adapterName") String adapterName) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    return MessageUtil.serializeModel(adapter.getAllInstancesModel(), IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("/{adapterName}/instance")
  @Produces("application/rdf+xml")
  public String getAllInstancesRDF(@PathParam("adapterName") String adapterName) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    return MessageUtil.serializeModel(adapter.getAllInstancesModel(), IMessageBus.SERIALIZATION_RDFXML);
  }
  
  @GET
  @Path("/{adapterName}/instance")
  @Produces("application/n-triples")
  public String getAllInstancesNTRIPLE(@PathParam("adapterName") String adapterName) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    return MessageUtil.serializeModel(adapter.getAllInstancesModel(), IMessageBus.SERIALIZATION_NTRIPLE);
  }
  
  @PUT
  @Path("/{adapterName}/instance")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String createInstances(@PathParam("adapterName") String adapterName, String rdfInput) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    try {
      Model resultModel = adapter.createInstances(MessageUtil.parseSerializedModel(rdfInput, IMessageBus.SERIALIZATION_TURTLE));
      return MessageUtil.serializeModel(resultModel, IMessageBus.SERIALIZATION_TURTLE);
    } catch (AdapterException e) {
      return e.getMessage();
    }
  }
  
  @POST
  @Path("/{adapterName}/instance")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String configureInstances(@PathParam("adapterName") String adapterName, String rdfInput) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    try {
      Model resultModel = adapter.configureInstances(MessageUtil.parseSerializedModel(rdfInput, IMessageBus.SERIALIZATION_TURTLE));
      return MessageUtil.serializeModel(resultModel, IMessageBus.SERIALIZATION_TURTLE);
    } catch (AdapterException e) {
      return e.getMessage();
    }
  }
    
  @DELETE
  @Path("/{adapterName}/instance/{instanceURI}")
  @Produces("text/html")
  public Response terminateInstance(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    adapter.deleteInstance(instanceURI);
    return Response.status(Response.Status.OK.getStatusCode()).build();
  }
  
  @GET
  @Path("/{adapterName}/instance/{instanceURI}")
  @Produces("text/turtle")
  public String monitorInstanceTurtle(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    Model model = adapter.getInstanceModel(instanceURI);
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("/{adapterName}/instance/{instanceURI}")
  @Produces("application/rdf+xml")
  public String monitorInstanceRDF(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    Model model = adapter.getInstanceModel(instanceURI);
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_RDFXML);
  }
  
  @GET
  @Path("/{adapterName}/instance/{instanceURI}")
  @Produces("application/n-triples")
  public String monitorInstanceNTRIPLE(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstances().get(adapterInstancesPrefix+adapterName);
    Model model = adapter.getInstanceModel(instanceURI);
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_NTRIPLE);
  }
}
