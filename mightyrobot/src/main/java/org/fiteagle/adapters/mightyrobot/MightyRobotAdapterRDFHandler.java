package org.fiteagle.adapters.mightyrobot;

import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class MightyRobotAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static MightyRobotAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    private MightyRobotAdapterRDFHandler(){
        super.adapter = MightyRobotAdapter.getInstance();
    }

    public static synchronized MightyRobotAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new MightyRobotAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }
}