package org.fiteagle.adapters.mightyrobot.dm;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Path("/")
public class MightyRobotAdapterREST extends AbstractAdapterREST {
    
	@Inject
    MightyRobotAdapter mightyRobotAdapter;

	@Override
	public AbstractAdapter handleSetup() {
		return mightyRobotAdapter;
	}
    
}
