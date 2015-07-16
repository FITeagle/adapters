
package org.fiteagle.adapters.tosca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn;

import java.io.InputStream;

import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.client.ToscaClientDummy;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.OntologyModelUtil;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ToscaAdapterTest {
  
  private ToscaAdapter adapter = null;
  
//  @Test
  public void testParseToDefinitions() throws InvalidRequestException {
    Model model = getModelFromTurtleFile("/osco.ttl");
    String definitions = adapter.parseToDefinitions(model);
    assertNotNull(definitions);
  }
  
//  @Test
  public void testGetLocalname() throws InvalidRequestException {
    Model model = getModelFromTurtleFile("/osco.ttl");
    adapter.updateAdapterDescriptionWithModel(model);
    
    String localname = adapter.getLocalname("osco:dummy");
    assertNotNull(localname);
    assertEquals("dummy", localname);
    
    localname = adapter.getLocalname("http://opensdncore.org/ontology/dummy");
    assertNotNull(localname);
    assertEquals("dummy", localname);
  }
  
//  @Test
  public void testCreateInfModel() throws InvalidModelException {
    Model ontologyModel = getModelFromTurtleFile("/osco.ttl");
    adapter.updateAdapterDescriptionWithModel(ontologyModel);
    
    Model model = getModelFromTurtleFile("/request-dummy.ttl");
    
    Resource dummy = model.getResource("http://opensdncore.org/ontology/dummy");
    Resource dummy1 = model.listSubjectsWithProperty(RDF.type, dummy).next();
    assertFalse(model.contains(dummy1, RDF.type, Omn.Resource));
    assertFalse(ontologyModel.contains(dummy1, RDF.type, Omn.Resource));
    
    Model infModel = adapter.createInfModel(model);
    assertTrue(infModel.contains(dummy1, RDF.type, Omn.Resource));
  }
  
  @Test
  public void testCreateInstance() throws ProcessingException, InvalidRequestException{
    ToscaAdapter testAdapter = createAdapterWithDummyClient();
    
//    Model requestModel = getModelFromTurtleFile("/request-dummy.ttl");
    Model requestModel = getModelFromTurtleFile("/request-openmtc.ttl");
//    Model responseModel = testAdapter.createInstances(requestModel);
//    assertNotNull(responseModel);
    
    String definations = testAdapter.parseToDefinitions(requestModel);
    System.out.println("DEFINITIONS ARE " + definations);
    assertNotNull(definations);
    
//    Resource dummy = responseModel.getResource("http://opensdncore.org/ontology/dummy");
//    Resource dummy1 = responseModel.listSubjectsWithProperty(RDF.type, dummy).next();
//    assertTrue(responseModel.contains(dummy1, RDF.type, dummy));
  }

  private ToscaAdapter createAdapterWithDummyClient() throws ProcessingException {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/tosca.ttl", IMessageBus.SERIALIZATION_TURTLE);
//    IToscaClient testClient = new ToscaClientDummy();
    
    String adapterInstance = "http://localhost/resource/ToscaAdapter-test";
    String toscaEndpoint = "http://130.149.247.221:8080/api/rest/tosca/v2/";
    Model model = ModelFactory.createDefaultModel();
    Resource resource = model.createResource(adapterInstance);
    ToscaAdapter adapter = new ToscaAdapter(adapterModel, resource);
    adapter.setToscaClient(toscaEndpoint);
    
//    ToscaAdapter testAdapter = new ToscaAdapter(adapterModel, testClient);
//    adapter.updateAdapterDescription();
    return adapter;
  }
  
  private Model getModelFromTurtleFile(String path){
    InputStream input = getClass().getResourceAsStream(path);
    Model model = ModelFactory.createDefaultModel();
    final RDFReader reader = model.getReader("TTL");
    reader.read(model, input, null);
    return model;
  }
  
  
}

