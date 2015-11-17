package org.fiteagle.adapters.ACSclient.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.ACSclient.ACSclientAdapterControl;

@Path("/")
public class ACSClientAdapterREST extends AbstractAdapterREST {

    @EJB
    private transient ACSclientAdapterControl controller;

    @Override
    protected Collection<AbstractAdapter> getAdapterInstances() {
	return this.controller.getAdapterInstances();
    }
    
    @GET
    @Path("/hallo")
    public Response hallo() {
	return Response.ok("HAlloWelt").build();
    }
    
    @GET
    @Path("/bs/airinterface")
    public Response airinterface() {
	return Response.ok("Ok").build();
    }

    @GET
    @Path("/bs/config/delete")
    public Response deleteConfig() {
	return Response.ok("Ok").build();
    }
    @GET
    @Path("/bs/config/list")
    public Response listConfig() {
	return Response.ok("Ok").build();
    }    
    
    @GET
    @Path("/bs/config/load")
    public Response loadConfig() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/config/save")
    public Response saveConfig() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/default_byxml")
    public Response default_byxml() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/epc")
    public Response epc() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/get")
    public Response get() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/info")
    public Response info() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/powercontrol")
    public Response powercontrol() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/reporting")
    public Response reporting() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/restart")
    public Response restart() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/set")
    public Response set() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/status")
    public Response status() {
	return Response.ok("Ok").build();
    }
    
    @GET
    @Path("/bs/wireless")
    public Response wireless() {
	return Response.ok("Ok").build();
    }

}
