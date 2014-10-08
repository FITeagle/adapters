package org.fiteagle.adapters.motor;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AbstractAdapterRDFHandler;

public class MotorAdapterRDFHandler extends AbstractAdapterRDFHandler {

    private static MotorAdapterRDFHandler abstractAdapterRDFHandlerSingleton;

    private MotorAdapterRDFHandler(){
    }

    public static synchronized MotorAdapterRDFHandler getInstance() {
        if (abstractAdapterRDFHandlerSingleton == null) {
            abstractAdapterRDFHandlerSingleton = new MotorAdapterRDFHandler();
        }
        return abstractAdapterRDFHandlerSingleton;
    }

    @Override
    protected AbstractAdapter getAdapter() {
      return MotorAdapter.getInstance();
    }
}
