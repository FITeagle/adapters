package org.fiteagle.adapters.Open5GCore;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.adapters.Open5GCore.dm.Open5GCoreAdapterMDBSender;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Singleton
@Startup
public class Open5GCoreAdapterControl extends AdapterControl {
    @Inject
    protected Open5GCoreAdapterMDBSender mdbSender;
    
    

    Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@PostConstruct
	public void init(){
        LOGGER.log(Level.INFO, "Starting Open5GCoreAdapter");
        this.adapterModel = ModelFactory.createDefaultModel();


    	this.propertiesName = "Open5GCoreAdapter";
	}
	@Override
	public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
        Open5GCoreAdapter adapter =  new Open5GCoreAdapter(tbox,abox);
        adapter.setListener(mdbSender);
        return  adapter;
	}

	@Override
	protected void parseConfig() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addAdapterProperties(Map<String, String> adapterInstnaceMap) {
		// TODO Auto-generated method stub
		
	}

    
}
