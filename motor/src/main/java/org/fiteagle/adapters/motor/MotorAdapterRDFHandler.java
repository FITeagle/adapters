package org.fiteagle.adapters.motor;

import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class MotorAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static MotorAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    private MotorAdapterRDFHandler(){
        super.adapter = MotorAdapter.getInstance();
    }

    public static synchronized MotorAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new MotorAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }

}
