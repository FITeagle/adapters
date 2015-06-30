package org.fiteagle.adapters.motor;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;


import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fiteagle.adapters.motor.dm.MotorAdapterMDBSender;
/**
 * Created by dne on 15.06.15.
 */
@Singleton
@Startup
public class MotorAdapterControl extends AdapterControl {

    @Inject
    protected MotorAdapterMDBSender mdbSender;

    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    @PostConstruct
    public void initialize(){
        LOGGER.log(Level.SEVERE, "Starting MotorAdapter");
        this.adapterModel = OntologyModelUtil.loadModel("ontologies/motor.ttl", IMessageBus.SERIALIZATION_TURTLE);

        this.adapterInstancesConfig= readConfig("MotorGarage");

        createAdapterInstances();

        publishInstances();
        
    }





    @Override
    public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
        AbstractAdapter adapter =  new MotorAdapter(tbox,abox);
        adapterInstances.put(adapter.getId(),adapter);
        return  adapter;
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
                    Model model = ModelFactory.createDefaultModel();
                    Resource resource = model.createResource(adapterInstance);
                    //parse possible additional values from config

                    createAdapterInstance(adapterModel, resource);
                }

            }


    }


}
