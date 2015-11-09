package org.fiteagle.adapters.tosca.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class TopologyResponse implements Serializable{
    private String id;
    private int version;
    private String name;
    private List<String> locations;
    private List<ServiceContainerResponse> serviceContainerResponses;
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<ServiceContainerResponse> getServiceContainers() {
        return serviceContainerResponses;
    }

    public void setServiceContainers(List<ServiceContainerResponse> serviceContainerResponses) {
        this.serviceContainerResponses = serviceContainerResponses;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
