package org.fiteagle.adapters.tosca.model;

import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class SubnetWrapperResponse {
    private String id;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Subnet> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<Subnet> subnets) {
        this.subnets = subnets;
    }

    private int version;
    private List<Subnet> subnets;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
