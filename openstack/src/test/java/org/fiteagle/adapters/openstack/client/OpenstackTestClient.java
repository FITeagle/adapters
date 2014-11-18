package org.fiteagle.adapters.openstack.client;

import java.util.ArrayList;

import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;
import com.woorea.openstack.nova.model.FloatingIpPools;

public class OpenstackTestClient implements IOpenstackClient {
  
  private static OpenstackTestClient instance;
  
  public static OpenstackTestClient getInstance(){
    if(instance == null){
      instance = new OpenstackTestClient();
    }
    return instance;
  }
  
  @Override
  public Flavors listFlavors() {
    return null;
  }

  @Override
  public Images listImages() {
    ArrayList<Image> imageList = new ArrayList<>();
    imageList.add(new Image("12345", "testImageName"));
    Images images = new Images(imageList);
    return images;
  }
  
  @Override
  public Servers listServers() {
    return null;
  }
  
  @Override
  public Server createServer(ServerForCreate serverForCreate) {
    Server server = new Server();
    Image image = new Image("12345", serverForCreate.getImageRef());
    server.setKeyName(serverForCreate.getKeyName());
    server.setImage(image);
    return server;
  }

  @Override
  public Server getServerDetails(String id) {
    return null;
  }

  @Override
  public void allocateFloatingIpForServer(String serverId, String floatingIp) {
   
  }

  @Override
  public FloatingIpPools getFloatingIpPools(){
    return null;
  }
  
  @Override
  public FloatingIp addFloatingIp(){
    return null;
  }
  
  @Override
  public void addKeyPair(String name, String publicKey){
    
  }

  @Override
  public void deleteKeyPair(String name){
    
  }
  
  @Override
  public void deleteServer(String id){
    
  }

}
