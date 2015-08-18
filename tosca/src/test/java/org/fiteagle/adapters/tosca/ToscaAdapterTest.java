
package org.fiteagle.adapters.tosca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn;

import java.io.InputStream;

import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
//import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
//import org.fiteagle.adapters.tosca.client.IToscaClient;
//import org.fiteagle.adapters.tosca.client.ToscaClientDummy;
//import org.fiteagle.api.core.IMessageBus;
//import org.fiteagle.api.core.OntologyModelUtil;
//import org.junit.Test;

import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ToscaAdapterTest {
  
  private ToscaAdapter adapter = null;
  private CallOpenSDNcore  callOpenSDNcore = null;
  
  @Before
  public void initialize(){
    Model dummyModel = ModelFactory.createDefaultModel();
    Resource adapterType = dummyModel.createResource("adapter type");
    Statement dummyStatement = new StatementImpl(adapterType, RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    dummyModel.add(dummyStatement);
    Resource resource = dummyModel.createResource("dummy resource");
    adapter = new ToscaAdapter(dummyModel, resource, null);
    callOpenSDNcore = new CallOpenSDNcore(dummyModel, adapter);
  }
  
  @Test
  public void testParseToDefinitions() throws InvalidRequestException {
   
    Model model = getModelFromTurtleFile("/osco.ttl");
    String definitions = callOpenSDNcore.parseToDefinitions(model);
    assertNotNull(definitions);
  }
  
  @Test
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
  
  @Test
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
    
    Model requestModel = getModelFromTurtleFile("/request-openmtc.ttl");
    
    String definations = callOpenSDNcore.parseToDefinitions(requestModel);
    assertNotNull(definations);
    
  }

  
  private ToscaAdapter createAdapterWithDummyClient() throws ProcessingException {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/tosca.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    String adapterInstance = "http://localhost/resource/ToscaAdapter-test";
    String toscaEndpoint = "http://xxx.xxx.xxx.xxx:8080/api/rest/tosca/v2/";
    Model model = ModelFactory.createDefaultModel();
    Resource resource = model.createResource(adapterInstance);
    ToscaAdapter adapter = new ToscaAdapter(adapterModel, resource, null);
    adapter.setToscaClient(toscaEndpoint);
    
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

