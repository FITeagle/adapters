package org.fiteagle.adapters.OpenBaton;

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
import org.fiteagle.abstractAdapter.dm.IAbstractAdapter;
import org.fiteagle.adapters.OpenBaton.dm.OpenBatonAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Singleton
@Startup
public class OpenBatonAdapterControl extends AdapterControl {
    @Inject
    protected OpenBatonAdapterMDBSender mdbSender;
    
    

    Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@PostConstruct
	public void init(){
        LOGGER.log(Level.INFO, "Starting OpenBatonAdapter");
        this.adapterModel = OntologyModelUtil.loadModel("ontologies/openbaton.ttl",
				IMessageBus.SERIALIZATION_TURTLE);


    	this.propertiesName = "OpenBatonAdapter";
    	
        this.adapterInstancesConfig= readConfig(propertiesName);

        createAdapterInstances();


        publishInstances();
	}
	@Override
	public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
        OpenBatonAdapter adapter =  new OpenBatonAdapter(tbox,abox);
        adapter.setListener(mdbSender);
        return  adapter;
	}

	@Override
	protected void parseConfig() {
        String jsonProperties = this.adapterInstancesConfig.readJsonProperties();
        if(!jsonProperties.isEmpty()){
            JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonProperties.getBytes()));

            JsonObject jsonObject = jsonReader.readObject();

            JsonArray adapterInstances = jsonObject.getJsonArray(IAbstractAdapter.ADAPTER_INSTANCES);

            for (int i = 0; i < adapterInstances.size(); i++) {
                JsonObject adapterInstanceObject = adapterInstances.getJsonObject(i);
                String adapterInstance = adapterInstanceObject.getString(IAbstractAdapter.COMPONENT_ID);
                
                if(!adapterInstance.isEmpty()){
                Model model = ModelFactory.createDefaultModel();
                Resource resource = model.createResource(adapterInstance);
                //parse possible additional values from config
                OpenBatonAdapter adapter = (OpenBatonAdapter)createAdapterInstance(adapterModel, resource);

                String username  = adapterInstanceObject.getString("username");
                adapter.setUsername(username);		
                
                String password  = adapterInstanceObject.getString("password");
                adapter.setPassword(password);		
                
                String nfvoIp  = adapterInstanceObject.getString("nfvoIp");
                adapter.setNfvoIp(nfvoIp);
                
                String nfvoPort  = adapterInstanceObject.getString("nfvoPort");
                adapter.setNfvoPort(nfvoPort);	
                
                String version  = adapterInstanceObject.getString("version");
                adapter.setVersion(version);	                
                
                String vpnIp  = adapterInstanceObject.getString("vpn-ip");
                adapter.setVpnIP(vpnIp);	                
                
                String vpnPort  = adapterInstanceObject.getString("vpn-port");
                adapter.setVpnPort(vpnPort);		
                
                this.adapterInstances.put(adapter.getId(),adapter);
                LOGGER.log(Level.SEVERE, this.adapterInstances.toString());
                adapter.init();
                }
                }
            }
	}

	@Override
	protected void addAdapterProperties(Map<String, String> adapterInstnaceMap) {
		// TODO Auto-generated method stub
		
	}

    
}
