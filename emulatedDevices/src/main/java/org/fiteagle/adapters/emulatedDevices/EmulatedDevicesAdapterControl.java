package org.fiteagle.adapters.emulatedDevices;

import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@Singleton
@Startup
public class EmulatedDevicesAdapterControl extends AdapterControl {

	@Override
	public AbstractAdapter createAdapterInstance(Model model, Resource resource) {
		// TODO Auto-generated method stub
		return null;
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
