package org.fiteagle.adapters.openstack;

import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class OpenstackAdapterRDFHandler extends AbstractAdapterRDFHandler {
  
  private static OpenstackAdapterRDFHandler RDFHandlerSingleton;

  public OpenstackAdapterRDFHandler(){
      super.adapter = OpenstackAdapter.getInstance();
  }

  public static synchronized OpenstackAdapterRDFHandler getInstance() {
      if (RDFHandlerSingleton == null) {
        RDFHandlerSingleton = new OpenstackAdapterRDFHandler();
      }
      return RDFHandlerSingleton;
  }
  
}
