package org.fiteagle.adapters.openMTC;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

import java.util.Iterator;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenMTCAdapterTest {
  
  private static AbstractAdapter adapter;
  
  @BeforeClass
  public static void loadModel(){
    Iterator<String> iter = OpenMTCAdapter.adapterInstances.keySet().iterator();
    if(iter.hasNext()){
        adapter = OpenMTCAdapter.adapterInstances.get(iter.next());
    }
  }
  
  @Test
  public void testAdapterModel() {
    assertTrue(adapter.getAdapterDescriptionModel().contains(adapter.getAdapterABox(), RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
  }
  
  @Test
  public void testAdapterPrefixes() {
    assertNotNull(adapter.getAdapterManagedResources().get(0).getNameSpace());
    assertNotNull(adapter.getAdapterInstance().getNameSpace());
  }
  
  @Test
  public void testAdapterResource() {
    assertTrue(adapter.getAdapterManagedResources().get(0).hasProperty(Omn_lifecycle.implementedBy, adapter.getAdapterABox()));
    assertTrue(adapter.getAdapterManagedResources().get(0).hasProperty(RDFS.subClassOf, Omn.Resource));
  }

  @Test
  public void testAdapterType() {
      assertTrue(adapter.getAdapterABox().hasProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter));
      assertTrue(adapter.getAdapterABox().hasProperty(Omn_lifecycle.implements_, adapter.getAdapterManagedResources().get(0)));
  }
  
  @Test
  public void testAdapterInstance() {
      assertTrue(adapter.getAdapterInstance().hasProperty(RDF.type, adapter.getAdapterABox()));
      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.comment));
      assertTrue(adapter.getAdapterInstance().hasProperty(RDFS.label)); 
  }
  
}
