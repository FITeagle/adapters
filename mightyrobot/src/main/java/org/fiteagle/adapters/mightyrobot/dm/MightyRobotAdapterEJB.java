package org.fiteagle.adapters.mightyrobot.dm;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapterEJB;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Singleton(name = "MightyRobotAdapter")
@Remote(IAbstractAdapterEJB.class)
public class MightyRobotAdapterEJB extends AbstractAdapterEJB {

    public MightyRobotAdapterEJB() {
        super.adapter = MightyRobotAdapter.getInstance();
    }
}
