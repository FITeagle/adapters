package org.fiteagle.adapters.mightyrobot;

import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class RobotAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static AbstractAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    public RobotAdapterRDFHandler(){
        super.adapter = MightyRobotAdapter.getInstance();
    }

    public static synchronized AbstractAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new RobotAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }
}