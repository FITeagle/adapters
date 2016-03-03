package org.fiteagle.adapters.epc.dm;

import info.openmultinet.ontology.vocabulary.Epc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST.AdapterWebApplicationException;
import org.fiteagle.adapters.epc.EpcAdapter;
import org.fiteagle.adapters.epc.EpcAdapterControl;
import org.fiteagle.adapters.epc.EpcGeneric;
import org.fiteagle.adapters.epc.model.EvolvedPacketCore;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

@Path("/")
public class EpcAdapterREST extends AbstractAdapterREST {

	private static final Logger LOGGER = Logger.getLogger(EpcAdapterREST.class
			.toString());
	final static String adapterName = "EpcAdapter-1";

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
		int codeInt = Integer.parseInt(code);

		if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
			EvolvedPacketCore epcGeneric = (EvolvedPacketCore) adapter
					.getInstanceObject(instanceURI);
			if (type.equals("rateUp")) {
				epcGeneric.getPdnGateway().setRateCodeUp(codeInt);
			} else if (type.equals("delay")) {
				epcGeneric.getPdnGateway().setDelayCode(codeInt);
			} else if (type.equals("loss")) {
				epcGeneric.getPdnGateway().setPacketlossCode(codeInt);
			}else if (type.equals("rateDown")) {
				epcGeneric.getPdnGateway().setRateCodeDown(codeInt);
			}
		}

		Model instances = adapter.getInstance(instanceURI);
		String instancesString = null;
		instancesString = MessageUtil.serializeModel(instances,
				IMessageBus.SERIALIZATION_TURTLE);

		return instancesString;
	}

	/**
	 * This method sends the current values for rate, delay and loss as
	 * parameters to start the script net-fuseco.sh
	 * 
	 * e.g.
	 * http://localhost:8080/epc/update/EpcAdapter-1/http%3A%2F%2Flocalhost%2F
	 * resource%2FEpcAdapter-1%2Fnew2
	 */

	@GET
	@Path("update/{adapterName}/{instanceURI}")
	public String update(@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {

		LOGGER.log(Level.INFO, "Send update script for " + instanceURI
				+ ", in adapter " + adapterName);

		String output = "update";

		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);

		if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
			EvolvedPacketCore epcGeneric = (EvolvedPacketCore) adapter
					.getInstanceObject(instanceURI);
			output = epcGeneric.getPdnGateway().updateRateDelayPacktloss();
		}

		return output;
	}

	/**
	 * This method sends stop as a parameter to the relevant hardware
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

		String output = "stop";
		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);
		if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
			EvolvedPacketCore epcGeneric = (EvolvedPacketCore) adapter
					.getInstanceObject(instanceURI);
			output = epcGeneric.getPdnGateway().stopInstance();
		}
		return output;
	}

	/**
	 * This method sends stop as a parameter to the script net-fuseco.sh
	 * 
	 * @param adapterName
	 * @param instanceURI
	 * @return
	 */
	@GET
	@Path("start/{adapterName}/{instanceURI}")
	public String startInstance(@PathParam("adapterName") String adapterName,
			@PathParam("instanceURI") String instanceURI) {
		LOGGER.log(Level.INFO, "Send start script for " + instanceURI
				+ ", in adapter " + adapterName);

		String output = "start";
		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);
		if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
			EvolvedPacketCore epcGeneric = (EvolvedPacketCore) adapter
					.getInstanceObject(instanceURI);
			output = epcGeneric.getPdnGateway().startInstance();
		}
		return output;
	}

	/**
	 * Standard api methods. Get the ABox for the assumed adapter. e.g.
	 * http://localhost:8080/lterf/epc/get
	 * 
	 * @return
	 */
	@GET
	@Path("get")
	@Produces("text/turtle")
	public String get() {

		LOGGER.log(Level.INFO, "Try to run method get(" + adapterName + ")");

		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);
		Model model = adapter.getAdapterDescriptionModel();
		String modelString = MessageUtil.serializeModel(model,
				IMessageBus.SERIALIZATION_TURTLE);

		// LOGGER.log(Level.INFO, "Returns: " + modelString);

		return modelString;
	}

	/**
	 * Start the EPC, of which there is assumed to be only one, e.g.
	 * http://localhost:8080/lterf/epc/start
	 * 
	 * @return
	 */
	@GET
	@Path("start")
	public String start() {

		LOGGER.log(Level.INFO,
				"Send start script for an EPC resource in adapter "
						+ adapterName);

		String output = "start";
		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);

		// find epc resource
		HashMap<String, EpcGeneric> instances = adapter.getAllInstanceObjects();
		Iterator<Entry<String, EpcGeneric>> it = instances.entrySet()
				.iterator();

		boolean epcFound = false;
		while (it.hasNext() && !epcFound) {
			Entry<String, EpcGeneric> next = it.next();
			EpcGeneric epcGeneric = next.getValue();
			String instanceURI = next.getKey();
			if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
				epcFound = true;
				EvolvedPacketCore epc = (EvolvedPacketCore) epcGeneric;
				output = epc.getPdnGateway().startInstance();
			}
		}
		return output;

	}

	/**
	 * Stop the EPC, of which there is assumed to be only one, e.g.
	 * http://localhost:8080/lterf/epc/stop
	 * 
	 * @return
	 */
	@GET
	@Path("stop")
	public String stop() {

		LOGGER.log(Level.INFO,
				"Send stop script for an EPC resource in adapter "
						+ adapterName);

		String output = "stop";
		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);

		// find epc resource
		HashMap<String, EpcGeneric> instances = adapter.getAllInstanceObjects();
		Iterator<Entry<String, EpcGeneric>> it = instances.entrySet()
				.iterator();

		boolean epcFound = false;
		while (it.hasNext() && !epcFound) {
			Entry<String, EpcGeneric> next = it.next();
			EpcGeneric epcGeneric = next.getValue();
			String instanceURI = next.getKey();
			if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
				epcFound = true;
				EvolvedPacketCore epc = (EvolvedPacketCore) epcGeneric;
				output = epc.getPdnGateway().stopInstance();
			}
		}
		return output;

	}

	/**
	 * Restart (stop and start with 5 second delay) the EPC, of which there is
	 * assumed to be only one, e.g. http://localhost:8080/lterf/epc/restart
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	@GET
	@Path("restart")
	public String restart() throws InterruptedException {

		LOGGER.log(Level.INFO,
				"Send restart script for an EPC resource in adapter "
						+ adapterName);

		String output = "restart";
		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);

		// find epc resource
		HashMap<String, EpcGeneric> instances = adapter.getAllInstanceObjects();
		Iterator<Entry<String, EpcGeneric>> it = instances.entrySet()
				.iterator();

		boolean epcFound = false;
		while (it.hasNext() && !epcFound) {
			Entry<String, EpcGeneric> next = it.next();
			EpcGeneric epcGeneric = next.getValue();
			String instanceURI = next.getKey();
			if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
				epcFound = true;
				EvolvedPacketCore epc = (EvolvedPacketCore) epcGeneric;
				output = epc.getPdnGateway().stopInstance();
				Thread.sleep(5000);
				output += epc.getPdnGateway().startInstance();
			}
		}
		return output;

	}

	/**
	 * Restart (stop and start with 5 second delay) the EPC, of which there is
	 * assumed to be only one, e.g. http://localhost:8080/lterf/epc/restart
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	@GET
	@Path("qos")
	public String qos(@DefaultValue("-1") @QueryParam("rateUp") int rateUp,@DefaultValue("-1") @QueryParam("rateDown") int rateDown,
			@DefaultValue("-1") @QueryParam("loss") int loss,
			@DefaultValue("-1") @QueryParam("delay") int delay)
			throws InterruptedException {

		LOGGER.log(Level.INFO, "Send update script for adapter " + adapterName);

		String output = "update";
		EpcAdapter adapter = (EpcAdapter) getAdapterInstance(adapterName);

		// find epc resource
		HashMap<String, EpcGeneric> instances = adapter.getAllInstanceObjects();
		Iterator<Entry<String, EpcGeneric>> it = instances.entrySet()
				.iterator();

		boolean epcFound = false;
		while (it.hasNext() && !epcFound) {
			Entry<String, EpcGeneric> next = it.next();
			EpcGeneric epcGeneric = next.getValue();
			String instanceURI = next.getKey();
			if (adapter.getInstanceObject(instanceURI) instanceof EvolvedPacketCore) {
				epcFound = true;
				EvolvedPacketCore epc = (EvolvedPacketCore) epcGeneric;
				if (loss >= 0) {
					epc.getPdnGateway().setPacketlossCode(loss);
				}
				if (rateUp >= 0) {
					epc.getPdnGateway().setRateCodeUp(rateUp);
				}
				if (rateDown >= 0) {
					epc.getPdnGateway().setRateCodeDown(rateDown);
				}
				if (delay >= 0) {
					epc.getPdnGateway().setDelayCode(delay);
				}
				output = epc.getPdnGateway().updateRateDelayPacktloss();
			}
		}
		return output;

	}
}
