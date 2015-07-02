package org.fiteagle.adapters.sshService.dm;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
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
public class SshServiceAdapterMDBSender extends AbstractAdapterMDBSender {
	
  @EJB
  SshServiceAdapterControl adapterControl;

}
