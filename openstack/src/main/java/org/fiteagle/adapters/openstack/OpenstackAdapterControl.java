package org.fiteagle.adapters.openstack;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.adapters.openstack.dm.OpenstackAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 30.06.15.
 */

@Singleton
@Startup
public class OpenstackAdapterControl extends AdapterControl {

    @Inject
    protected OpenstackAdapterMDBSender mdbSender;


    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    @PostConstruct
    public void initialize(){
        LOGGER.log(Level.SEVERE, "Starting OpenStackAdapter");

        this.adapterModel = ModelFactory.createDefaultModel();


        this.adapterInstancesConfig= readConfig("OpenStackAdapter");

        createAdapterInstances();


        publishInstances();

    }
    @Override
    public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
        OpenstackAdapter adapter =  new OpenstackAdapter(tbox,abox);

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
                OpenstackAdapter adapter = (OpenstackAdapter)createAdapterInstance(adapterModel, resource);

                String floating_ip_pool  = adapterInstanceObject.getString("floating_ip_pool_name");
                adapter.setFloatingPool(floating_ip_pool);

                String keystone_auth_URL  = adapterInstanceObject.getString("keystone_auth_URL");
                adapter.setKeystone_auth_URL(keystone_auth_URL);

                String net_name  = adapterInstanceObject.getString("net_name");
                adapter.setNet_name(net_name);

                String nova_endpoint  = adapterInstanceObject.getString("nova_endpoint");
                adapter.setNova_endpoint(nova_endpoint);

                String keystone_password  = adapterInstanceObject.getString("keystone_password");
                adapter.setKeystone_password(keystone_password);

                String keystone_endpoint  = adapterInstanceObject.getString("keystone_endpoint");
                adapter.setKeystone_endpoint(keystone_endpoint);

                String glance_endpoint  = adapterInstanceObject.getString("glance_endpoint");
                adapter.setGlance_endpoint(glance_endpoint);

                String net_endpoint  = adapterInstanceObject.getString("net_endpoint");
                adapter.setNet_endpoint(net_endpoint);

                String tenant_name  = adapterInstanceObject.getString("tenant_name");
                adapter.setTenant_name(tenant_name);

                String keystone_username  = adapterInstanceObject.getString("keystone_username");
                adapter.setKeystone_username(keystone_username);

                String default_flavor_id  = adapterInstanceObject.getString("default_flavor_id");
                adapter.setDefault_flavor_id(default_flavor_id);

                String default_image_id  = adapterInstanceObject.getString("default_image_id");
                adapter.setDefault_image_id(default_image_id);


                adapter.initFlavors();
                this.adapterInstances.put(adapter.getId(),adapter);
            }

        }
    }
}
