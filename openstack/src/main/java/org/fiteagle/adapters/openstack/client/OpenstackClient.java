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
import org.jclouds.openstack.nova.v2_0.domain.FloatingIPPool;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPPoolApi;
//import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;

import com.google.common.base.Optional;
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
//        NET_NAME = "trescimo-net";
       }	
	
	
	private void loadPreferences() {


		try{
		if (openStackAdapter.getFloatingPool() != null){
		  FLOATINGIP_POOL_NAME = openStackAdapter.getFloatingPool() ;
		}
		if (openStackAdapter.getKeystone_auth_URL() != null){
		  KEYSTONE_AUTH_URL = openStackAdapter.getKeystone_auth_URL();
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_auth_URL");
		}
		if (openStackAdapter.getKeystone_endpoint() != null){
		  KEYSTONE_ENDPOINT = openStackAdapter.getKeystone_endpoint();
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_endpoint");
		}
		if (openStackAdapter.getKeystone_password() != null){
		  KEYSTONE_PASSWORD = openStackAdapter.getKeystone_password();
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_password");
		}
		if (openStackAdapter.getKeystone_username() != null){
		  KEYSTONE_USERNAME = openStackAdapter.getKeystone_username() ;
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_username");
		}
		if (openStackAdapter.getNet_endpoint() != null){
		  NET_ENDPOINT = openStackAdapter.getNet_endpoint();
		}
		else{
		  throw new InsufficientOpenstackPreferences("net_endpoint");
		}
		if (openStackAdapter.getNet_name() != null){
		  NET_NAME = openStackAdapter.getNet_name();
		}
		else{
		  throw new InsufficientOpenstackPreferences("net_name");
		}
		if (openStackAdapter.getNova_endpoint() != null){
		  NOVA_ENDPOINT = openStackAdapter.getNova_endpoint();
		}
		else{
		  throw new InsufficientOpenstackPreferences("nova_endpoint");
		}
		if (openStackAdapter.getTenant_name() != null){
		  TENANT_NAME =openStackAdapter.getTenant_name();
		}
		else{
		  throw new InsufficientOpenstackPreferences("tenant_name");
		}

		if(openStackAdapter.getDefault_image_id()!= null){
			DEFAULT_IMAGE_ID = openStackAdapter.getDefault_image_id();
		}else{
			throw new InsufficientOpenstackPreferences("default_image_id");
		}

		if(openStackAdapter.getDefault_flavor_id()!= null){
			DEFAULT_FLAVOR_ID = openStackAdapter.getDefault_flavor_id();
		}else{
			throw new InsufficientOpenstackPreferences("default_flavor_id");
		}
		
		if(openStackAdapter.getDefault_region()!= null){
			DEFAULT_REGION = openStackAdapter.getDefault_region();
		}else{
			throw new InsufficientOpenstackPreferences("default_region");
		}
		PREFERENCES_INITIALIZED = true;
		
		}catch (IllegalArgumentException e){		
		LOGGER.log(Level.SEVERE, "IllegalArgumentException - Was not able to copy Propertys from Adapter");
		}catch (InsufficientOpenstackPreferences e){
		LOGGER.log(Level.SEVERE, "InsufficientOpenstackPreferences - Was not able to copy Propertys from Adapter");
		}
	}
	

	private void init(){
		LOGGER.info("init client");
        if(PREFERENCES_INITIALIZED){
    		Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
    		if(KEYSTONE_AUTH_URL != null){
				LOGGER.info("creating novaApiObject");
    			novaApi = ContextBuilder.newBuilder("openstack-nova")
    	                .endpoint(KEYSTONE_AUTH_URL)
    	                .credentials(TENANT_NAME + ":" + KEYSTONE_USERNAME, KEYSTONE_PASSWORD)
    	                .modules(modules)
    	                .buildApi(NovaApi.class);
    			LOGGER.info("creating neutronAPIObject");
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
        }else{
        	loadPreferences();
        	
        	 if(PREFERENCES_INITIALIZED){
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
					LOGGER.info("Default region is now: " + DEFAULT_REGION);

         		}
             }
        }
}

	@Override
	public Flavors listFlavors() {
		if(novaApi == null){
			init();
		}

		List<Flavor> flavorList = new ArrayList<Flavor> ();
        try{
			    LOGGER.info("listing flavors");
            	FlavorApi flavorApi = novaApi.getFlavorApi(DEFAULT_REGION);                
                flavorList= flavorApi.listInDetail().concat().toList();
        }catch(Exception e){
    		LOGGER.log(Level.SEVERE, e.getStackTrace().toString());	
        }
		LOGGER.info("return flavor list with " + flavorList.size() + " entries");
		
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
    		LOGGER.log(Level.SEVERE, e.getStackTrace().toString());	
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
    		LOGGER.log(Level.WARNING, "Exception in listServers");
    		LOGGER.log(Level.SEVERE, e.getStackTrace().toString());	
        }
		
        return new Servers(serverList);
	}
	
	private void getAccessWithTenantName() throws InsufficientOpenstackPreferences{
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
    	options.networks(getNetworkIdByName(listAllNetworks()));
		}
    }
    ServerCreated serverCreated = serverApi.create(name, imageId, flavorId, options);
	return serverCreated;
}

	private String listAllNetworks() {
		if(neutronApi == null){
			init();
			}
		String result = neutronApi.getNetworkApi(DEFAULT_REGION).list().get(0).get(0).getName();
		LOGGER.log(Level.WARNING, "No Network-Name found in Config. Taking the first one from Server instead! -> "+result);

		return result;
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
	if(novaApi==null ){
		init();
	}
	FloatingIPApi api = novaApi.getFloatingIPApi(DEFAULT_REGION).get();
	api.addToServer(floatingIp, serverId);
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
        	@SuppressWarnings("deprecation")
			Optional<? extends FloatingIPApi> floatingApiOptional = novaApi.getFloatingIPApi(DEFAULT_REGION);
        	Optional<org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi> floatingNeutronOptional = neutronApi.getFloatingIPApi(DEFAULT_REGION);
//        	 Optional<org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi> neutronFloatingAPI = neutronApi.getFloatingIPApi(DEFAULT_REGION);

        	FloatingIPApi floatingApi = null;
        	org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi floatingNeutronIpApi = null;
        	//org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi neutronApi = null;

        	if(floatingApiOptional.isPresent()){
        	floatingApi = 	floatingApiOptional.get();
        	}else{
        		LOGGER.log(Level.SEVERE, "FloatingIP-API is null");
        	}
        	if(floatingNeutronOptional.isPresent()){
        		floatingNeutronIpApi = 	floatingNeutronOptional.get();
        	}else{
        		LOGGER.log(Level.SEVERE, "FloatingIP-API is null");
        	}

        	floatingIpList = floatingApi.list().toList();
    		List<FloatingIP> resultList = new ArrayList<>();

        	for (FloatingIP floatIp : floatingIpList){
        		if(floatIp.getFixedIp() == null){
        			resultList.add(floatIp);
        		}
        	}
        	
        	if(resultList.isEmpty()){
        		LOGGER.log(Level.SEVERE, "NO FLOATING IP FOUND");
            	return null;
            }
        	return resultList;
        }catch(Exception e){
    		e.printStackTrace();	
        }
		LOGGER.log(Level.SEVERE, "EXCEPTION IN FLOATING IP");
		return null;
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
	public boolean deleteServer(String id){
	if(novaApi == null){
		init();
	}
	
	ServerApi serverApi = novaApi.getServerApi(DEFAULT_REGION);
	
	if(serverApi.delete(id)){
		return true;
	}else return false;
	
	
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
		
		return "NO NETWORK FOUND";
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


