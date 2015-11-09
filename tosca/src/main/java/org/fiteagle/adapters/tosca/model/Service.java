package org.fiteagle.adapters.tosca.model;

import java.util.LinkedList;

/**
 * Created by dne on 25.09.15.
 */
public class Service {

    private String id;
    private int version;
    private Configuration configuration;
    private String flavour;
    private int minNumInst;
    private int maxNumInst;
    private String serviceType;
    private String instanceName;
    private LinkedList<Object> requires;

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

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
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

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {

        this.instanceName = instanceName;
    }

    public void setRequires(LinkedList<Object> requires) {
        this.requires = requires;
    }
}
