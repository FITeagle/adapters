package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.tosca.ToscaAdapter;
import org.fiteagle.adapters.tosca.ToscaAdapterControl;

@Path("/")
public class ToscaAdapterREST extends AbstractAdapterREST {


  @EJB
  ToscaAdapterControl adapterControl;
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return adapterControl.getAdapterInstances();
  }
  
}
