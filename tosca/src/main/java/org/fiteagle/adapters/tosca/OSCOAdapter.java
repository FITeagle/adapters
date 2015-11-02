package org.fiteagle.adapters.tosca;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.client.OrchestratorClient;
import org.fiteagle.adapters.tosca.dm.ToscaMDBSender;
import org.fiteagle.adapters.tosca.model.*;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 29.10.15.
 */
public class OSCOAdapter extends AbstractAdapter {


    private final ToscaMDBSender sender;
    private OrchestratorClient client;
    private Logger LOGGER = Logger.getLogger(OSCOAdapter.class.getName());
    private Map<String, TopologyResponse> topologies = new HashMap<String, TopologyResponse>();
    private List<Datacenter> datacenters;


    public OSCOAdapter( Model adapterTBox, Resource adapterABox, ToscaMDBSender sender) {
        this.sender = sender;
        this.uuid = UUID.randomUUID().toString();
        this.adapterTBox = adapterTBox;
        this.adapterABox = adapterABox;
        Resource adapterType = null;
        ResIterator adapterIterator = adapterTBox.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
        if (adapterIterator.hasNext()) {
            adapterType = adapterIterator.next();
        }
        this.adapterABox.addProperty(RDF.type, adapterType);
        this.adapterABox.addProperty(RDFS.label, adapterABox.getLocalName());

        this.adapterABox.addProperty(RDFS.comment, "An adapter for TOSCA-compliant resources");
        Resource testbed = adapterABox.getModel().createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
        this.adapterABox.addProperty(Omn_federation.partOfFederation, testbed);

    }
    @Override
    public Resource getAdapterABox() {
        return this.adapterABox;
    }

    @Override
    public Model getAdapterDescriptionModel() {
        return this.adapterABox.getModel();
    }

    @Override
    public void updateAdapterDescription() throws ProcessingException {

    }

    @Override
    public Model updateInstance(String instanceURI, Model configureModel) throws InvalidRequestException, ProcessingException {
        return configureModel;
    }

    @Override
    public Model createInstances(Model newInstanceModel){
        LOGGER.log(Level.INFO, "OSCO-ADAPTER RECEIVED CREATE");
        try {
            ManagedThreadFactory threadFactory = (ManagedThreadFactory) new InitialContext().lookup("java:jboss/ee/concurrency/factory/default");
            CallOpenSDNcoreJson callOpenSDNcore = new CallOpenSDNcoreJson(newInstanceModel, this);
            callOpenSDNcore.addDatacenters(datacenters);
            callOpenSDNcore.addDatacenterConfig(client.getConfig());
            Thread callOpenSDNcoreThread = threadFactory.newThread(callOpenSDNcore);
            callOpenSDNcoreThread.start();
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "REQUIRED VMs counldn't be created ", e);
        }


        Model returnModel = ModelFactory.createDefaultModel();
        ResIterator resIterator = newInstanceModel.listSubjectsWithProperty(Omn.isResourceOf);
        while(resIterator.hasNext()){
            Resource resource = resIterator.nextResource();
            Resource res = returnModel.createResource(resource.getURI());
            Property property = returnModel.createProperty(Omn_lifecycle.hasState.getNameSpace(), Omn_lifecycle.hasState.getLocalName());
            property.addProperty(RDF.type, OWL.FunctionalProperty);
            res.addProperty(property, Omn_lifecycle.Uncompleted);
        }

        return returnModel;

    }
    @Override
    public Model createInstance(String instanceURI, Model newInstanceModel) throws ProcessingException, InvalidRequestException {

        return createInstances(newInstanceModel);

    }
    @Override
    public Model deleteInstances(Model model) throws InvalidRequestException, ProcessingException {

        Model deletedInstancesModel = ModelFactory.createDefaultModel();

        NodeIterator nodeIterator = model.listObjectsOfProperty(Omn.hasResource);
        while(nodeIterator.hasNext()){
            RDFNode node = nodeIterator.nextNode();
            String instanceLocalName = node.asResource().getLocalName();
            if(this.topologies.get(instanceLocalName)!= null){
                TopologyResponse topologyResponse = topologies.get(instanceLocalName);
                client.deleteTopology(topologyResponse.getId());
                topologies.remove(instanceLocalName);

            }

            Resource deletedInstance = deletedInstancesModel.createResource(node.asResource().getURI());
            deletedInstance.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Removing);
        }

        return deletedInstancesModel;
    }
    protected String getLocalname(String instanceURI) throws InvalidRequestException {
        try{
            return OntologyModelUtil.getNamespaceAndLocalname(instanceURI, this.adapterABox.getModel().getNsPrefixMap())[1];
        } catch(IllegalArgumentException e){
            throw new InvalidRequestException(e);
        }
    }
    @Override
    public void deleteInstance(String instanceURI) throws  InvalidRequestException, ProcessingException {
//        TopologyResponse deleteTopology = this.topologies.get(instanceURI);
//        if(deleteTopology != null){
//            String topologyId =
//        }
//        LOGGER.log(Level.INFO, "deleting Topology " + instanceURI + " . Its equivalent URI is " + deleteTopology);
//        String id = getLocalname(deleteTopology);
//        client.deleteTopology(id);
//        this.topologies.remove(instanceURI);
    }

    @Override
    public Model getInstance(String instanceURI) throws InstanceNotFoundException, ProcessingException, InvalidRequestException {
//        String id = getLocalname(instanceURI);
//        TopologyResponse topology;
//        topology = client.getTopology(id);
//        LOGGER.log(Level.INFO, "Result definitions: \n" +topology.toString());
//
//        return parseToModel(topology);
        return this.adapterABox.getModel();
    }

    @Override
    public Model getAllInstances() throws InstanceNotFoundException, ProcessingException {
       return this.adapterABox.getModel();
    }

    @Override
    public void refreshConfig() throws ProcessingException {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void configure(Config configuration) {

    }

    public OrchestratorClient getClient() {
        return client;
    }

    public void setClient(OrchestratorClient client) {
        this.client = client;
    }

    public ToscaMDBSender getSender() {
        return sender;
    }

    public Model parseToModel(Model newInstanceModel, TopologyResponse result) {
        Model returnModel = ModelFactory.createDefaultModel();
        ResIterator resIterator = newInstanceModel.listSubjectsWithProperty(Omn.isResourceOf);
        while(resIterator.hasNext()){
            Resource resource = resIterator.nextResource();
            Resource res = returnModel.createResource(resource.getURI());
            Property property = returnModel.createProperty(Omn_lifecycle.hasState.getNameSpace(), Omn_lifecycle.hasState.getLocalName());
            property.addProperty(RDF.type, OWL.FunctionalProperty);
            res.addProperty(property, Omn_lifecycle.Ready);
            addLoginService(res,result);
        }

        return returnModel;


    }

    private void addLoginService(Resource res, TopologyResponse result) {
        String containerName = res.getLocalName();
        for(ServiceContainerResponse serviceContainer: result.getServiceContainers()){
            if(containerName.equalsIgnoreCase(serviceContainer.getContainerName())){
                Resource loginService = res.getModel().createResource(OntologyModelUtil
                        .getResourceNamespace() + "LoginService" + UUID.randomUUID().toString());
                RelationElement firstRelationElement = serviceContainer.getRelationElements().get(0);
                Unit firstUnit = firstRelationElement.getUnit();
                String ip = "";
                Object[] ipArray = null;
                HashMap<String, String> floatingIPs = firstUnit.getFloatingIps();
                if(floatingIPs != null && !floatingIPs.isEmpty()){
                    ipArray = floatingIPs.values().toArray();
                    ip = (String) ipArray[0];
                }
                if(ip.isEmpty()){
                    HashMap<String,String > ips = firstUnit.getIps();
                    ipArray = ips.values().toArray();
                    ip = (String) ipArray[0];
                }


                loginService.addProperty(RDF.type, Omn_service.LoginService);
                loginService.addProperty(Omn_service.authentication,"ssh-keys");
                loginService.addProperty(Omn_service.username, "stack");
                loginService.addProperty(Omn_service.hostname, ip);
                loginService.addProperty(Omn_service.port,"22");
                res.addProperty(Omn.hasService, loginService);
            }
        }
    }



    public void addDatacenters(List<Datacenter> datacenters) {
        this.datacenters = datacenters;
    }

    public void saveTopology(TopologyResponse result) {
        this.topologies.put(result.getServiceContainers().get(0).getContainerName(),result);
    }
}
