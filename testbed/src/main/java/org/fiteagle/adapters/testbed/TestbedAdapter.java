package org.fiteagle.adapters.testbed;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.testbed.dm.TestbedAdapterMDBSender;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModels;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class TestbedAdapter {
  
  private static Logger LOGGER = Logger.getLogger(TestbedAdapter.class.toString());
  
  private static Model testbedModel;
  
  private TestbedAdapterMDBSender mib;
  
  private Resource adapterInstance;
  
  static {
    testbedModel = OntologyModels.loadModel("ontologies/testbedAdapter.ttl", "TURTLE");
    
    StmtIterator testbedIterator = testbedModel.listStatements(null, RDF.type, MessageBusOntologyModel.classTestbed);
    if (testbedIterator.hasNext()) {
      Resource testbed = testbedIterator.next().getSubject();
      LOGGER.log(Level.INFO, "Registering Testbed: "+testbed);
      instance = new TestbedAdapter(testbed);
    }
  }
  
  private TestbedAdapter(Resource instance){
    this.adapterInstance = instance;
  }
  
  private static TestbedAdapter instance;
  public static TestbedAdapter getInstance(){
    return instance;
  }
  
  public void setMDBSender(TestbedAdapterMDBSender mib){
    this.mib = mib;
  }
  
  public static Model getTestbedModel() {
    return testbedModel;
  }
  
  public Resource getAdapterInstance(){
    return adapterInstance;
  }
  
  public void deregisterAdapter() {
    Model messageModel = ModelFactory.createDefaultModel();
    messageModel.add(MessageBusOntologyModel.internalMessage, MessageBusOntologyModel.methodReleases, getAdapterInstance());
    
    mib.sendInformMessage(messageModel, null);
  }
}
