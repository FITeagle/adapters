package org.fiteagle.adapters.openstack.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate.SecurityGroup;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.woorea.openstack.base.client.Entity;
import com.woorea.openstack.base.client.HttpMethod;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.connector.JerseyConnector;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.api.TokensResource;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.nova.Nova;
import com.woorea.openstack.nova.api.ServersResource.AssociateFloatingIp;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;
import com.woorea.openstack.nova.model.FloatingIpPools;
import com.woorea.openstack.nova.model.FloatingIpPools.FloatingIpPool;
import com.woorea.openstack.nova.model.ServerAction;
import com.woorea.openstack.quantum.Quantum;
import com.woorea.openstack.quantum.model.Network;
import com.woorea.openstack.quantum.model.Networks;
import org.fiteagle.api.core.Config;

public class OpenstackClient implements IOpenstackClient{

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

	public OpenstackClient() {
	}
	
	private boolean PREFERENCES_INITIALIZED = false;
	
	private void loadPreferences() {
		Config preferences = new Config("Openstack-1");
		if (preferences.getProperty("floating_ip_pool_name") != null){
		  FLOATINGIP_POOL_NAME = preferences.getProperty("floating_ip_pool_name");
		}
		if (preferences.getProperty("keystone_auth_URL") != null){
		  KEYSTONE_AUTH_URL = preferences.getProperty("keystone_auth_URL");
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_auth_URL");
		}
		if (preferences.getProperty("keystone_endpoint") != null){
		  KEYSTONE_ENDPOINT = preferences.getProperty("keystone_endpoint");
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_endpoint");
		}
		if (preferences.getProperty("keystone_password") != null){
		  KEYSTONE_PASSWORD = preferences.getProperty("keystone_password");
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_password");
		}
		if (preferences.getProperty("keystone_username") != null){
		  KEYSTONE_USERNAME = preferences.getProperty("keystone_username");
		}
		else{
		  throw new InsufficientOpenstackPreferences("keystone_username");
		}
		if (preferences.getProperty("net_endpoint") != null){
		  NET_ENDPOINT = preferences.getProperty("net_endpoint");
		}
		else{
		  throw new InsufficientOpenstackPreferences("net_endpoint");
		}
		if (preferences.getProperty("net_name") != null){
		  NET_NAME = preferences.getProperty("net_name");
		}
		else{
		  throw new InsufficientOpenstackPreferences("net_name");
		}
		if (preferences.getProperty("nova_endpoint") != null){
		  NOVA_ENDPOINT = preferences.getProperty("nova_endpoint");
		}
		else{
		  throw new InsufficientOpenstackPreferences("nova_endpoint");
		}
		if (preferences.getProperty("tenant_name") != null){
		  TENANT_NAME = preferences.getProperty("tenant_name");
		}
		else{
		  throw new InsufficientOpenstackPreferences("tenant_name");
		}
	}

	@Override
	public Flavors listFlavors() {
		Access access = getAccessWithTenantId();

		Flavors flavors = null;
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
		novaClient.token(access.getToken().getId());

		flavors = novaClient.flavors().list(true).execute();

		return flavors;
	}

	@Override
	public Images listImages() {
		Access access = getAccessWithTenantId();
		
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
		novaClient.token(access.getToken().getId());

		OpenStackRequest<String> request = new OpenStackRequest<String>(
				novaClient, HttpMethod.GET, "/images/detail", null,
				String.class);
		
		String response = null;
		try{
		  response = novaClient.execute(request);
		} catch(OpenStackResponseException e){
		  LOGGER.log(Level.SEVERE, e.getMessage());
		  return null;
		}

		Images images = OpenstackParser.parseToImages(response);
		return images;
	}
	
  public int getMaxInstances() {
      Access access = getAccessWithTenantId();
      
      Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
      novaClient.token(access.getToken().getId());
  
      OpenStackRequest<String> request = new OpenStackRequest<String>(
          novaClient, HttpMethod.GET, "/os-quota-sets/defaults", null,
          String.class);
      
      int response = 0;
      try{
        response = Integer.valueOf(novaClient.execute(request));
      } catch(OpenStackResponseException e){
        LOGGER.log(Level.SEVERE, e.getMessage());
        return 0;
      }
    
      return response;
  }
	
	
	@Override
	public Servers listServers() {
    Access access = getAccessWithTenantId();
    
    Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
    novaClient.token(access.getToken().getId());

    OpenStackRequest<String> request = new OpenStackRequest<String>(
        novaClient, HttpMethod.GET, "/servers/detail", null,
        String.class);
    
    String response = null;
    try{
      response = novaClient.execute(request);
    } catch(OpenStackResponseException e){
      LOGGER.log(Level.SEVERE, e.getMessage());
      return null;
    }
    Servers servers = OpenstackParser.parseToServers(response);
  
    return servers;
	}
	
	private Access getAccessWithTenantId() throws InsufficientOpenstackPreferences{
	  if(PREFERENCES_INITIALIZED == false){
	    loadPreferences();
	    PREFERENCES_INITIALIZED = true;
	  }
		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL,	new JerseyConnector());
		TokensResource tokens = keystone.tokens();
		UsernamePassword credentials = new UsernamePassword(KEYSTONE_USERNAME,  KEYSTONE_PASSWORD);
		Access access = tokens.authenticate(credentials).withTenantName(TENANT_NAME).execute();
		keystone.token(access.getToken().getId());

		Tenants tenants = keystone.tenants().list().execute();

		List<Tenant> tenantsList = tenants.getList();

		if (tenants.getList().size() > 0) {
			for (Iterator<Tenant> iterator = tenantsList.iterator(); iterator.hasNext();) {
				Tenant tenant = (Tenant) iterator.next();
				if (tenant.getName().compareTo(TENANT_NAME) == 0) {
					TENANT_ID = tenant.getId();
					break;
				}
			}
		} else {
			throw new RuntimeException("No tenants found!");
		}

		TokenAuthentication tokenAuth = new TokenAuthentication(access.getToken().getId());
		access = tokens.authenticate(tokenAuth).withTenantId(TENANT_ID).execute();

		return access;
	}

	public Server createServer(ServerForCreate serverForCreate) {
		Access access = getAccessWithTenantId();
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
		novaClient.token(access.getToken().getId());

		SecurityGroup sGroup = new ServerForCreate.SecurityGroup("default");
		serverForCreate.getSecurityGroups().add(sGroup);

		List<ServerForCreate.Network> networkList = serverForCreate.getNetworks();
		ServerForCreate.Network net_demo = new ServerForCreate.Network();
		net_demo.setUuid(this.getNetworkId());
		networkList.add(net_demo);

		OpenStackRequest<Server> createServerRequest = new OpenStackRequest<Server>(
				novaClient,
				HttpMethod.POST,
				"/servers",
				Entity.json(serverForCreate),
				Server.class);
		
		String serverID = novaClient.execute(createServerRequest).getId();
		return getServerDetails(serverID);
	}

	@Override
	public Server getServerDetails(String id) {
		Access access = getAccessWithTenantId();
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(
				TENANT_ID));
		novaClient.token(access.getToken().getId());

		OpenStackRequest<Server> request = new OpenStackRequest<Server>(
				novaClient,
				HttpMethod.GET,
				new StringBuilder("/servers/").append(id).toString(),
				null,
				Server.class);
		Server serverDetail = novaClient.execute(request);
		return serverDetail;
	}

	@Override
	public void allocateFloatingIpForServer(String serverId, String floatingIp) {
		Access access = getAccessWithTenantId();
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(
				TENANT_ID));
		novaClient.token(access.getToken().getId());
		
		ServerAction.AssociateFloatingIp action = new ServerAction.AssociateFloatingIp(floatingIp);
		AssociateFloatingIp associateFloatingIp = new AssociateFloatingIp(serverId, action);
		
		@SuppressWarnings("unchecked")
    OpenStackRequest<ServerAction.AssociateFloatingIp> request = new OpenStackRequest<ServerAction.AssociateFloatingIp>(novaClient,
				HttpMethod.POST,"/servers/"+serverId+"/action",
				associateFloatingIp.json(action),
				ServerAction.AssociateFloatingIp.class);
		
		try {
			novaClient.execute(request);
		} catch (Exception e) {
			//TODO: this can throw harmless exceptions, but check the exception if it is not harmless
			LOGGER.log(Level.WARNING, e.getMessage());
		}
	}

	@Override
	public FloatingIpPools getFloatingIpPools(){
		Access access = getAccessWithTenantId();
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
		novaClient.token(access.getToken().getId());
		
		OpenStackRequest<FloatingIpPools> request = new OpenStackRequest<FloatingIpPools>(
				novaClient, HttpMethod.GET, "/os-floating-ip-pools", null,
				FloatingIpPools.class);
		FloatingIpPools floatingIpPools = novaClient.execute(request);
		return floatingIpPools;
	}
	
	@Override
	public FloatingIp addFloatingIp(){
		
		String poolName="";
		
		if (FLOATINGIP_POOL_NAME.compareTo("")==0) {
			List<FloatingIpPool> poolList = this.getFloatingIpPools().getList();
			if (poolList!=null && poolList.size()>0) {
				poolName = poolList.get(0).getName();
			} else {
				throw new RuntimeException("there isn't any floating ip pool defined");
			}
		}else {
			poolName = FLOATINGIP_POOL_NAME;
		}
		
		Map<String, String> body = new HashMap<String, String>();
		body.put("pool", poolName);
		Entity<Map<String, String>> entity = Entity.json(body);
		
		Access access = getAccessWithTenantId();
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
		novaClient.token(access.getToken().getId());
		
		OpenStackRequest<FloatingIp> request = new OpenStackRequest<FloatingIp>(
				novaClient, HttpMethod.POST, "os-floating-ips",entity ,
				FloatingIp.class);
		FloatingIp floatingIp = novaClient.execute(request);
		return floatingIp;
	}
	
	@Override
	public void addKeyPair(String name, String publicKey){
		Access access = getAccessWithTenantId();

		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(
				TENANT_ID));
		novaClient.token(access.getToken().getId());

		novaClient.keyPairs().create(name, publicKey).execute();
	}
	
	@Override
	public void deleteKeyPair(String name){
		Access access = getAccessWithTenantId();

		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(
				TENANT_ID));
		novaClient.token(access.getToken().getId());

		novaClient.keyPairs().delete(name).execute();
	}
	
	@Override
	public void deleteServer(String id){
		Access access = getAccessWithTenantId();
		Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
		novaClient.token(access.getToken().getId());

		OpenStackRequest<Server> request = new OpenStackRequest<Server>(
				novaClient,
				HttpMethod.DELETE,
				new StringBuilder("/servers/").append(id).toString(),
				null,
				Server.class);
		novaClient.execute(request);
	}

	public String getNetworkId() {
		if(this.networkId==null || this.networkId.compareTo("")==0)
			this.setNetworkId(getNetworkIdByName(NET_NAME));
		return networkId;
	}

	private String getNetworkIdByName(String networkName) {
		Access access = getAccessWithTenantId();
		Quantum quantum = new Quantum(NET_ENDPOINT);
		
		quantum.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));
		Networks networks = quantum.networks().list().execute();
		List<Network> networkList = networks.getList();
		for (Iterator<Network> iterator = networkList.iterator(); iterator.hasNext();) {
			Network network = (Network) iterator.next();
			if(network.getName().compareToIgnoreCase(networkName)==0)
				return network.getId();
		}
		throw new RuntimeException("there isn't any network with the specified network name");
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
}
