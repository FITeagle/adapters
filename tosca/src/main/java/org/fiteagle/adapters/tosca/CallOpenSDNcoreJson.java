package org.fiteagle.adapters.tosca;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;
import info.openmultinet.ontology.vocabulary.*;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.model.*;
import org.fiteagle.api.core.IMessageBus;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dne on 29.10.15.
 */
public class CallOpenSDNcoreJson implements Runnable{
    private final OSCOAdapter oscoAdapter;
    private  Model newInstanceModel;
    private Logger LOGGER = Logger.getLogger(CallOpenSDNcoreJson.class.getName());
    private List<Datacenter> datacenters;
    private OscoclientConfigObject oscoclientConfig;

    public CallOpenSDNcoreJson(Model newInstanceModel, OSCOAdapter oscoAdapter) {
        this.newInstanceModel = newInstanceModel;
        this.oscoAdapter = oscoAdapter;
    }

    @Override
    public void run() {

            LOGGER.log(Level.INFO, "Create model: \n" + newInstanceModel);

            TopologyRequest request = prepareTopology();
            LOGGER.log(Level.INFO, "Input request: \n"+request);

            TopologyResponse result = this.oscoAdapter.getClient().createTopology(request);
            LOGGER.log(Level.INFO, "Result definitions: \n"+ result);
            this.oscoAdapter.saveTopology(result);
            Model model = oscoAdapter.parseToModel(newInstanceModel,result);
            LOGGER.log(Level.INFO, "Result Model: \n" + model);
            this.oscoAdapter.getSender().publishModelUpdate(model, UUID.randomUUID().toString(), IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);



    }


    private TopologyRequest prepareTopology() {
        TopologyRequest topologyRequest = new TopologyRequest();
        Resource topoResource =  getTopologyResource();
        topologyRequest.setName(topoResource.getLocalName());
        List<String> locations = getTopologyLocations();
        topologyRequest.setLocations(locations);

        List<ServiceContainerRequest> serviceContainerRequests = new LinkedList<>();
        StmtIterator stmtIterator = topoResource.listProperties(Omn.hasResource);
        while (stmtIterator.hasNext()){
            Statement statement = stmtIterator.nextStatement();
            Resource service = newInstanceModel.getResource(statement.getObject().asResource().getURI());
            ServiceContainerRequest serviceContainerRequest = new ServiceContainerRequest();
            Resource location = service.getProperty(Omn_resource.hasLocation).getObject().asResource();
            addServiceLocations(serviceContainerRequest, location);
            Datacenter requestedDatacenter = getDatacenter(location);
            if(requestedDatacenter != null){
                DatacenterConfig  datacenterConfig = oscoclientConfig.getDatacenterConfig(requestedDatacenter.getName());
                serviceContainerRequest.setFlavour(datacenterConfig.getFlavor());
                serviceContainerRequest.setMaxNumInst(1);
                serviceContainerRequest.setMinNumInst(1);
                addImages(serviceContainerRequest, datacenterConfig);
                addSubnets(serviceContainerRequest, requestedDatacenter, datacenterConfig);
                serviceContainerRequest.setContainerName(service.getLocalName());
                addServices(service, serviceContainerRequest);
                serviceContainerRequests.add(serviceContainerRequest);
                topologyRequest.setServiceContainers(serviceContainerRequests);
            }

        }
         return topologyRequest;
    }

    private void addServices(Resource service, ServiceContainerRequest serviceContainerRequest) {
        List<ServiceRequest> services = new LinkedList<>();
        ServiceRequest serviceRequest = new ServiceRequest();
        String serviceName = getServiceName(service);
        serviceRequest.setServiceType(serviceName);
        serviceRequest.setInstanceName(service.getProperty(RDFS.label).getObject().asLiteral().getString());
        addConfiguration(service, serviceRequest);
        serviceRequest.setRequires(getRequirements(service));
        services.add(serviceRequest);
        serviceContainerRequest.setServices(services);
    }

    private LinkedList<String> getRequirements(Resource service) {
        LinkedList<String> requirements = new LinkedList<>();
        if(service.hasProperty(Osco.requirement)){
            requirements.add(service.getProperty(Osco.requirement).getObject().asLiteral().getString());
        }
        return requirements;
    }

    private String getServiceName(Resource service) {
        StmtIterator stmtIterator  = service.listProperties(RDF.type);
        String name = "";
        while(stmtIterator.hasNext()){
            Statement statement =  stmtIterator.nextStatement();
            if(statement.getObject().equals(Omn_resource.Node)){
                LOGGER.log(Level.INFO, "Ignoring Node type");
            }else{
                name = statement.getObject().asResource().getLocalName();
            }
        }
        return name;
    }

    private void addConfiguration(Resource service, ServiceRequest serviceRequest) {
        List<ParameterRequest> parameters = new LinkedList<>();
        StmtIterator propertyIterator = service.listProperties();
        while(propertyIterator.hasNext()){
           Statement propertyStatement = propertyIterator.nextStatement();
            Property property = propertyStatement.getPredicate();
            String namespace = property.getNameSpace();
            if(property.getNameSpace().equalsIgnoreCase(Osco.NS) && !property.getLocalName().equalsIgnoreCase("requirement")){
                ParameterRequest parameterRequest = new ParameterRequest();
                parameterRequest.setConfig_key(property.getLocalName());
                parameterRequest.setConfig_value(propertyStatement.getObject().asLiteral().getString());
                parameters.add(parameterRequest);
            }


        }
        ConfigurationRequest configuration = new ConfigurationRequest();
        configuration.setParameters(parameters);
        serviceRequest.setConfiguration(configuration);
    }

    private void addSubnets(ServiceContainerRequest serviceContainerRequest, Datacenter requestedDatacenter, DatacenterConfig datacenterConfig) {
        SubnetWrapper subnetWrapper =  new SubnetWrapper();
        SubnetRequest subnet = new SubnetRequest();
        subnet.setName(getSubnetName(datacenterConfig,requestedDatacenter));
        subnet.setFloatingIp("random");
        subnet.setMgmt(true);
        List<SubnetRequest> subnets = new LinkedList<>();
        subnets.add(subnet);
        subnetWrapper.setSubnets(subnets);
        HashMap<String, SubnetWrapper> subnetWrapperHashMap = new HashMap<>();
        subnetWrapperHashMap.put(datacenterConfig.getName(),subnetWrapper);
        serviceContainerRequest.setSubnets(subnetWrapperHashMap);
    }

    private void addImages(ServiceContainerRequest serviceContainerRequest, DatacenterConfig datacenterConfig) {
        HashMap<String, String > images = new HashMap<>();
        images.put(datacenterConfig.getName(),datacenterConfig.getImageId());
        serviceContainerRequest.setImages(images);
    }

    private void addServiceLocations(ServiceContainerRequest serviceContainerRequest, Resource location) {
        List<String> serviceLocations = new LinkedList<>();
        if(location.hasProperty(RDFS.label)){
            serviceLocations.add(location.getProperty(RDFS.label).getObject().asLiteral().getString());
        }else {
            serviceLocations.add(findLocation(location));
        }

        serviceContainerRequest.setLocations(serviceLocations);
    }

    private Datacenter getDatacenter(Resource location) {

        String lat = location.getProperty(Wgs84.lat).getObject().asLiteral().getString();
        String lng = location.getProperty(Wgs84.long_).getObject().asLiteral().getString();
        Datacenter datacenter = null;
        for(Datacenter dc: datacenters){
            Location datacenterLocation = dc.getLocation();
            String dcLocationLat = String.valueOf(datacenterLocation.getLatitude());
            String dcLocationLong = String.valueOf(datacenterLocation.getLongitude());
            if(dcLocationLat.equalsIgnoreCase(lat) && dcLocationLong.equalsIgnoreCase(lng)){
                datacenter = dc;
                break;
            }
        }
        return datacenter;

    }

    private List<String> getTopologyLocations() {
        List<String> locations = new ArrayList<>();
        ResIterator resIterator = newInstanceModel.listResourcesWithProperty(RDF.type, Omn_resource.Location);
        while(resIterator.hasNext()){
            Resource location = resIterator.nextResource();
            if(location.hasProperty(RDFS.label)){
                locations.add(location.getProperty(RDFS.label).getObject().asLiteral().getString());
            }else{
                String locationName = findLocation(location);
                locations.add(locationName);
            }




        }
        return locations;
    }

    private String findLocation(Resource location) {
        Datacenter dc = getDatacenter(location);
        return dc.getLocation().getName();
    }

    private Resource getTopologyResource() {
        Resource resource = null;
        if(newInstanceModel.contains(null, RDF.type, Omn.Topology)){
            ResIterator resIterator = newInstanceModel.listResourcesWithProperty(RDF.type,Omn.Topology);
            while(resIterator.hasNext()){
                resource =  resIterator.nextResource();
                break;

            }

        }else{
            LOGGER.log(Level.SEVERE, "No Topology found in request");
            throw new RuntimeException("No Topology found in request");
        }
        return resource;
    }

    public void addDatacenters(List<Datacenter> datacenters) {
        this.datacenters = datacenters;
    }

    public void addDatacenterConfig(OscoclientConfigObject config) {
        this.oscoclientConfig = config;
    }

    public String getSubnetName(DatacenterConfig datacenterConfig, Datacenter requestedDatacenter) {
        List<Subnet> subnets = requestedDatacenter.getSubnets();
        String name = "";
        for(Subnet subnet : subnets){
            if(subnet.getId().equalsIgnoreCase(datacenterConfig.getSubnetId())){
                name =subnet.getName();
                break;
            }
        }
        return name;
    }
}
