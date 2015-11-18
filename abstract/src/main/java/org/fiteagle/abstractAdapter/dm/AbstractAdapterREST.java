package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * subclasses must be annotated as
 * 
 * @Path("/") for this to work!
 */
public abstract class AbstractAdapterREST {

	private final Logger LOGGER = Logger.getLogger(AbstractAdapterREST.class
			.getName());

	/**
	 * Method to return underlying abstract model of the adapter
	 * 
	 * @param adapterName
	 * @return
	 */
	// e.g. http://localhost:8080/epc/epc/EpcAdapter-1
	@GET
	@Path("/{adapterName}")
	@Produces("text/turtle")
	public String getAdapterDescriptionTurtle(
			@PathParam("adapterName") String adapterName) {

		LOGGER.log(Level.INFO, "Try to run method getAdapterDescriptionTurtle("
				+ adapterName + ")");

		AbstractAdapter adapter = getAdapterInstance(adapterName);

		return MessageUtil.serializeModel(adapter.getAdapterDescriptionModel(),
				IMessageBus.SERIALIZATION_TURTLE);
	}

	/**
	 * Methods to return all of the resource instances controlled by this
	 * adapter, returns format as specified in HTTP request
	 * 
	 * @param adapterName
	 * @return
	 * @throws InstanceNotFoundException
	 */
	// e.g. curl -H "Accept: text/turtle" -X GET
	// "http://localhost:8080/epc/epc/EpcAdapter-1/instances"
	@GET
	@Path("/{adapterName}/instances")
	@Produces("text/turtle")
	public String getAllInstancesTurtle(
			@PathParam("adapterName") String adapterName)
			throws InstanceNotFoundException {

		AbstractAdapter adapter = getAdapterInstance(adapterName);

		try {

			Model instances = adapter.getAllInstances();
			String instancesString = null;
			if (instances == null) {
				LOGGER.log(Level.WARNING,
						"instances null in getAllInstancesTurtle method");
			} else {
				instancesString = MessageUtil.serializeModel(instances,
						IMessageBus.SERIALIZATION_TURTLE);
				LOGGER.log(Level.WARNING,
						"instances in getAllInstancesTurtle method: "
								+ instancesString);
			}

			return instancesString;

		} catch (InstanceNotFoundException e) {
			LOGGER.log(Level.SEVERE,
					"InstanceNotFoundException in getAllInstancesTurtle method");
			processInstanceNotFoundException(e);
		} catch (ProcessingException e) {
			LOGGER.log(Level.SEVERE,
					"ProcessingException in getAllInstancesTurtle method");
			processProcessingRequestException(e);
		}
		return null;
	}

	// e.g. curl -H "Accept: application/rdf+xml" -X GET
	// "http://localhost:8080/epc/epc/EpcAdapter-1/instances"
	@GET
	@Path("/{adapterName}/instances")
	@Produces("application/rdf+xml")
	public String getAllInstancesRDF(
			@PathParam("adapterName") String adapterName)
			throws InstanceNotFoundException {
		AbstractAdapter adapter = getAdapterInstance(adapterName);
		try {
			return MessageUtil.serializeModel(adapter.getAllInstances(),
					IMessageBus.SERIALIZATION_RDFXML);
		} catch (InstanceNotFoundException e) {
			processInstanceNotFoundException(e);
		} catch (ProcessingException e) {
			processProcessingRequestException(e);
		}
		return null;
	}

	// e.g. curl -H "Accept: application/n-triples" -X GET
	// "http://localhost:8080/epc/epc/EpcAdapter-1/instances"
	@GET
	@Path("/{adapterName}/instances")
	@Produces("application/n-triples")
	public String getAllInstancesNTRIPLE(
			@PathParam("adapterName") String adapterName)
			throws InstanceNotFoundException {
		AbstractAdapter adapter = getAdapterInstance(adapterName);
		try {
			return MessageUtil.serializeModel(adapter.getAllInstances(),
					IMessageBus.SERIALIZATION_NTRIPLE);
		} catch (InstanceNotFoundException e) {
			processInstanceNotFoundException(e);
		} catch (ProcessingException e) {
			processProcessingRequestException(e);
		}
		return null;
	}

	/**
	 * HTTP GET method to get a specified resource controlled by this adapter,
	 * according to its URI
	 * 
	 * @param adapterName
	 * @param instanceURI
	 * @return
	 */
	// e.g. http://localhost:8080/epc/epc/EpcAdapter-1/instances/
	// http%3A%2F%2Flocalhost%2Fresource%2FEpcAdapter-1%2Fe1d02989-3b61-45f5-932f-078ad1d0bbc6
	@GET
	@Path("/{adapterName}/instances/{instanceURI}")
	@Produces("text/turtle")
	public String getInstanceTurtle(
			@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {

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
		return MessageUtil.serializeModel(model,
				IMessageBus.SERIALIZATION_TURTLE);
	}

	@GET
	@Path("/{adapterName}/instances/{instanceURI}")
	@Produces("application/rdf+xml")
	public String getInstanceRDFXML(
			@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {

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
		return MessageUtil.serializeModel(model,
				IMessageBus.SERIALIZATION_RDFXML);
	}

	@GET
	@Path("/{adapterName}/instances/{instanceURI}")
	@Produces("application/n-triples")
	public String getInstanceNTRIPLE(
			@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {

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
		return MessageUtil.serializeModel(model,
				IMessageBus.SERIALIZATION_NTRIPLE);
	}

	/**
	 * Other methods
	 * 
	 * @param adapterName
	 * @param rdfInput
	 * @return
	 */
	@POST
	@Path("/{adapterName}/instances")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html")
	public String createInstances(@PathParam("adapterName") String adapterName,
			String rdfInput) {

		AbstractAdapter adapter = getAdapterInstance(adapterName);

		try {
			Model resultModel = adapter.createInstances(MessageUtil
					.parseSerializedModel(rdfInput,
							IMessageBus.SERIALIZATION_TURTLE));
			return MessageUtil.serializeModel(resultModel,
					IMessageBus.SERIALIZATION_TURTLE);
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
	public Response updateConfig(@PathParam("adapterName") String adapterName,
			String configInput) {

		AbstractAdapter adapter = getAdapterInstance(adapterName);
		try {
			adapter.updateConfig(adapterName, configInput);
			return Response.status(Response.Status.OK.getStatusCode()).build();
		} catch (ProcessingException e) {
			processProcessingRequestException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Response.Status.CONFLICT.getStatusCode())
				.build();
	}

	@PUT
	@Path("/{adapterName}/instances/{instanceURI}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html")
	public String configureInstance(
			@PathParam("adapterName") String adapterName, String rdfInput,
			@PathParam("instanceURI") String instanceURI) {
		AbstractAdapter adapter = getAdapterInstance(adapterName);
		try {
			Model resultModel = adapter.updateInstance(decode(instanceURI),
					MessageUtil.parseSerializedModel(rdfInput,
							IMessageBus.SERIALIZATION_TURTLE));
			return MessageUtil.serializeModel(resultModel,
					IMessageBus.SERIALIZATION_TURTLE);
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
	public Response deleteInstance(
			@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {

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

	// returns the underlying model for the adapter
	protected AbstractAdapter getAdapterInstance(String adapterName) {

		LOGGER.log(Level.INFO, "getAdapterInstance: " + adapterName);

		AbstractAdapter adapter = null;

		Collection<AbstractAdapter> adapterInstances = getAdapterInstances();

		for (AbstractAdapter abstractAdapter : adapterInstances) {
			String id = abstractAdapter.getId();
			String name = abstractAdapter.getAdapterABox().getLocalName();
			if (adapterName.equals(id) || adapterName.equals(name)) {
				adapter = abstractAdapter;
				break;
			}
		}

		if (adapter == null) {
			throw new AdapterWebApplicationException(Status.NOT_FOUND,
					"The adapter adapterABox " + adapterName
							+ " could not be found");
		}
		return adapter;
	}

	private void processInvalidRequestException(InvalidRequestException e) {
		if (e.getCause() != null) {
			throw new AdapterWebApplicationException(Status.BAD_REQUEST,
					e.getCause());
		}
		throw new AdapterWebApplicationException(Status.BAD_REQUEST,
				e.getMessage());
	}

	private void processProcessingRequestException(ProcessingException e) {
		if (e.getCause() != null) {
			throw new AdapterWebApplicationException(
					Status.INTERNAL_SERVER_ERROR, e.getCause());
		}
		throw new AdapterWebApplicationException(Status.INTERNAL_SERVER_ERROR,
				e.getMessage());
	}

	private void processInstanceNotFoundException(InstanceNotFoundException e) {
		if (e.getCause() != null) {
			throw new AdapterWebApplicationException(Status.NOT_FOUND,
					e.getCause());
		}
		throw new AdapterWebApplicationException(Status.NOT_FOUND,
				e.getMessage());
	}

	private String decode(String string) {
		try {
			return URLDecoder.decode(string, "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new AdapterWebApplicationException(Status.BAD_REQUEST, e);
		}
	}

	protected abstract Collection<AbstractAdapter> getAdapterInstances();

	public static class AdapterWebApplicationException extends
			WebApplicationException {
		private static final long serialVersionUID = -9105333519515224562L;

		public AdapterWebApplicationException(final Status status,
				final Throwable cause) {
			super(Response.status(status).entity(cause.getMessage())
					.type(MediaType.TEXT_PLAIN).build());
		}

		public AdapterWebApplicationException(final StatusType status,
				final Throwable cause) {
			super(Response.status(status).entity(cause.getMessage())
					.type(MediaType.TEXT_PLAIN).build());
		}

		public AdapterWebApplicationException(final Status status,
				final String message) {
			super(Response.status(status).entity(message).build());
		}
	}

}
