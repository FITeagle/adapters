package org.fiteagle.adapters.tosca.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.fiteagle.adapters.tosca.OscoclientConfigObject;
import org.fiteagle.adapters.tosca.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by dne on 25.09.15.
 */
public class OrchestratorClient implements IOrchestratorClient {

    private static final String URL_ADMIN_DATACENTERS = "/datacenters";
    private static final String URL_ORCHESTRATOR_TOPOLOGIES = "/topologies";
    private final String URL_ORCHESTRATOR_SERVICES = "/services";

    private final String url;

    public OscoclientConfigObject getConfig() {
        return config;
    }

    private final OscoclientConfigObject config;

    private Logger LOGGER  = LoggerFactory.getLogger(this.getClass().getName());
    private String orchestratorEndpoint;
    private String adminEndpoint;

    public OrchestratorClient(String url){
        this.url = url;
        this.config = new OscoclientConfigObject();
    }

    public OrchestratorClient(OscoclientConfigObject oscoclientConfigObject) {
        this.config = oscoclientConfigObject;
        url = oscoclientConfigObject.getEndpoint();
        this.orchestratorEndpoint = oscoclientConfigObject.getOrchestratorEndpoint();
        this.adminEndpoint = oscoclientConfigObject.getAdminEndpoint();
    }


    public List<Service> getServices() {
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        List<Service> serviceList = client.target(this.url+this.orchestratorEndpoint + URL_ORCHESTRATOR_SERVICES).request().get(new GenericType<List<Service>>(){});

        return serviceList;
    }

    public Service getService(String id){
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        Service service = client.target(this.url+orchestratorEndpoint+URL_ORCHESTRATOR_SERVICES+"/"+id).request().get(Service.class);
        return service;
    }

    public List<TopologyResponse> getTopologies(){
            Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
            List<TopologyResponse> topologies = client.target(this.url+this.orchestratorEndpoint+URL_ORCHESTRATOR_TOPOLOGIES).request().get(new GenericType<List<TopologyResponse>>(){});
            return  topologies;
    }

    public TopologyResponse getTopology(String id){
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        TopologyResponse topologyResponse = client.target(this.url+this.orchestratorEndpoint+URL_ORCHESTRATOR_TOPOLOGIES+ "/" + id).request().get(TopologyResponse.class);
        return topologyResponse;
    }

    public TopologyResponse createTopology(TopologyRequest topologyRequest){
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        TopologyResponse response = client.target(this.url + this.orchestratorEndpoint + URL_ORCHESTRATOR_TOPOLOGIES).request().post(Entity.entity(topologyRequest, MediaType.APPLICATION_JSON), TopologyResponse.class);
        return response;
    }


    public List<Datacenter> getDataCenters(){
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        List<Datacenter> datacenterList = client.target(this.url +this.adminEndpoint +URL_ADMIN_DATACENTERS).request().get(new GenericType<List<Datacenter>>(){});
        return datacenterList;
    }

    public void deleteTopology(String id){
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        client.target(this.url + this.orchestratorEndpoint + URL_ORCHESTRATOR_TOPOLOGIES+ "/" +id).request().delete();
    }

    public void setOrchestratorEndpoint(String orchestratorEndpoint) {
        this.orchestratorEndpoint = orchestratorEndpoint;
    }

    public void setAdminEndpoint(String adminEndpoint) {
        this.adminEndpoint = adminEndpoint;
    }
}
