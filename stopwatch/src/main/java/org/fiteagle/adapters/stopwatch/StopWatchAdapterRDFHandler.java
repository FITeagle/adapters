package org.fiteagle.adapters.stopwatch;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class StopWatchAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static StopWatchAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    private StopWatchAdapterRDFHandler(){
    }

    public static synchronized StopWatchAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new StopWatchAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }

    @Override
    protected AbstractAdapter getAdapter() {
      return StopWatchAdapter.getInstance();
    }
}
