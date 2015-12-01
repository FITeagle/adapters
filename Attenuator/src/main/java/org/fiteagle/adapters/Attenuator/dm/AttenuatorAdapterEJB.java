package org.fiteagle.adapters.Attenuator.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapterControl;

@Singleton
@Remote(AdapterEJB.class)
public class AttenuatorAdapterEJB extends AbstractAdapterEJB{
  
  @EJB
  private transient AttenuatorAdapterControl controller;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
return this.controller.getAdapterInstances();
  }
  
}
