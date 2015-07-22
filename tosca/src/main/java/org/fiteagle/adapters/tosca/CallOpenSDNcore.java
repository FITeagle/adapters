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

import javax.xml.bind.JAXBException;

import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.dm.ToscaMDBSender;
import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class CallOpenSDNcore implements Runnable{
  
     private final ToscaMDBSender sender;
     
     private final Model createModel;
     
     private final Resource adapterABox;
     
     private IToscaClient client;
     
     private static Logger LOGGER = Logger.getLogger(CallOpenSDNcore.class.toString());
  
     public CallOpenSDNcore(Model createModel, ToscaMDBSender sender, Resource adapterABox, IToscaClient client){
       this.sender = sender;
       this.createModel = createModel;
       this.adapterABox = adapterABox;
       this.client = client;
       }
     
     @Override
     public void run(){
       
       try {
         String definitions = parseToDefinitions(this.createModel);
         LOGGER.log(Level.INFO, "Input definitions: \n"+definitions);
        
         Definitions resultDefinitions = client.createDefinitions(definitions);
         LOGGER.log(Level.INFO, "Result definitions: \n"+ toString(resultDefinitions));
         
         Model model = parseToModel(resultDefinitions);
         sender.publishModelUpdate(model, UUID.randomUUID().toString(), IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);

         } 
       catch (InvalidRequestException e) {
         LOGGER.log(Level.SEVERE, "Definitions couldn't be created ! ", e);
         } 
       catch (ProcessingException e) {
         LOGGER.log(Level.SEVERE, "VMs could't be created ", e);
         }
     }
     
     private String parseToDefinitions(Model createModel) throws InvalidRequestException {
       try{
         createModel.removeAll(null, Omn.isResourceOf, null);
         createModel.removeAll(null, Omn.hasReservation, null);
         createModel.removeAll(null, Omn_lifecycle.hasState, null);
         createModel.removeAll(null, Omn_lifecycle.implementedBy, null);
         
         System.out.println("CREATE MODEL " + createModel);
         Map<String,String> pref = createModel.getNsPrefixMap();

         InfModel infModel = createInfModel(createModel);
         infModel.setNsPrefix("osco","http://opensdncore.org/ontology/");
         return OMN2Tosca.getTopology(infModel);      
       } catch(InvalidModelException | JAXBException | MultiplePropertyValuesException | RequiredResourceNotFoundException | MultipleNamespacesException e){
         throw new InvalidRequestException(e);
       }
     }
     
     private InfModel createInfModel(Model model) throws InvalidModelException{
       model.add( this.adapterABox.getModel());
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
           this.adapterABox.getModel().setNsPrefixes(resultModel.getNsPrefixMap());
           
           Model returnModel = resultModel;
           ResIterator resIterator = resultModel.listResourcesWithProperty(Omn.hasResource);
           while(resIterator.hasNext()){
             Resource resource = resIterator.nextResource();
             Property property = returnModel.createProperty(Omn_lifecycle.hasState.getNameSpace(),Omn_lifecycle.hasState.getLocalName());
             property.addProperty(RDF.type, OWL.FunctionalProperty);
             Statement statement = new StatementImpl(resource, property, Omn_lifecycle.Started);
             returnModel.add(statement);
           }
         return returnModel;
       } catch (UnsupportedException e) {
         throw new ProcessingException(e);
       }      
     }
  
}
