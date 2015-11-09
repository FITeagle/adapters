package org.fiteagle.adapters.tosca.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Created by dne on 15.10.15.
 */
public class Datacenter {

    private String id;
    private int version;
    private String name;
    private Location location;

    private String type;

    private Configuration configuration;

    private List<Subnet> subnets;

    private Map<String,String> serviceImageId;

    @JsonProperty("switch")
    private Switch switchJson;


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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<Subnet> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<Subnet> subnets) {
        this.subnets = subnets;
    }

    public Map<String, String> getServiceImageId() {
        return serviceImageId;
    }

    public void setServiceImageId(Map<String, String> serviceImageId) {
        this.serviceImageId = serviceImageId;
    }

    public Switch getSwitchJson() {
        return switchJson;
    }

    public void setSwitchJson(Switch switchJson) {
        this.switchJson = switchJson;
    }
}
