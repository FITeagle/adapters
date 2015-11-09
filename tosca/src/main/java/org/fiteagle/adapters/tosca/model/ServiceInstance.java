package org.fiteagle.adapters.tosca.model;

import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class ServiceInstance {
    private String id;
    private int version;
    private Configuration configuration;
    private String flavour;
    private int minNumInst;
    private int maxNumInst;
    private String serviceType;
    private String instanceName;
    private String state;

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getMaxNumInst() {
        return maxNumInst;
    }

    public void setMaxNumInst(int maxNumInst) {
        this.maxNumInst = maxNumInst;
    }

    public int getMinNumInst() {
        return minNumInst;
    }

    public void setMinNumInst(int minNumInst) {
        this.minNumInst = minNumInst;
    }

    public String getFlavour() {
        return flavour;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private List<String> policies;
}
