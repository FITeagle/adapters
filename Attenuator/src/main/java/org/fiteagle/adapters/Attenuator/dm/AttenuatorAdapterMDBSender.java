package org.fiteagle.adapters.Attenuator.dm;

import javax.ejb.EJB;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapterControl;

public class AttenuatorAdapterMDBSender extends AbstractAdapterMDBSender{
  
  @EJB
  @SuppressWarnings("PMD.UnusedPrivateField")
  private transient AttenuatorAdapterControl controller;
  
}
