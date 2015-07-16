package org.fiteagle.adapters.tosca.dm;


import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.tosca.ToscaAdapterControl;

@Singleton
public class ToscaMDBSender extends AbstractAdapterMDBSender {
  @EJB
  ToscaAdapterControl adapterControl;
}
