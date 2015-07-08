package org.fiteagle.adapters.tosca.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.tosca.ToscaAdapterControl;

@Singleton
@Remote(AdapterEJB.class)
public class ToscaAdapterEJB extends AbstractAdapterEJB{
  
  @EJB
  ToscaAdapterControl toscaAdapterControl;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return toscaAdapterControl.getAdapterInstances();
  }
}
