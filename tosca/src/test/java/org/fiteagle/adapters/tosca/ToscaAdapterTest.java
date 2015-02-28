package org.fiteagle.adapters.tosca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn;

import java.io.InputStream;

import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class ToscaAdapterTest {
  
  private ToscaAdapter adapter = (ToscaAdapter) ToscaAdapter.adapterInstances.values().iterator().next();
  
  @Test
  public void testParseToDefinitions() throws InvalidRequestException {
    Model model = getModelFromTurtleFile("/osco.ttl");
    String definitions = adapter.parseToDefinitions(model);
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
  
  private Model getModelFromTurtleFile(String path){
    InputStream input = getClass().getResourceAsStream(path);
    Model model = ModelFactory.createDefaultModel();
    final RDFReader reader = model.getReader("TTL");
    reader.read(model, input, null);
    return model;
  }
  
  
}
