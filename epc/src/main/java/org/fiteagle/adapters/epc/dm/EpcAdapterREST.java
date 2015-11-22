package org.fiteagle.adapters.epc.dm;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST.AdapterWebApplicationException;
import org.fiteagle.adapters.epc.EvolvedPacketCore;
import org.fiteagle.adapters.epc.EpcAdapter;
import org.fiteagle.adapters.epc.EpcAdapterControl;
import org.fiteagle.adapters.epc.EpcGeneric;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;

@Path("/")
public class EpcAdapterREST extends AbstractAdapterREST {

	private static final Logger LOGGER = Logger.getLogger(EpcAdapterREST.class
			.toString());

	@EJB
	private transient EpcAdapterControl controller;

	@Override
	protected Collection<AbstractAdapter> getAdapterInstances() {
		return this.controller.getAdapterInstances();
	}

	/**
	 * This method is used to update the rate, delay or loss of an EpcGeneric
	 * object
	 * 
	 * @param type
	 *            : rate delay or loss
	 * @param adapterName
	 *            : e.g. EpcAdapter-1
	 * @param instanceURI
	 *            : e.g. http://localhost/resource/EpcAdapter-1/new1
	 * @param code
	 *            : the value to which rate, delay or loss should be updated to
	 * @return: returns the updated model for the EpcGeneric resource in
	 *          question
	 * @throws InstanceNotFoundException
	 */
	// http://localhost:8080/epc/rate/EpcAdapter-1/http%3A%2F%2Flocalhost%2Fresource%2FEpcAdapter-1%2Fnew2/2
	@GET
	@Path("{type}/{adapterName}/{instanceURI}/{code}")
	@Produces("text/turtle")
	public String rate(@PathParam("type") String type,
			@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI,
			@PathParam("code") String code) throws InstanceNotFoundException {

		LOGGER.log(Level.INFO, "Update rate for " + instanceURI
				+ ", in adapter " + adapterName + " for type " + type);

		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);
		EpcGeneric epcGeneric = adapter.getInstanceObject(instanceURI);
		int codeInt = Integer.parseInt(code);
		if (type.equals("rate")) {
			epcGeneric.setRateCode(codeInt);
		} else if (type.equals("delay")) {
			epcGeneric.setDelayCode(codeInt);
		} else if (type.equals("loss")) {
			epcGeneric.setPacketlossCode(codeInt);
		}

		Model instances = adapter.getInstance(instanceURI);
		String instancesString = null;
		instancesString = MessageUtil.serializeModel(instances,
				IMessageBus.SERIALIZATION_TURTLE);

		return instancesString;
	}

	// http://localhost:8080/epc/get
	@GET
	@Path("get")
	public String get() {
		return "get";
	}

	/**
	 * This method sends the current values for rate, delay and loss as
	 * parameters to start the script net-fuseco.sh
	 * 
	 * e.g.
	 * http://localhost:8080/epc/start/EpcAdapter-1/http%3A%2F%2Flocalhost%2F
	 * resource%2FEpcAdapter-1%2Fnew2
	 */

	@GET
	@Path("start/{adapterName}/{instanceURI}")
	public String start(@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {

		LOGGER.log(Level.INFO, "Send start script for " + instanceURI
				+ ", in adapter " + adapterName);

		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);
		EpcGeneric epcGeneric = adapter.getInstanceObject(instanceURI);
		// epcGeneric.updateRateDelayPacktloss();

		return "start";
	}

	/**
	 * This method sends stop as a parameter to the script net-fuseco.sh
	 * 
	 * @param adapterName
	 * @param instanceURI
	 * @return
	 */
	@GET
	@Path("stop/{adapterName}/{instanceURI}")
	public String stop(@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {
		LOGGER.log(Level.INFO, "Send stop script for " + instanceURI
				+ ", in adapter " + adapterName);

		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);
		EpcGeneric epcGeneric = adapter.getInstanceObject(instanceURI);
		// epcGeneric.stopInstance();

		return "stop";
	}

	@GET
	@Path("restart")
	public String restart() {
		return "restart";
	}
}
