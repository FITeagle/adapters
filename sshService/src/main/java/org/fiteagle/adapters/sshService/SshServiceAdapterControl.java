package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn_service;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.adapters.sshService.dm.SshServiceAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

@Singleton
@Startup
public class SshServiceAdapterControl extends AdapterControl {


    @Inject
    SshServiceAdapterMDBSender mdbSender;

    Logger LOGGER = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    public void initialize() {
        LOGGER.log(Level.SEVERE, "Starting SshService");
        this.adapterModel = OntologyModelUtil.loadModel(ISshService.SEMANTIC_DESCRIPTION_PATH, IMessageBus.SERIALIZATION_TURTLE);

        this.adapterInstancesConfig = readConfig(ISshService.SSH_SERVICE);

        createAdapterInstances();

        publishInstances();

    }


    @Override
    protected void parseConfig() {


        String jsonProperties = this.adapterInstancesConfig.readJsonProperties();
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonProperties.getBytes()));

        JsonObject jsonObject = jsonReader.readObject();

        JsonArray adapterInstances = jsonObject.getJsonArray(ISshService.ADAPTER_INSTANCES);

        for (int i = 0; i < adapterInstances.size(); i++) {


            JsonObject adapterInstanceObject = adapterInstances.getJsonObject(i);
            String adapterInstance_componentID = adapterInstanceObject.getString(ISshService.COMPONENT_ID);

            if (!adapterInstance_componentID.isEmpty()) {
                Model model = ModelFactory.createDefaultModel();
                Resource resource = model.createResource(adapterInstance_componentID);

                String adapterInstance_AccessUsername = adapterInstanceObject.getString(ISshService.USERNAME);
                if (adapterInstance_AccessUsername != null) {
                    resource.addProperty(model.createProperty(Omn_service.getURI(), ISshService.USERNAME), adapterInstance_AccessUsername);
                }

                String adapterIntance_IP = adapterInstanceObject.getString(ISshService.IP);
                if (adapterIntance_IP != null) {
                    resource.addProperty(model.createProperty(Omn_service.getURI(), ISshService.IP), adapterIntance_IP);
                }

                String adapterInstance_PrivateKeyPath = adapterInstanceObject.getString(ISshService.PRIVATE_KEY_PATH);
                if (adapterInstance_PrivateKeyPath != null) {
                    resource.addProperty(model.createProperty(Omn_service.getURI(), ISshService.PRIVATE_KEY_PATH), adapterInstance_PrivateKeyPath);
                }

                String adapterInstance_PrivateKeyPassword = adapterInstanceObject.getString(ISshService.PRIVATE_KEY_PASSWORD);
                if (adapterInstance_PrivateKeyPassword != null) {
                    resource.addProperty(model.createProperty(Omn_service.getURI(), ISshService.PRIVATE_KEY_PASSWORD), adapterInstance_PrivateKeyPassword);
                }

                String adapterInstance_password = adapterInstanceObject.getString(ISshService.PASSWORD);
                if (adapterInstance_password != null) {
                    resource.addProperty(model.createProperty(Omn_service.getURI(), ISshService.PASSWORD), adapterInstance_password);
                }

                String adapterInstance_sshPort = adapterInstanceObject.getString(ISshService.SSH_PORT);
                if (adapterInstance_sshPort != null) {
                    resource.addProperty(model.createProperty(Omn_service.getURI(), ISshService.SSH_PORT), adapterInstance_sshPort);
                }

                createAdapterInstance(adapterModel, resource);
            }
        }


    }


    @Override
    public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
        AbstractAdapter adapter = new SshServiceAdapter(tbox, abox);
        adapterInstances.put(adapter.getId(), adapter);
        return adapter;
    }

    @Override
    protected void addAdapterProperties(Map<String, String> adapterInstanceMap) {
        adapterInstanceMap.put(ISshService.PASSWORD, "");
        adapterInstanceMap.put(ISshService.PRIVATE_KEY_PATH, "");
        adapterInstanceMap.put(ISshService.PRIVATE_KEY_PASSWORD, "");
        adapterInstanceMap.put(ISshService.USERNAME, "");
        adapterInstanceMap.put(ISshService.IP, "");
        adapterInstanceMap.put(ISshService.SSH_PORT, "");

    }

}
   
   
