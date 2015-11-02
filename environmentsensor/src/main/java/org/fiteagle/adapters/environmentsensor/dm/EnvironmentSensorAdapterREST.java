package org.fiteagle.adapters.environmentsensor.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.environmentsensor.EnvironmentSensorAdapterControl;

@Path("/")
public class EnvironmentSensorAdapterREST extends AbstractAdapterREST {

    @EJB
    private transient EnvironmentSensorAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }

    @GET
    @Path("/hallo")
    public Response hallo() {
	return Response.ok("HalloWelt2").build();
    }

    @GET
    @Path("/ontology")
    public Response ont() {
	return Response.ok("Ontology").build();
    }

}
