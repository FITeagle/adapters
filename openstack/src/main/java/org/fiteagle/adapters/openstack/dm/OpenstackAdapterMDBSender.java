package org.fiteagle.adapters.openstack.dm;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.OpenstackAdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

@Singleton
public class OpenstackAdapterMDBSender extends AbstractAdapterMDBSender{

  @EJB
  OpenstackAdapterControl adapterControl;

}
