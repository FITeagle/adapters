package org.fiteagle.adapters.tosca.model;

import java.util.HashMap;

/**
 * Created by dne on 29.10.15.
 */
public class Unit {

    private String id;
    private int version;
    private HashMap<String,String> ips;
    private HashMap<String,String> floatingIps;
    private String state;
    private String message;
    private long upTime;
    private String extId;
    private String hostname;
    private int seqNumber;
    private String datacenterId;

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

    public HashMap<String, String> getIps() {
        return ips;
    }

    public void setIps(HashMap<String, String> ips) {
        this.ips = ips;
    }

    public HashMap<String, String> getFloatingIps() {
        return floatingIps;
    }

    public void setFloatingIps(HashMap<String, String> floatingIps) {
        this.floatingIps = floatingIps;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public String getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(String datacenterId) {
        this.datacenterId = datacenterId;
    }
}
