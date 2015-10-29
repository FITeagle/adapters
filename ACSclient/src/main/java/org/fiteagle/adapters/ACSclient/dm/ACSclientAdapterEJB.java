package org.fiteagle.adapters.ACSclient.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.AdapterEJB;
import org.fiteagle.adapters.ACSclient.ACSclientAdapterControl;

@Singleton
@Remote(AdapterEJB.class)
public class ACSclientAdapterEJB extends AbstractAdapterEJB{
  
  @EJB
  private transient ACSclientAdapterControl controller;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
return this.controller.getAdapterInstances();
  }
  
}
