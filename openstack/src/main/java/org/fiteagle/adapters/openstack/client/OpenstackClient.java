package org.fiteagle.adapters.openstack.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.client.model.Flavors;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Servers;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Network;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;

public class OpenstackClient implements IOpenstackClient,Closeable{

    private final OpenstackAdapter openStackAdapter;
    private String DEFAULT_KEYPAIR_ID ;
	private String DEFAULT_FLAVOR_ID  ;
	private String DEFAULT_IMAGE_ID ;
	private static Logger LOGGER = Logger.getLogger(OpenstackClient.class.toString());
  
	private String KEYSTONE_AUTH_URL;
	private String KEYSTONE_USERNAME;
	private String KEYSTONE_PASSWORD;
	private String KEYSTONE_ENDPOINT;
	private String TENANT_NAME;
	private String NOVA_ENDPOINT;
	private String GLANCE_ENDPOINT;
	private String NET_ENDPOINT;
	private String FLOATINGIP_POOL_NAME;
	private String NET_NAME;
	private String TENANT_ID = "";
	private String networkId = "";
	private String DEFAULT_REGION ="";

	private NovaApi novaApi;
    private Set<String> regions;
    private NeutronApi neutronApi;
    
	boolean PREFERENCES_INITIALIZED = false;

    
	public OpenstackClient(OpenstackAdapter openstackAdapter) {
        this.openStackAdapter = openstackAdapter;
        NET_NAME = "trescimo-net";
       }	
	

	private void init(){
        
		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
		if(KEYSTONE_AUTH_URL != null){
			novaApi = ContextBuilder.newBuilder("openstack-nova")
	                .endpoint(KEYSTONE_AUTH_URL)
	                .credentials(TENANT_NAME + ":" + KEYSTONE_USERNAME, KEYSTONE_PASSWORD)
	                .modules(modules)
	                .buildApi(NovaApi.class);
			
			neutronApi = ContextBuilder.newBuilder("openstack-neutron")
	                .endpoint(KEYSTONE_AUTH_URL)
	                .credentials(TENANT_NAME + ":" + KEYSTONE_USERNAME, KEYSTONE_PASSWORD)
	                .modules(modules)
	                .buildApi(NeutronApi.class);
		}
		
		
		if(DEFAULT_REGION == null || DEFAULT_REGION.equals("")){
			regions = novaApi.getConfiguredRegions();
			LOGGER.log(Level.INFO, "Default Region is Empty. Setting first one from "+ regions.toString());
	        DEFAULT_REGION = regions.iterator().next();

		}
}

	@Override
	public Flavors listFlavors() {
		if(novaApi == null){
			init();
		}

		List<Flavor> flavorList = new ArrayList<Flavor> ();
        try{
            	FlavorApi flavorApi = novaApi.getFlavorApi(DEFAULT_REGION);                
                flavorList= flavorApi.listInDetail().concat().toList();
        }catch(Exception e){
        e.printStackTrace();	
        }
		
		
        return new Flavors(flavorList);

	}

	@Override
	public Images listImages() {
		if(novaApi == null){
			init();
		}

		List<Image> imagesList = new ArrayList<Image>();
        try{
            	ImageApi imageApi = novaApi.getImageApi(DEFAULT_REGION);                
                imagesList =imageApi.listInDetail().concat().toList();
        }catch(Exception e){
        e.printStackTrace();	
        }
		
        return new Images(imagesList);
	}
	
	
	
  public void getMaxInstances() {
     
  }
	
	
	@Override
	public Servers listServers() {
		if(novaApi == null){
			init();
		}

		List<Server> serverList = new ArrayList<Server>();
        try{
            	ServerApi serverApi = novaApi.getServerApi(DEFAULT_REGION);                
                serverList = serverApi.listInDetail().concat().toList();
        }catch(Exception e){
        e.printStackTrace();	
        }
		
        return new Servers(serverList);
	}
	
	private void getAccessWithTenantName() throws InsufficientOpenstackPreferences{
		LOGGER.log(Level.SEVERE, "STARTING INIT");
        
        String provider ="openstack-nova";
		
		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
		if(KEYSTONE_AUTH_URL != null){
			novaApi = ContextBuilder.newBuilder(provider)
	                .endpoint(KEYSTONE_AUTH_URL)
	                .credentials(TENANT_NAME + ":" + KEYSTONE_USERNAME, KEYSTONE_PASSWORD)
	                .modules(modules)
	                .buildApi(NovaApi.class);
		}
		
        regions = novaApi.getConfiguredRegions();
	}
	
	@Override
	public ServerCreated createServer(String name,String imageId,String flavorId,CreateServerOptions options) {
		if(novaApi == null || neutronApi == null){
			init();
			}
		
	ServerApi serverApi = novaApi.getServerApi(DEFAULT_REGION);
    if(!networkId.equals("")){
        options.networks(networkId);
    }else{
    	if(NET_NAME != null){
        options.networks(getNetworkIdByName(NET_NAME));
    }else{
        options.networks(getNetworkIdByName("trescimo-net"));
    }
    }
    ServerCreated serverCreated = serverApi.create(name, imageId, flavorId, options);
	return serverCreated;
}

	private void setDefaultValues() {
	}

	public Servers getAllServersDetails(){
		if(novaApi == null){
			init();
			}
			
			ServerApi serverApi = novaApi.getServerApi(DEFAULT_REGION);
			List<Server> serverList = serverApi.listInDetail().concat().toList();
			
			return new Servers(serverList);
			
	}
	
	@Override
	public Server getServerDetails(String id) {
	if(novaApi == null){
	init();
	}
	
	ServerApi serverApi = novaApi.getServerApi(DEFAULT_REGION);
	Server server = serverApi.get(id);
	return server;
	
	}

	@Override
	public void allocateFloatingIpForServer(String serverId, String floatingIp) {
	}

	@Override
	public void getFloatingIpPools(){
	}
	
	@Override
	public void addFloatingIp(){
		
	}

	@Override
	public List<FloatingIP> listFreeFloatingIps() {
		if(novaApi == null){
			init();
		}
		List<FloatingIP> floatingIpList = new ArrayList<>();
				
        try{
            	FloatingIPApi floatingApi = novaApi.getFloatingIPApi(DEFAULT_REGION).get();       
            	floatingIpList = floatingApi.list().toList();
        }catch(Exception e){
        e.printStackTrace();	
        }
		
        return floatingIpList;
	}

	@Override
	public void addKeyPair(String name, String publicKey, String tmpRegion){
		if(novaApi == null){
			init();
		}
			KeyPairApi keypairApi = novaApi.getKeyPairApi(tmpRegion).get();
			keypairApi.createWithPublicKey(name, publicKey);
	}
	
	@Override
	public void addKeyPair(String name, String publicKey) {
		if(novaApi == null){
			init();
		}
			KeyPairApi keypairApi = novaApi.getKeyPairApi(DEFAULT_REGION).get();
			keypairApi.createWithPublicKey(name, publicKey);		
	}
	
	@Override
	public void deleteKeyPair(String name,String tmpRegion){
		if(novaApi == null){
			init();
		}
			KeyPairApi keypairApi = novaApi.getKeyPairApi(tmpRegion).get();
			keypairApi.delete(name);
	}


	@Override
	public void deleteServer(String id){
	}

	public String getNetworkId() {
		if(neutronApi == null){
			init();
		}
		
		if(this.networkId==null || this.networkId.compareTo("")==0)
			this.setNetworkId(getNetworkIdByName(NET_NAME));

		return networkId;
	}

	private String getNetworkIdByName(String networkName) {
		if(neutronApi == null){
			init();
		}
		Iterator<Network> netIterator = neutronApi.getNetworkApi(DEFAULT_REGION).list().concat().iterator();

		while(netIterator.hasNext()){
		Network net = netIterator.next();
		if(net.getName().equals(networkName)){
			return net.getId();
		}
		}
		
		return null;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getKEYSTONE_ENDPOINT() {
		return KEYSTONE_ENDPOINT;
	}

	public String getGLANCE_ENDPOINT() {
		return GLANCE_ENDPOINT;
	}
	
	
  public static class InsufficientOpenstackPreferences extends RuntimeException {
    
    private static final long serialVersionUID = 6511540487288262809L;

    public InsufficientOpenstackPreferences(String preferenceName) {
      super("Please set the preference: "+preferenceName);
    }
  }


@Override
public void close() throws IOException {
	// TODO Auto-generated method stub
    Closeables.close(novaApi, true);
    Closeables.close(neutronApi, true);
}


}


