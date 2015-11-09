package org.fiteagle.adapters.tosca.model;

import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class TopologyRequest {
    private List<ServiceContainerRequest> serviceContainers;
    private String name;

    public List<String> getLocations() {
        return locations;
    }

    public String getName() {
        return name;
    }

    public List<ServiceContainerRequest> getServiceContainers() {
        return serviceContainers;
    }

    private List<String> locations;

    public void setName(String name) {
        this.name = name;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void setServiceContainers(List<ServiceContainerRequest> serviceContainers) {
        this.serviceContainers = serviceContainers;
    }
}
