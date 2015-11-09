package org.fiteagle.adapters.tosca;




import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.HashMap;

/**
 * Created by dne on 28.10.15.
 */
public class OscoclientConfigObject {

    private HashMap<String,DatacenterConfig> datacenterConfigHashMap;

    public String getAdminEndpoint() {
        return adminEndpoint;
    }

    private String adminEndpoint;
    private String endpoint ;

    public OscoclientConfigObject(){
        this.datacenterConfigHashMap = new HashMap<>();
    }
    public String getOrchestratorEndpoint() {
        return orchestratorEndpoint;
    }

    private String orchestratorEndpoint;

    private void addDatacenterConfig(JsonObject jsonObject) {
        DatacenterConfig datacenterConfig = new DatacenterConfig();
        datacenterConfig.setName(jsonObject.getString("name"));
        datacenterConfig.setFlavor(jsonObject.getString("flavor"));
        datacenterConfig.setImageId(jsonObject.getString("image"));
        datacenterConfig.setSubnetId(jsonObject.getString("subnet"));
        datacenterConfigHashMap.put(datacenterConfig.getName(),datacenterConfig);

    }

    public DatacenterConfig getDatacenterConfig(String name){
        return datacenterConfigHashMap.get(name);
    }
    public void addDatacenters(JsonArray datacenters) {
        for(int i = 0; i< datacenters.size(); i++){
            this.addDatacenterConfig(datacenters.getJsonObject(i));
        }
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setOrchestratorEndpoint(String orchestratorEndpoint) {
        this.orchestratorEndpoint = orchestratorEndpoint;
    }

    public void setAdminEndpoint(String adminEndpoint) {
        this.adminEndpoint = adminEndpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
