package org.fiteagle.adapters.epc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapter;
import org.fiteagle.adapters.epc.dm.EpcAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Created by dne on 15.06.15.
 */
@Singleton
@Startup
public class EpcAdapterControl extends AdapterControl {

	@Inject
	protected EpcAdapterMDBSender mdbSender;

	private static final Logger LOGGER = Logger
			.getLogger(EpcAdapterControl.class.getName());

	@PostConstruct
	public void initialize() {
		LOGGER.log(Level.INFO, "Starting EpcAdapter");
		this.adapterModel = OntologyModelUtil.loadModel(
				"ontologies/epc-adapter.ttl", IMessageBus.SERIALIZATION_TURTLE);

		this.adapterInstancesConfig = this.readConfig("EpcAdapter");

		this.createAdapterInstances();

		this.publishInstances();

	}

	@Override
	public AbstractAdapter createAdapterInstance(final Model tbox,
			final Resource abox) {
		final AbstractAdapter adapter = new EpcAdapter(tbox, abox);
		this.adapterInstances.put(adapter.getId(), adapter);
		return adapter;
	}

	@Override
	protected void parseConfig() {

		final String jsonProperties = this.adapterInstancesConfig
				.readJsonProperties();
		if (!jsonProperties.isEmpty()) {
			final JsonReader jsonReader = Json
					.createReader(new ByteArrayInputStream(jsonProperties
							.getBytes(StandardCharsets.UTF_8)));

			final JsonObject jsonObject = jsonReader.readObject();

			final JsonArray adaptInstances = jsonObject
					.getJsonArray(IAbstractAdapter.ADAPTER_INSTANCES);

			for (int i = 0; i < adaptInstances.size(); i++) {
				final JsonObject adaptInstObject = adaptInstances
						.getJsonObject(i);
				final String adapterInstance = adaptInstObject
						.getString(IAbstractAdapter.COMPONENT_ID);
				if (!adapterInstance.isEmpty()) {
					final Model model = ModelFactory.createDefaultModel();
					final Resource resource = model
							.createResource(adapterInstance);
					// parse possible additional values from config
					this.createAdapterInstance(this.adapterModel, resource);
				}
			}

		}

	}

	@Override
	@SuppressWarnings("PMD.GuardLogStatementJavaUtil")
	protected void addAdapterProperties(final Map<String, String> adaptInstance) {
		LOGGER.warning("Not implemented. Input: " + adaptInstance);
	}

}
