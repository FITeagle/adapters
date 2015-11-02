package org.fiteagle.adapters.tosca.model;

/**
 * Created by dne on 29.10.15.
 */
public class ParameterRequest {
    private String config_value;

    public String getConfig_key() {
        return config_key;
    }

    public String getConfig_value() {
        return config_value;
    }

    private String config_key;

    public void setConfig_key(String config_key) {
        this.config_key = config_key;
    }

    public void setConfig_value(String config_value) {
        this.config_value = config_value;
    }
}
