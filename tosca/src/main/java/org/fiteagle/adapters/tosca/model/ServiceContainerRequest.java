package org.fiteagle.adapters.tosca.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class ServiceContainerRequest {
    private List<ServiceRequest> services;
    private List<String> locations;
    private String flavour;
    private int maxNumInst  ;
    private int minNumInst;
    private HashMap<String, String> images;
    private HashMap<String, SubnetWrapper> subnets;

    public String getContainerName() {
        return containerName;
    }

    public HashMap<String, SubnetWrapper> getSubnets() {
        return subnets;
    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public int getMinNumInst() {
        return minNumInst;
    }

    public int getMaxNumInst() {
        return maxNumInst;
    }

    public String getFlavour() {
        return flavour;
    }

    public List<String> getLocations() {
        return locations;
    }

    public List<ServiceRequest> getServices() {
        return services;
    }

    private String containerName;

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public void setMaxNumInst(int maxNumInst) {
        this.maxNumInst = maxNumInst;
    }

    public void setMinNumInst(int minNumInst) {
        this.minNumInst = minNumInst;
    }

    public void setImages(HashMap<String,String> images) {
        this.images = images;
    }

    public void setSubnets(HashMap<String,SubnetWrapper> subnets) {
        this.subnets = subnets;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public void setServices(List<ServiceRequest> services) {
        this.services = services;
    }
}
