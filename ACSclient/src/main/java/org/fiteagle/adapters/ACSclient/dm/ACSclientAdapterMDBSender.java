package org.fiteagle.adapters.ACSclient.dm;

import javax.ejb.EJB;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.ACSclient.ACSclientAdapterControl;

public class ACSclientAdapterMDBSender extends AbstractAdapterMDBSender{
  
  
  @EJB
  @SuppressWarnings("PMD.UnusedPrivateField")
  private transient ACSclientAdapterControl controller;
}
