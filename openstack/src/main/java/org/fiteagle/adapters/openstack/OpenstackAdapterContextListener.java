package org.fiteagle.adapters.openstack;


import javax.servlet.annotation.WebListener;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterContextListener;

@WebListener
public class OpenstackAdapterContextListener extends AbstractAdapterContextListener{

  @Override
  protected AbstractAdapter getAdapter() {
    return OpenstackAdapter.getInstance();
  }
}
