package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @Path("/") for this to work!
 */
public abstract class AbstractAdapterREST {
  
  @GET
  @Path("/{adapterName}")
  @Produces("text/turtle")
  public String getAdapterDescriptionTurtle(@PathParam("adapterName") String adapterName) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    return MessageUtil.serializeModel(adapter.getAdapterDescriptionModel(), IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("/{adapterName}/instances")
  @Produces("text/turtle")
  public String getAllInstancesTurtle(@PathParam("adapterName") String adapterName) throws InstanceNotFoundException {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
      return MessageUtil.serializeModel(adapter.getAllInstances(), IMessageBus.SERIALIZATION_TURTLE);
    } catch (InstanceNotFoundException e) {
      processInstanceNotFoundException(e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    }
    return null;
  }
  
  @GET
  @Path("/{adapterName}/instances")
  @Produces("application/rdf+xml")
  public String getAllInstancesRDF(@PathParam("adapterName") String adapterName) throws InstanceNotFoundException {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
      return MessageUtil.serializeModel(adapter.getAllInstances(), IMessageBus.SERIALIZATION_RDFXML);
    } catch (InstanceNotFoundException e) {
      processInstanceNotFoundException(e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    }
    return null;
  }
  
  @GET
  @Path("/{adapterName}/instances")
  @Produces("application/n-triples")
  public String getAllInstancesNTRIPLE(@PathParam("adapterName") String adapterName) throws InstanceNotFoundException {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
      return MessageUtil.serializeModel(adapter.getAllInstances(), IMessageBus.SERIALIZATION_NTRIPLE);
    } catch (InstanceNotFoundException e) {
      processInstanceNotFoundException(e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    }
    return null;
  }
  
  @POST
  @Path("/{adapterName}/instances")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String createInstances(@PathParam("adapterName") String adapterName, String rdfInput) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
      Model resultModel = adapter.createInstances(MessageUtil.parseSerializedModel(rdfInput, IMessageBus.SERIALIZATION_TURTLE));
      return MessageUtil.serializeModel(resultModel, IMessageBus.SERIALIZATION_TURTLE);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    } catch (InvalidRequestException e) {
      processInvalidRequestException(e);
    }
    return null;
  }
  
  @POST
  @Path("/{adapterName}/config")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("text/html")
  public Response updateConfig(@PathParam("adapterName") String adapterName, String configInput) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
    	adapter.updateConfig(adapterName,configInput);
    	return Response.status(Response.Status.OK.getStatusCode()).build();
    } catch (ProcessingException e) {
        processProcessingRequestException(e);
      } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return Response.status(Response.Status.CONFLICT.getStatusCode()).build();
  }

  @PUT
  @Path("/{adapterName}/instances/{instanceURI}")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String configureInstance(@PathParam("adapterName") String adapterName, String rdfInput, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
      Model resultModel = adapter.updateInstance(decode(instanceURI), MessageUtil.parseSerializedModel(rdfInput, IMessageBus.SERIALIZATION_TURTLE));
      return MessageUtil.serializeModel(resultModel, IMessageBus.SERIALIZATION_TURTLE);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    } catch (InvalidRequestException e) {
      processInvalidRequestException(e);
    }
    return null;
  }
    
  @DELETE
  @Path("/{adapterName}/instances/{instanceURI}")
  @Produces("text/html")
  public Response deleteInstance(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    try {
      adapter.deleteInstance(decode(instanceURI));
    } catch (InstanceNotFoundException e) {
      throw new AdapterWebApplicationException(Status.NOT_FOUND, e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    } catch (InvalidRequestException e) {
      processInvalidRequestException(e);
    }
    return Response.status(Response.Status.OK.getStatusCode()).build();
  }
  
  @GET
  @Path("/{adapterName}/instances/{instanceURI}")
  @Produces("text/turtle")
  public String getInstanceTurtle(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    Model model = null;
    try {
      model = adapter.getInstance(decode(instanceURI));
    } catch (InstanceNotFoundException e) {
      throw new AdapterWebApplicationException(Status.NOT_FOUND, e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    } catch (InvalidRequestException e) {
      processInvalidRequestException(e);
    }
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_TURTLE);
  }
  
  @GET
  @Path("/{adapterName}/instances/{instanceURI}")
  @Produces("application/rdf+xml")
  public String getInstanceRDFXML(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    Model model = null;
    try {
      model = adapter.getInstance(decode(instanceURI));
    } catch (InstanceNotFoundException e) {
      throw new AdapterWebApplicationException(Status.NOT_FOUND, e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    } catch (InvalidRequestException e) {
      processInvalidRequestException(e);
    }
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_RDFXML);
  }
  
  @GET
  @Path("/{adapterName}/instances/{instanceURI}")
  @Produces("application/n-triples")
  public String getInstanceNTRIPLE(@PathParam("adapterName") String adapterName, @PathParam("instanceURI") String instanceURI) {
    AbstractAdapter adapter = getAdapterInstance(adapterName);
    Model model = null;
    try {
      model = adapter.getInstance(decode(instanceURI));
    } catch (InstanceNotFoundException e) {
      processInstanceNotFoundException(e);
    } catch (ProcessingException e) {
      processProcessingRequestException(e);
    } catch (InvalidRequestException e) {
      processInvalidRequestException(e);
    }
    return MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_NTRIPLE);
  }

  private AbstractAdapter getAdapterInstance(String adapterName) {
    AbstractAdapter adapter = getAdapterInstances().get(OntologyModelUtil.getResourceNamespace()+adapterName);
    if(adapter == null){
      throw new AdapterWebApplicationException(Status.NOT_FOUND, "The adapter adapterABox "+adapterName+" could not be found");
    }
    return adapter;
  }
  
  private void processInvalidRequestException(InvalidRequestException e) {
    if(e.getCause() != null){
      throw new AdapterWebApplicationException(Status.BAD_REQUEST, e.getCause());
    }
    throw new AdapterWebApplicationException(Status.BAD_REQUEST, e.getMessage());
  }
  
  private void processProcessingRequestException(ProcessingException e) {
    if(e.getCause() != null){
      throw new AdapterWebApplicationException(Status.INTERNAL_SERVER_ERROR, e.getCause());
    }
    throw new AdapterWebApplicationException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
  }
  
  private void processInstanceNotFoundException(InstanceNotFoundException e){
    if(e.getCause() != null){
      throw new AdapterWebApplicationException(Status.NOT_FOUND, e.getCause());
    }
    throw new AdapterWebApplicationException(Status.NOT_FOUND, e.getMessage());
  }
  
  private String decode(String string){
    try {
      return URLDecoder.decode(string, "UTF8");
    } catch (UnsupportedEncodingException e) {
      throw new AdapterWebApplicationException(Status.BAD_REQUEST, e);
    }
  }

  protected abstract Map<String, AbstractAdapter> getAdapterInstances();
  
  public static class AdapterWebApplicationException extends WebApplicationException {
    private static final long serialVersionUID = -9105333519515224562L;
    
    public AdapterWebApplicationException(final Status status, final Throwable cause) {
      super(Response.status(status).entity(cause.getMessage()).type(MediaType.TEXT_PLAIN).build());
    }
    
    public AdapterWebApplicationException(final StatusType status, final Throwable cause) {
      super(Response.status(status).entity(cause.getMessage()).type(MediaType.TEXT_PLAIN).build());
    }
    
    public AdapterWebApplicationException(final Status status, final String message) {
      super(Response.status(status).entity(message).build());
    }
  }

}
