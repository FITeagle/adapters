package org.fiteagle.adapters.monitoring;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_component;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle_v2;
import info.openmultinet.ontology.vocabulary.Omn_monitoring;
import info.openmultinet.ontology.vocabulary.Omn_resource;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.fiteagle.adapters.monitoring.openstack.OpenstackClient;
import org.fiteagle.adapters.monitoring.sql.SQLite;

public final class MonitoringAdapter {
	
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  private static Logger LOGGER = Logger.getLogger(MonitoringAdapter.class.toString());  
  private static MonitoringAdapter monitoringAdapterSingleton ;

  private String prop_vmid = "http://open-multinet.info/ontology/omn-domain-pc#hasVMID" ;
  private String type_vm = "http://open-multinet.info/ontology/omn-domain-pc#VM" ;
  private String type_omsp = "http://open-multinet.info/ontology/omn-monitoring#OMSPService" ;
  private String prop_usesService = "http://open-multinet.info/ontology/omn-lifecycle#usesService" ;
  
  private String vm_id = null ;
  private String oml_uri = null ;
  private String host = null ;
 
  public static MonitoringAdapter getInstance() {
		if (monitoringAdapterSingleton != null){
			return monitoringAdapterSingleton;
		}
		else return new MonitoringAdapter();
  }

  public void handleInform(Model model){
	ResIterator resIterator =  model.listSubjects();
	
	while(resIterator.hasNext()){
		boolean createOk = false, deleteOk = false ;
		Resource r = resIterator.nextResource();
		System.out.println("In handleInform(): Resource: " + r.toString()) ;
		
		if(r.hasProperty(RDF.type, type_vm) && r.hasProperty(model.getProperty(prop_vmid))){	// if resource is vm
			if(r.hasProperty(Omn_lifecycle.hasState, Omn_lifecycle.Started)){
				createOk = handleCreate(model, r) ;
			}
			else if(r.hasProperty(Omn_lifecycle.hasState, Omn_lifecycle.Stopped)){
				deleteOk = handleDelete(model, r) ;
			}
			else{
//				LOGGER.log(Level.INFO, "Not monitoring-related resource: State of VM is not created/deleted. Ignoring...") ;
			}
		}else{
//			LOGGER.log(Level.INFO, "Not monitoring-related resource: Resource type is not VM. Ignoring...") ;
		}
		
		if(createOk && !deleteOk ){
			SQLite sql = new SQLite() ;
			sql.insert(vm_id, host, oml_uri) ;
			System.out.println("Data successfully added to database.") ;
		}else if(!createOk && deleteOk){
			SQLite sql = new SQLite() ;
			sql.delete(vm_id) ;
			System.out.println("Data successfully deleted to database.") ;
		}
	}
  }
  
  private boolean handleCreate(Model model, Resource r){
	  vm_id = r.getProperty(model.getProperty(prop_vmid)).getLiteral().getString() ;
	  if(r.hasProperty((model.getProperty(prop_usesService)))){
		  Resource service = r.getProperty(model.getProperty(prop_usesService)).getResource() ;
		  if(service.hasProperty(RDF.type, type_omsp) && service.hasProperty(Omn.hasEndpoint)){
			  oml_uri = service.getProperty(Omn.hasEndpoint).getLiteral().getString() ;
			  host = new OpenstackClient().getHostName(vm_id) ; 
		  }else{
//			  LOGGER.log(Level.INFO, "Not monitoring-related resource: OML Collector URI not found. Ignoring...") ;
			  return false ;
		  }
		}
	  else{
//		  LOGGER.log(Level.INFO, "Not monitoring-related resource: OML Collector URI not found. Ignoring...") ;
		  return false ;
	  }
	  if (vm_id != null && oml_uri != null && host != null) return true ; else return false ;
  }
  
  private boolean handleDelete(Model model, Resource r){
	  vm_id = r.getProperty(model.getProperty(prop_vmid)).getLiteral().getString() ;
	  if (vm_id != null) return true ; else return false ;
  }
}

