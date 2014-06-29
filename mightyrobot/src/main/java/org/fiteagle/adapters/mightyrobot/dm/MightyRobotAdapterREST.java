package org.fiteagle.adapters.mightyrobot.dm;

import javax.ws.rs.Path;

import org.fiteagle.adapters.AbstractAdapter;
import org.fiteagle.adapters.abstractdm.AbstractAdapterREST;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Path("/")
public class MightyRobotAdapterREST extends AbstractAdapterREST {

	@Override
	public AbstractAdapter handleSetup() {
		return MightyRobotAdapter.getInstance();
	}
    
}
