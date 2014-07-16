package org.fiteagle.adapters.mightyrobot.dm;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.mightyrobot.MightyRobotAdapter;

@Stateless
@Path("/")
public class MightyRobotAdapterREST extends AbstractAdapterREST {
    @Inject
    public  MightyRobotAdapter mightyRobotAdapter;

	@Override
	public AbstractAdapter handleSetup() {
		return mightyRobotAdapter;//MightyRobotAdapter.getInstance();
	}
    
}
