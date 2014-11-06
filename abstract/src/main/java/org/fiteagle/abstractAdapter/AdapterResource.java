package org.fiteagle.abstractAdapter;

import java.util.Map;

import com.hp.hpl.jena.rdf.model.Property;

public class AdapterResource {
  
  private String name;
  private Map<Property, Object> properties;
  
  public AdapterResource(String name, Map<Property, Object> properties){
    this.name = name;
    this.properties = properties;
  }
  
  public Object getProperty(Property key){
    return properties.get(key);
  }
  
  public Map<Property, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<Property, Object> properties) {
    this.properties = properties;
  }

  public String getName() {
    return name;
  }
}
