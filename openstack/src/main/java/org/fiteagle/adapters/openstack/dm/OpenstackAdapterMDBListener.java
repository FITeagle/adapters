package org.fiteagle.adapters.openstack.dm;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import com.hp.hpl.jena.rdf.model.Model;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBListener;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.OpenstackAdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageFilters;
import org.fiteagle.api.core.MessageUtil;

@MessageDriven(name = "OpenstackAdapterMDB", activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue =IMessageBus.METHOD_TARGET + " = '" +"http://open-multinet.info/ontology/omn-domain-pc#VMServer"  + "'"
            + "AND ("+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_CREATE+"' "
            + "OR "+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_CONFIGURE+"' "
            + "OR "+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_GET+"' "
            + "OR "+IMessageBus.METHOD_TYPE+" = '"+IMessageBus.TYPE_DELETE+"')"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class OpenstackAdapterMDBListener  extends AbstractAdapterMDBListener {
    @EJB
    OpenstackAdapterControl openstackAdapterControl;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
        return openstackAdapterControl.getAdapterInstances();
    }
}