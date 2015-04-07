package org.fiteagle.adapters.sshService.dm;

import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.atlas.logging.Log;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST.AdapterWebApplicationException;
import org.fiteagle.adapters.sshService.SshServiceAdapter;
import org.fiteagle.api.core.OntologyModelUtil;

@Path("/")
public class SshServiceAdapterREST extends AbstractAdapterREST {
  
  @Override
  protected Map<String, AbstractAdapter> getAdapterInstances() {
	  Log.fatal("Instances", SshServiceAdapter.adapterInstances.toString());
    return SshServiceAdapter.adapterInstances;
  }
  
  
  
}
