package org.fiteagle.adapters.networking;

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
import org.fiteagle.adapters.networking.dm.NetworkingAdapterMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Singleton
@Startup
public class NetworkingAdapterControl extends AdapterControl{
  
  @Inject
  protected NetworkingAdapterMDBSender mdbSender;
  
  Logger LOGGER = Logger.getLogger(this.getClass().getName());
  
  @PostConstruct
  public void initialize(){
	  
    LOGGER.log(Level.SEVERE, "Starting NetworkingAdapter");
	this.propertiesName = "NetworkingAdapter";

    this.adapterModel = OntologyModelUtil.loadModel("ontologies/networking.ttl", IMessageBus.SERIALIZATION_TURTLE);

    this.adapterInstancesConfig= readConfig(propertiesName);

    createAdapterInstances();

    publishInstances();
    
    }
  
  @Override
  public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
    AbstractAdapter adapter = new NetworkingAdapter(tbox,abox);
    adapterInstances.put(adapter.getId(),adapter);
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

              createAdapterInstance(adapterModel, resource);
              }
            }
        }
    }
  
  
  @Override
  protected void addAdapterProperties(Map<String, String> adapterInstnaceMap){
    
  }
  
}
