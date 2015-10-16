package org.fiteagle.adapters.tosca.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.fiteagle.adapters.tosca.model.Datacenter;
import org.fiteagle.adapters.tosca.model.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import java.util.List;

/**
 * Created by dne on 25.09.15.
 */
public class OrchestratorClient implements IOrchestratorClient {

    private static final String URL_ADMIN_DATACENTERS = "/datacenters";
    private final String URL_ORCHESTRATOR_SERVICES = "/services";
    private final String url;

    private Logger LOGGER  = LoggerFactory.getLogger(this.getClass().getName());
    private String orchestratorEndpoint;
    private String adminEndpoint;

    public OrchestratorClient(String url){
        this.url = url;
    }


    public List<Service> getServices() {
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        List<Service> serviceList = client.target(this.url+this.orchestratorEndpoint + URL_ORCHESTRATOR_SERVICES).request().get(new GenericType<List<Service>>(){});

        return serviceList;
    }


    public List<Datacenter> getDataCenters(){
        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
        List<Datacenter> datacenterList = client.target(this.url +this.adminEndpoint +URL_ADMIN_DATACENTERS).request().get(new GenericType<List<Datacenter>>(){});
        return datacenterList;
    }

    public void setOrchestratorEndpoint(String orchestratorEndpoint) {
        this.orchestratorEndpoint = orchestratorEndpoint;
    }

    public void setAdminEndpoint(String adminEndpoint) {
        this.adminEndpoint = adminEndpoint;
    }
}
