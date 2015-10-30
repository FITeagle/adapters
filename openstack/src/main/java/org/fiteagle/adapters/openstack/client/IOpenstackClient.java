package org.fiteagle.adapters.openstack.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.adapters.openstack.client.model.Flavors;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Servers;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;



public interface IOpenstackClient {
  
  public abstract Flavors listFlavors();
  
  public abstract Images listImages();
  
  public abstract Servers listServers();
  
  public abstract ServerCreated createServer(String name,String imageId,String flavorId,CreateServerOptions options);
  
  public abstract Server getServerDetails(String id);
  
  public abstract void allocateFloatingIpForServer(String serverId, String floatingIp);
  
  public abstract void getFloatingIpPools();
  
  public abstract void addFloatingIp();
  
  public abstract List<FloatingIP> listFreeFloatingIps();
  
  public abstract void addKeyPair(String name, String publicKey,String tmpRegion);
  
  public abstract void addKeyPair(String name, String publicKey);

  
  public abstract void deleteKeyPair(String name,String tmpRegion);
  
  public abstract boolean deleteServer(String id);

  public abstract void close() throws IOException;

  
}