package org.fiteagle.abstractAdapter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IConfig;

import javax.ejb.Local;
import javax.inject.Inject;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        return config;
    }

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
