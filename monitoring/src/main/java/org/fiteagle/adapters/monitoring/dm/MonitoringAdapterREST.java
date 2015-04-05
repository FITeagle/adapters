package org.fiteagle.adapters.monitoring.dm;

import java.util.Map;

import javax.jms.Message;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.monitoring.MonitoringAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

@Path("/")
public class MonitoringAdapterREST extends AbstractAdapterREST {
	@Override
	  protected Map<String, AbstractAdapter> getAdapterInstances() {
	    return MonitoringAdapter.adapterInstances;
	  }
	
	
	
}
