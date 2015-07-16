package org.fiteagle.abstractAdapter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;

import javax.ejb.Local;
import javax.inject.Inject;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dne on 15.06.15.
 */
@Local
public abstract class AdapterControl {


    protected Model adapterModel;
    protected Config adapterInstancesConfig;
    protected Map<String, AbstractAdapter> adapterInstances;

    @Inject
    protected AbstractAdapterMDBSender mdbSender;

    public abstract AbstractAdapter createAdapterInstance(Model model, Resource resource);



    public AbstractAdapter getAdapterInstance(String adapterInstanceID) {
        return adapterInstances.get(adapterInstanceID);
    }

    protected void createAdapterInstances() {
        if(this.adapterInstances == null)
            this.adapterInstances = new HashMap<>();

        parseConfig();
    }

    protected abstract void parseConfig() ;

    public Collection<AbstractAdapter> getAdapterInstances() {
        return adapterInstances.values();
    }
    public Model configure(String adapterId,Config configuration) {
        AbstractAdapter adapter = adapterInstances.get(adapterId);
        adapter.configure(configuration);
        return adapter.getAdapterDescriptionModel();
    }

    public void deleteAdapterInstance(String adapterId) {

        AbstractAdapter adapter = adapterInstances.get(adapterId);
        adapter.shutdown();
        adapterInstances.remove(adapterId);
    }

    public Config readConfig(String name) {

      Config config = new Config(name);
      if(!config.getFilePath().toFile().exists()){
        Map<String, Object> propertiesMap = new HashMap<String, Object>();
        propertiesMap.put(IConfig.KEY_HOSTNAME, IConfig.DEFAULT_HOSTNAME);
        propertiesMap.put(IConfig.LOCAL_NAMESPACE, IConfig.LOCAL_NAMESPACE_VALUE);
        propertiesMap.put(IConfig.RESOURCE_NAMESPACE,IConfig.RESOURCE_NAMESPACE_VALUE);
        
        List<Map<String, String>> adapterInstancesList = new LinkedList<Map<String, String>>();
        Map<String, String> adapterInstnaceMap = new HashMap<String, String>();
        adapterInstnaceMap.put(IAbstractAdapter.COMPONENT_ID, ""); 
        
        addAdapterProperties(adapterInstnaceMap);
        
        adapterInstancesList.add(adapterInstnaceMap);
        propertiesMap.put(IAbstractAdapter.ADAPTER_INSTANCES, adapterInstancesList);
        
        Properties property = new Properties();
        property.putAll(propertiesMap);
        config.writeProperties(property);
      }
      return config;
    }

    protected abstract void addAdapterProperties(Map<String, String> adapterInstnaceMap);
    
    protected void publishInstances() {

        for(AbstractAdapter adapter : this.getAdapterInstances()){
            publish(adapter);
        }
    }


    protected  void publish(AbstractAdapter adapter){
        adapter.addListener(this.mdbSender);
        this.mdbSender.register(adapter, 1000);
    }
}
