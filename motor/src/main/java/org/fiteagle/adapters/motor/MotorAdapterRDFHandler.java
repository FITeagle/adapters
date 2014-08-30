package org.fiteagle.adapters.motor;

import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class MotorAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static AbstractAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    public MotorAdapterRDFHandler(){
        super.adapter = MotorAdapter.getInstance();
    }

    public static synchronized AbstractAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new MotorAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }

}
