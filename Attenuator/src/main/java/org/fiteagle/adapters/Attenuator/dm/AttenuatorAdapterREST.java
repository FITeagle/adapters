package org.fiteagle.adapters.Attenuator.dm;

import info.openmultinet.ontology.vocabulary.Epc;

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
import org.fiteagle.adapters.Attenuator.AttenuatorAdapter;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapterControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

@Path("/")
public class AttenuatorAdapterREST extends AbstractAdapterREST{
  
  private Logger LOGGER  = LoggerFactory.getLogger(this.getClass().getName());
  
  @EJB
  private transient AttenuatorAdapterControl controller;
  
  @Override
  protected Collection<AbstractAdapter> getAdapterInstances() {
    return this.controller.getAdapterInstances();
  }
  
  /**
   * 
   * @param id : attenuator_id located in configuration file
   * @param value : desired attenuation
   * @return
   */
  @POST
  @Path("{adapterName}/{attenuator_id}/{attenuator_value}")
  @Produces("text/html")
  public Response configureAttenuator(@PathParam("adapterName") String adapterName, 
      @PathParam("attenuator_id") String id, 
      @PathParam("attenuator_value") String value){
    
    LOGGER.info("Setting attenuator " + id + " to " + value + "dB . . .");
    
    Model configModel = ModelFactory.createDefaultModel();
    Resource resource = configModel.createResource("http://open-multinet.info/ontology/resource/Attenuator#" + adapterName);
    resource.addProperty(Epc.attenuator, value);
    
    AttenuatorAdapter adapter = (AttenuatorAdapter) getAdapterInstance(adapterName);
    Model model = adapter.updateInstance("instanceURI", configModel);
    
    
    
    return Response.ok("responseString", "text/html").build();
  }
  
  
  
}
