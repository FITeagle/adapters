package org.fiteagle.adapters.tosca.model;

import java.util.List;

/**
 * Created by dne on 29.10.15.
 */
public class ConfigurationRequest {
    private List<ParameterRequest> parameters;

    public void setParameters(List<ParameterRequest> parameters) {
        this.parameters = parameters;
    }

    public List<ParameterRequest> getParameters() {
        return parameters;
    }
}
