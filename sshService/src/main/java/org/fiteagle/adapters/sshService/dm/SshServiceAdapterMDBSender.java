package org.fiteagle.adapters.sshService.dm;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.sshService.*;
import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

@Singleton
@Startup
public class SshServiceAdapterMDBSender extends AbstractAdapterMDBSender {
	
private static Logger LOGGER = Logger.getLogger(AbstractAdapterMDBSender.class.toString());

  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
    return SshServiceAdapter.adapterInstances;
  }
  
  @Override
  public void contextDestroyed() {
	    for(AbstractAdapter adapter : getAdapterInstances().values()){
	      LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Deregistering " + adapter.getAdapterInstance().getURI());
	      Model messageModel = ModelFactory.createDefaultModel();
	      messageModel.add(adapter.getAdapterInstance(), RDF.type, adapter.getAdapterABox());
	      String fileName = adapter.getAdapterInstance().getLocalName();
	      adapter.notifyListeners(messageModel, null, IMessageBus.TYPE_DELETE, IMessageBus.TARGET_RESOURCE_ADAPTER_MANAGER);
	    }
	  }
}
