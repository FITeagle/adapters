package org.fiteagle.adapters.tosca.model;

import java.util.LinkedList;

/**
 * Created by dne on 29.10.15.
 */
public class ServiceRequest {
    private String serviceType;
    private String instanceName;
    private ConfigurationRequest configuration;
    private LinkedList<String> requires;

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }


    public void setConfiguration(ConfigurationRequest configuration) {
            this.configuration = configuration;
    }


    public String getServiceType() {
        return serviceType;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public ConfigurationRequest getConfiguration() {
        return configuration;
    }

    public LinkedList<String> getRequires() {
        return requires;
    }

    public void setRequires(LinkedList<String> requires) {

        this.requires = requires;
    }
}
