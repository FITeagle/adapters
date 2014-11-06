package org.fiteagle.adapters.openstack;

public class Image {
  
  private String name;
  private String id;
  
  public Image(String name, String id){
    this.setName(name);
    this.setId(id);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
}
