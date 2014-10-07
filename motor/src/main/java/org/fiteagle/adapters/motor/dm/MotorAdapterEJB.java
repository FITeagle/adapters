package org.fiteagle.adapters.motor.dm;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapterEJB;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton(name = "MotorAdapter")
@Remote(IAbstractAdapterEJB.class)
public class MotorAdapterEJB extends AbstractAdapterEJB {

    public MotorAdapterEJB() {
        super.adapter = MotorAdapter.getInstance();
    }
}
