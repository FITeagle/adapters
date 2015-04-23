package org.fiteagle.adapters.openstack.client;

import com.woorea.openstack.nova.model.FloatingIps;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;
import com.woorea.openstack.nova.model.FloatingIpPools;

public interface IOpenstackClient {
  
  public abstract Flavors listFlavors();
  
  public abstract Images listImages();
  
  public abstract Servers listServers();
  
  public abstract Server createServer(ServerForCreate serverForCreate);
  
  public abstract Server getServerDetails(String id);
  
  public abstract void allocateFloatingIpForServer(String serverId, String floatingIp);
  
  public abstract FloatingIpPools getFloatingIpPools();
  
  public abstract FloatingIp addFloatingIp();
  public abstract FloatingIps listFreeFloatingIps();
  
  public abstract void addKeyPair(String name, String publicKey);
  
  public abstract void deleteKeyPair(String name);
  
  public abstract void deleteServer(String id);
  
}