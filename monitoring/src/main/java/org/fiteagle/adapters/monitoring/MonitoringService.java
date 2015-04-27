package org.fiteagle.adapters.monitoring;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_component;
import info.openmultinet.ontology.vocabulary.Omn_domain_pc;
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

public final class MonitoringService {
	
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  private static Logger LOGGER = Logger.getLogger(MonitoringService.class.toString());  
  private static MonitoringService monitoringAdapterSingleton ;

  
  private String vm_id = null ;
  private String oml_uri = null ;
  private String host = null ;
  private String vm_uri = null ;
 
  public static MonitoringService getInstance() {
		if (monitoringAdapterSingleton != null){
			return monitoringAdapterSingleton;
		}
		else return new MonitoringService();
  }

  public void handleInform(Model model){
	ResIterator resIterator =  model.listSubjects();
	
	while(resIterator.hasNext()){
		boolean createOk = false, deleteOk = false ;
		Resource r = resIterator.nextResource();
		
		if(r.hasProperty(RDF.type, Omn_domain_pc.VM) && r.hasProperty(Omn_domain_pc.hasVMID)){	// if resource is vm
			vm_uri = r.toString() ;
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
			if(sql.insert(vm_id, host, oml_uri,vm_uri)) System.out.println("Data successfully added to database.") ;
		}else if(!createOk && deleteOk){
			SQLite sql = new SQLite() ;
			if(sql.delete(vm_id)) System.out.println("Data successfully deleted to database.") ;
		}
	}
  }
  
  private boolean handleCreate(Model model, Resource r){
	  vm_id = r.getProperty(Omn_domain_pc.hasVMID).getLiteral().getString() ;
	  if(r.hasProperty(Omn_lifecycle.usesService)){
		  Resource service = r.getProperty(Omn_lifecycle.usesService).getResource() ;
		  if(service.hasProperty(RDF.type, Omn_monitoring.OMSPService) && service.hasProperty(Omn.hasEndpoint)){
			  oml_uri = service.getProperty(Omn.hasEndpoint).getLiteral().getString() ;
			  host = new OpenstackClient().getHostName(vm_id) ; 
			  if(host == null){
				  LOGGER.log(Level.INFO, "Host not found. Ignoring...") ;
				  return false ;
			  }
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
	  vm_id = r.getProperty(Omn_domain_pc.hasVMID).getLiteral().getString() ;
	  if (vm_id != null) return true ; else return false ;
  }
}

