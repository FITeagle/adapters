package org.fiteagle.adapters.tosca.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class ServiceContainerResponse {
    private String id;
    private int version;
    private String flavour;
    private int minNumInst;
    private int maxNumInst;
    private String serviceType;
    private List<String> locations;
    List<RelationElement> relationElements;
    private HashMap<String, SubnetWrapperResponse> subnets;
    private int runningUnits;
    private String containerName;
    private String state;
    private List<String> subnetNames;


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

    public String getFlavour() {
        return flavour;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public int getMinNumInst() {
        return minNumInst;
    }

    public void setMinNumInst(int minNumInst) {
        this.minNumInst = minNumInst;
    }

    public int getMaxNumInst() {
        return maxNumInst;
    }

    public void setMaxNumInst(int maxNumInst) {
        this.maxNumInst = maxNumInst;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<RelationElement> getRelationElements() {
        return relationElements;
    }

    public void setRelationElements(List<RelationElement> relationElements) {
        this.relationElements = relationElements;
    }

    public HashMap<String, SubnetWrapperResponse> getSubnets() {
        return subnets;
    }

    public void setSubnets(HashMap<String,SubnetWrapperResponse> subnets) {
        this.subnets = subnets;
    }

    public int getRunningUnits() {
        return runningUnits;
    }

    public void setRunningUnits(int runningUnits) {
        this.runningUnits = runningUnits;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getSubnetNames() {
        return subnetNames;
    }

    public void setSubnetNames(List<String> subnetNames) {
        this.subnetNames = subnetNames;
    }


}
