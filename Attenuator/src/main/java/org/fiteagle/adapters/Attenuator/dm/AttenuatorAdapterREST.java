package org.fiteagle.adapters.Attenuator.dm;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapterControl;

@Path("/")
public class AttenuatorAdapterREST extends AbstractAdapterREST{
  
  @EJB
  private transient AttenuatorAdapterControl controller;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return this.controller.getAdapterInstances();
  }
  
  @POST
  @Path("/{attenuator_id}/{attenuator_value}")
  @Produces("text/html")
  public Response configureAttenuator(@PathParam("attenuator_id") String id, @PathParam("attenuator_value") String value){
    
    
    
    return Response.ok("responseString", "text/html").build();
  }
  
  
  
}
