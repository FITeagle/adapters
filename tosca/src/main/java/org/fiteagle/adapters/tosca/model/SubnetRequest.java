package org.fiteagle.adapters.tosca.model;

/**
 * Created by dne on 29.10.15.
 */
public class SubnetRequest {
    private String fixedIp;
    private String floatingIp;
    private boolean mgmt;
    private String name;

    public String getFixedIp() {
        return fixedIp;
    }

    public void setFixedIp(String fixedIp) {
        this.fixedIp = fixedIp;
    }

    public String getFloatingIp() {
        return floatingIp;
    }

    public void setFloatingIp(String floatingIp) {
        this.floatingIp = floatingIp;
    }

    public boolean isMgmt() {
        return mgmt;
    }

    public void setMgmt(boolean mgmt) {
        this.mgmt = mgmt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
