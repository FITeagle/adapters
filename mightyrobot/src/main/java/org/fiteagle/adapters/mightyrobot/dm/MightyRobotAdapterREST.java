package org.fiteagle.adapters.mightyrobot.dm;

import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.abstractdm.AbstractAdapterREST;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Path("/")
public class MightyRobotAdapterREST extends AbstractAdapterREST {

	@Override
	public AbstractAdapter handleSetup() {
		return MightyRobotAdapter.getInstance();
	}
    
}
