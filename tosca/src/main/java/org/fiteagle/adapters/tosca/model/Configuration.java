package org.fiteagle.adapters.tosca.model;

import java.util.List;

/**
 * Created by dne on 25.09.15.
 */
public class Configuration {

    private String id;
    private int version;
    private List<Parameter> parameters;
    private String configurationName;

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

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }
}
