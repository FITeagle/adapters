package org.fiteagle.adapters.tosca.model;

import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class SubnetWrapper {

    private int version;
    private List<SubnetRequest> subnets;



    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<SubnetRequest> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<SubnetRequest> subnets) {
        this.subnets = subnets;
    }
}
