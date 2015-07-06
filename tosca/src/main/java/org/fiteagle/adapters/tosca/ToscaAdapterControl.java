package org.fiteagle.adapters.tosca;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 03.07.15.
 */

@Singleton
@Startup
public class ToscaAdapterControl extends AdapterControl {

    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    @PostConstruct
    public void initialize(){
        LOGGER.log(Level.SEVERE, "Starting Tosca");
        this.adapterModel =  OntologyModelUtil.loadModel("ontologies/tosca.ttl", IMessageBus.SERIALIZATION_TURTLE);

        this.adapterInstancesConfig= readConfig("ToscaAdapter");

        createAdapterInstances();

        publishInstances();

    }


    @Override
    public AbstractAdapter createAdapterInstance(Model model, Resource resource) {
      return new ToscaAdapter(model,resource);
    }

    @Override
    protected void parseConfig() {


        String jsonProperties = this.adapterInstancesConfig.readJsonProperties();
        if(!jsonProperties.isEmpty()){
            JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonProperties.getBytes()));

            JsonObject jsonObject = jsonReader.readObject();

            JsonArray adapterInstances = jsonObject.getJsonArray("adapterInstances");

            for (int i = 0; i < adapterInstances.size(); i++) {
                JsonObject adapterInstanceObject = adapterInstances.getJsonObject(i);
                String adapterInstance = adapterInstanceObject.getString("componentID");
                String toscaEndpoint = adapterInstanceObject.getString("tosca_endpoint");
                Model model = ModelFactory.createDefaultModel();
                Resource resource = model.createResource(adapterInstance);
                //parse possible additional values from config

                ToscaAdapter adapter = (ToscaAdapter)createAdapterInstance(adapterModel, resource);
                adapter.setToscaClient(toscaEndpoint);
                this.adapterInstances.put(adapter.getId(),adapter);
            }

        }
    }
}
