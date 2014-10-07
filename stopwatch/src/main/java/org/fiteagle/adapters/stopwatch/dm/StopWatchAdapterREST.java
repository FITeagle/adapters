package org.fiteagle.adapters.stopwatch.dm;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;

@Path("/")
public class StopWatchAdapterREST extends AbstractAdapterREST {

    @Override
    public AbstractAdapter handleSetup() {
        return StopWatchAdapter.getInstance();
    }

}
