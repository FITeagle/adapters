package org.fiteagle.adapters.tosca;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import info.openmultinet.ontology.Parser;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.translators.AbstractConverter;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.MultipleNamespacesException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.MultiplePropertyValuesException;
import info.openmultinet.ontology.translators.tosca.OMN2Tosca.RequiredResourceNotFoundException;
import info.openmultinet.ontology.translators.tosca.Tosca2OMN.UnsupportedException;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;


import javax.xml.bind.JAXBException;

import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.dm.ToscaMDBSender;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class CallOpenSDNcore implements Runnable{
  
     private final Model createModel;
     
     private ToscaAdapter toscaAdapter;
     
     private static Logger LOGGER = Logger.getLogger(CallOpenSDNcore.class.toString());
     
     public CallOpenSDNcore(Model createModel, ToscaAdapter toscaAdapter){
       this.createModel = createModel;
       this.toscaAdapter = toscaAdapter;
       }
     
     @Override
     public void run(){
       
       try {
         LOGGER.log(Level.INFO, "Create model: \n" + createModel);
         
         String definitions = prepareCreateModel();
         LOGGER.log(Level.INFO, "Input definitions: \n"+definitions);
        
         Definitions resultDefinitions = this.toscaAdapter.getClient().createDefinitions(definitions);
         LOGGER.log(Level.INFO, "Result definitions: \n"+ toString(resultDefinitions));
         
         Model model = parseToModel(resultDefinitions);
         LOGGER.log(Level.INFO, "Result Model: \n" + model);
         this.toscaAdapter.getSender().publishModelUpdate(model, UUID.randomUUID().toString(), IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);

         } 
       catch (InvalidRequestException e) {
         LOGGER.log(Level.SEVERE, "Definitions couldn't be created ! ", e);
         } 
       catch (ProcessingException e) {
         LOGGER.log(Level.SEVERE, "VMs could't be created ", e);
         }
     }
     
     private String prepareCreateModel() throws InvalidRequestException {
         Model model = ModelFactory.createDefaultModel();
         model = this.createModel;
         model.removeAll(null, Omn.isResourceOf, null);
         model.removeAll(null, Omn.hasReservation, null);
         model.removeAll(null, Omn_lifecycle.hasState, null);
         model.removeAll(null, Omn_lifecycle.implementedBy, null);
         
         String createModel = MessageUtil.serializeModel(model, IMessageBus.SERIALIZATION_TURTLE);
         LOGGER.log(Level.INFO, "CREATE MODEL at TOSCA ADAPTER \n" + createModel);
         
//         Map<String,String> pref = model.getNsPrefixMap();
         
        return parseToDefinitions(model);

              
     }
     
     protected String parseToDefinitions(Model model) throws InvalidRequestException {
       try {
         //InfModel infModel = createInfModel(model);
         //infModel.setNsPrefix("osco","http://opensdncore.org/ontology/");
         return OMN2Tosca.getTopology(model);
       } catch(InvalidModelException | JAXBException | MultiplePropertyValuesException | RequiredResourceNotFoundException | MultipleNamespacesException e){
         throw new InvalidRequestException(e);
       }
       
     }
     
     private InfModel createInfModel(Model model) throws InvalidModelException{
       model.add( this.toscaAdapter.getAdapterABox().getModel());
       List additionalOntologies = new ArrayList<String>();
       additionalOntologies.add("/ontologies/osco.ttl");
       Parser parser = new Parser(model, additionalOntologies);
       return parser.getInfModel();
     }
     
     
     private static String toString(Definitions definitions) throws ProcessingException {
       try {
         return AbstractConverter.toString(definitions, OMN2Tosca.JAXB_PACKAGE_NAME);
       } catch (JAXBException e) {
         throw new ProcessingException(e);
       }
     }
     
     private Model parseToModel(Definitions definitions) throws ProcessingException {
       try {
         Model resultModel = Tosca2OMN.getModel(definitions);
         LOGGER.log(Level.INFO, "Received model \n" + resultModel);
         
           this.toscaAdapter.getAdapterABox().getModel().setNsPrefixes(resultModel.getNsPrefixMap());
           
           Model returnModel = this.createModel;
           
           startTopology(returnModel);           
           addCreatedTopology(resultModel);
           
           extendResourcesProperties(returnModel, resultModel);
         
         return returnModel;
       } catch (UnsupportedException e) {
         throw new ProcessingException(e);
       }     
     }
  
     public void startTopology(Model returnModel){
       
       ResIterator resIterator = this.createModel.listResourcesWithProperty(Omn.hasResource);
       while(resIterator.hasNext()){
         Resource resource = resIterator.nextResource();
         Property property = returnModel.createProperty(Omn_lifecycle.hasState.getNameSpace(),Omn_lifecycle.hasState.getLocalName());
         property.addProperty(RDF.type, OWL.FunctionalProperty);
         Statement statement = new StatementImpl(resource, property, Omn_lifecycle.Started);
         returnModel.add(statement);
       }
     }
     
     public void addCreatedTopology(Model resultModel){
       String requestedTopologyURI = null;
       String createdTopologyURI = null;
       
       ResIterator requestedTopologyIterator = this.createModel.listResourcesWithProperty(Omn.hasResource);
       while(requestedTopologyIterator.hasNext()){
         requestedTopologyURI = requestedTopologyIterator.nextResource().getURI();
         LOGGER.log(Level.INFO, "requested topology URI " + requestedTopologyURI);
       }
       ResIterator createdTopologyIterator = resultModel.listResourcesWithProperty(RDF.type, Omn.Topology);
       while(createdTopologyIterator.hasNext()){
         createdTopologyURI = createdTopologyIterator.nextResource().getURI();
         LOGGER.log(Level.INFO, "created topology URI " + createdTopologyURI);
       }
       toscaAdapter.setTopologies(requestedTopologyURI, createdTopologyURI);
       
     }
     
     
     public void extendResourcesProperties(Model returnModel, Model resultModel){
       
       StmtIterator statementIterator_createModel = this.createModel.listStatements(new SimpleSelector((Resource)null, Omn.hasResource, (Object)null));
       
       while(statementIterator_createModel.hasNext()){
         
         Statement requestedResourceStatement = statementIterator_createModel.nextStatement();
         Resource requestedResource = requestedResourceStatement.getObject().asResource();
         String resource_id = this.createModel.getRequiredProperty(requestedResource, Omn_lifecycle.hasID).getString();
         Resource resourceProperties = this.createModel.getResource(requestedResource.getURI());
         
         StmtIterator stmtIterator = resultModel.listStatements(new SimpleSelector((Resource)null, Omn_lifecycle.hasID, (Object) resource_id));
         while(stmtIterator.hasNext()){
           Statement statement = stmtIterator.nextStatement();
           Resource createdResource = statement.getSubject();
           
           StmtIterator stmtIter = resultModel.listStatements(new SimpleSelector(createdResource, (Property)null, (RDFNode)null));
           
           while(stmtIter.hasNext()){
             Statement createdStatement = stmtIter.nextStatement();
             
             if(!resourceProperties.hasProperty(createdStatement.getPredicate(), createdStatement.getObject())){
             Statement stmt = new StatementImpl(requestedResource, createdStatement.getPredicate(), createdStatement.getObject());
             returnModel.add(stmt);
             }
           }
         }
       } 
     }
     
}
