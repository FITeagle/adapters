package org.fiteagle.adapters.mightyrobot;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class MightyRobotAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static MightyRobotAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    private MightyRobotAdapterRDFHandler(){
    }

    public static synchronized MightyRobotAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new MightyRobotAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }

    @Override
    protected AbstractAdapter getAdapter() {
      return MightyRobotAdapter.getInstance();
    }
}