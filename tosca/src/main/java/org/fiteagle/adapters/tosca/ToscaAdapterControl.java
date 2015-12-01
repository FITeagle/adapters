package org.fiteagle.adapters.tosca;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import java.io.ByteArrayInputStream;
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
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.client.OrchestratorClient;
import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.fiteagle.adapters.tosca.dm.ToscaMDBSender;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.OntologyModelUtil;

@Singleton
@Startup
public class ToscaAdapterControl extends AdapterControl
{
    private static final String ENDPOINT = "endpoint";
    private static final String ORCHESTRATOR_ENDPOINT = "orchestrator_endpoint";
    private static final String ADMIN_ENDPOINT = "admin_endpoint";

    @Inject
    protected ToscaMDBSender mdbSender;
    final String TOSCA_ENDPOINT = "tosca_endpoint";
    Logger LOGGER = Logger.getLogger(getClass().getName());

    @PostConstruct
    public void initialize() { this.LOGGER.log(Level.SEVERE, "Starting Tosca");
        this.adapterModel = OntologyModelUtil.loadModel("ontologies/tosca.ttl", "TURTLE");

        this.adapterInstancesConfig = readConfig("ToscaAdapter");

        createAdapterInstances();

        publishInstances();
    }

    public AbstractAdapter createAdapterInstance(Model model, Resource resource)
    {
        ToscaAdapter adapter = new ToscaAdapter(model, resource, this.mdbSender);
        return adapter;
    }

    protected void parseConfig()
    {
        String jsonProperties = this.adapterInstancesConfig.readJsonProperties();
        if (!jsonProperties.isEmpty()) {
            JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonProperties.getBytes()));

            JsonObject jsonObject = jsonReader.readObject();

            JsonArray adapterInstances = jsonObject.getJsonArray("adapterInstances");

            for (int i = 0; i < adapterInstances.size(); i++)
            {
                JsonObject configInstanceObject = adapterInstances.getJsonObject(i);
                String configComponentId = configInstanceObject.getString("componentID");

                if (!configComponentId.isEmpty()) {
                    String endpoint = configInstanceObject.getString("endpoint");
                    String orchestrator_enpoint = configInstanceObject.getString("orchestrator_endpoint");
                    String toscaEndpointURI = endpoint + configInstanceObject.getString("tosca_endpoint");
                    String adminEndpoint = configInstanceObject.getString("admin_endpoint");

                    boolean jsonAdapter = configInstanceObject.getBoolean("json");
                    if (jsonAdapter) {
                        JsonArray datacenters = configInstanceObject.getJsonArray("datacenters");
                        OscoclientConfigObject oscoclientConfigObject = new OscoclientConfigObject();

                        oscoclientConfigObject.addDatacenters(datacenters);
                        oscoclientConfigObject.setEndpoint(endpoint);
                        oscoclientConfigObject.setOrchestratorEndpoint(orchestrator_enpoint);
                        oscoclientConfigObject.setAdminEndpoint(adminEndpoint);
                        OrchestratorClient client = new OrchestratorClient(oscoclientConfigObject);
                        ToscaAdapterBuilder adapterBuilder = new ToscaAdapterBuilder(client, null);
                        Resource adapterInstance = this.adapterModel.createResource(configComponentId);
                        OSCOAdapter oscoAdapter = new OSCOAdapter(this.adapterModel, adapterInstance, this.mdbSender);
                        adapterBuilder.handleAdapter(oscoAdapter);
                        oscoAdapter.setClient(client);
                        oscoAdapter.addDatacenters(adapterBuilder.getDatacenters());
                        this.adapterInstances.put(oscoAdapter.getId(), oscoAdapter);
                    }
                    else {
                        OrchestratorClient client = new OrchestratorClient(endpoint);
                        client.setOrchestratorEndpoint(orchestrator_enpoint);
                        client.setAdminEndpoint(adminEndpoint);
                        IToscaClient toscaClient = new ToscaClient(toscaEndpointURI);
                        ToscaAdapterBuilder adapterBuilder = new ToscaAdapterBuilder(client, toscaClient);

                        Resource adapterInstance = this.adapterModel.createResource(configComponentId);
                        ToscaAdapter adapter = (ToscaAdapter)createAdapterInstance(this.adapterModel, adapterInstance);

                        adapterBuilder.handleAdapter(adapter);
                        this.adapterInstances.put(adapter.getId(), adapter);
                    }
                }
            }
        }
    }

    protected void addAdapterProperties(Map<String, String> adapterInstnaceMap)
    {
        adapterInstnaceMap.put("tosca_endpoint", "");
    }
}