package org.fiteagle.adapters.motor.dm;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.motor.MotorAdapter;

@Path("/")
public class MotorAdapterREST extends AbstractAdapterREST {

    @Override
    public AbstractAdapter handleSetup() {
        return MotorAdapter.getInstance();
    }

}
