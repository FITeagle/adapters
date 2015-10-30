package org.fiteagle.adapters.Open5GCore;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public final class Open5GCoreAdapter extends AbstractAdapter {

	@Override
	public Resource getAdapterABox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getAdapterDescriptionModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAdapterDescription() throws ProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Model updateInstance(String instanceURI, Model configureModel)
			throws InvalidRequestException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model createInstance(String instanceURI, Model newInstanceModel)
			throws ProcessingException, InvalidRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException,
			ProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Model getInstance(String instanceURI)
			throws InstanceNotFoundException, ProcessingException,
			InvalidRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException,
			ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshConfig() throws ProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Config configuration) {
		// TODO Auto-generated method stub
		
	}

  

}
