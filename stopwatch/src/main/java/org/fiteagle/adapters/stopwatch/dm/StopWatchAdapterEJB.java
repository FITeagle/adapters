package org.fiteagle.adapters.stopwatch.dm;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapterEJB;
import org.fiteagle.adapters.stopwatch.StopWatchAdapter;

@Singleton(name = "StopWatchAdapter")
@Remote(IAbstractAdapterEJB.class)
public class StopWatchAdapterEJB extends AbstractAdapterEJB {

    public StopWatchAdapterEJB() {
        super.adapter = StopWatchAdapter.getInstance();
    }
}
