package org.fiteagle.adapters.Attenuator.dm;

import info.openmultinet.ontology.vocabulary.Epc;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterREST;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapter;
import org.fiteagle.adapters.Attenuator.AttenuatorAdapterControl;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
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
  // curl -k -v --request POST -H "Accept: text/html" http://localhost:8080/Attenuator/http%3A%2F%2Flocalhost%2Fresource%2FAttenuationAdapter-1/1/15
  @POST
  @Path("/{adapterURL}/{attenuator_id}/{attenuator_value}")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces("text/html")
  public String configureAttenuator(@PathParam("adapterURL") String adapterURL, 
      @PathParam("attenuator_id") String id, 
      @PathParam("attenuator_value") String value){
    
    LOGGER.info("Setting attenuator " + id + " to " + value + "dB . . .");
    
    Model configModel = ModelFactory.createDefaultModel();
    Resource resource = configModel.createResource(adapterURL);
    resource.addProperty(Epc.attenuator, value);
    
    AttenuatorAdapter adapter = (AttenuatorAdapter) getAdapterInstance(adapterURL);
    Model model = adapter.updateInstance("instanceURI", configModel);
    
    return MessageUtil.serializeModel(model,IMessageBus.SERIALIZATION_TURTLE);
  }
  
  
  // curl -i GET -H "Accept: text/html" http://localhost:8080/Attenuator/hello
  @GET
  @Path("/hello")
  public String hallo() {
    return "hello world";
  }
}
