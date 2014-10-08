package org.fiteagle.adapters.openstack;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class OpenstackAdapterRDFHandler extends AbstractAdapterRDFHandler {
  
  private static OpenstackAdapterRDFHandler RDFHandlerSingleton;

  private OpenstackAdapterRDFHandler(){
  }

  public static synchronized OpenstackAdapterRDFHandler getInstance() {
      if (RDFHandlerSingleton == null) {
        RDFHandlerSingleton = new OpenstackAdapterRDFHandler();
      }
      return RDFHandlerSingleton;
  }
  
  @Override
  protected AbstractAdapter getAdapter() {
    return OpenstackAdapter.getInstance();
  }
  
}
