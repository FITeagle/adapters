package org.fiteagle.adapters.openSDNCore.dm;

import java.util.Iterator;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.openSDNCore.OpenSDNCoreAdapter;

@Path("/")
public class OpenSDNCoreAdapterREST extends AbstractAdapterREST {

private static OpenSDNCoreAdapter adapter;
  
  @Override
  protected AbstractAdapter getAdapter() {
    if (adapter == null) {
      Iterator<String> iterator = OpenSDNCoreAdapter.adapterInstances.keySet().iterator();
      if (iterator.hasNext()) {
        adapter = OpenSDNCoreAdapter.getInstance(iterator.next());
      }
    }
    return adapter;
  }

}
