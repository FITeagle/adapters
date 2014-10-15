package org.fiteagle.adapters.testbed;

import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModels;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class TestbedAdapter {
  
  private static Model testbedModel;
  
  static {
    testbedModel = OntologyModels.loadModel("ontologies/testbedAdapter.ttl", "TURTLE");
  }
  
  public static Model getTestbedModel() {
    return testbedModel;
  }
  
  public static Resource getTestbed() {
    StmtIterator iterator = testbedModel.listStatements(null, RDF.type, MessageBusOntologyModel.classTestbed);
    while (iterator.hasNext()) {
      return iterator.next().getSubject();
    }
    return null;
  }
}
