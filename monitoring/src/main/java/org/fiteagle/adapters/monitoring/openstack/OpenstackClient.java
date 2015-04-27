package org.fiteagle.adapters.monitoring.openstack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.fiteagle.api.core.Config;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import com.woorea.openstack.base.client.HttpMethod;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.connector.JerseyConnector;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.api.TokensResource;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.nova.Nova;

public class OpenstackClient {


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
		
private boolean PREFERENCES_INITIALIZED = false;

	private void loadPreferences() {
		Config preferences = new Config("Openstack-2");
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

		if(preferences.getProperty("default_image_id")!= null){
			DEFAULT_IMAGE_ID = preferences.getProperty("default_image_id");
		}else{
			throw new InsufficientOpenstackPreferences("default_image_id");
		}

		if(preferences.getProperty("default_flavor_id")!= null){
			DEFAULT_FLAVOR_ID = preferences.getProperty("default_flavor_id");
		}else{
			throw new InsufficientOpenstackPreferences("default_flavor_id");
		}

		if(preferences.getProperty("default_keypair_id")!= null){
			DEFAULT_KEYPAIR_ID = preferences.getProperty("default_keypair_id");
		}else{
			throw new InsufficientOpenstackPreferences("default_keypair_id");
		}

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
	
	public String getHostName(String vm_id){
		ArrayList<String> hypervisors = getHypervisors() ;
		
		Access access = getAccessWithTenantId();
	    
	    Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
	    novaClient.token(access.getToken().getId());
	    
	    String result = null ;
	    for (String hostname : hypervisors){
	    	OpenStackRequest<String> request = new OpenStackRequest<String>(
	    			novaClient, HttpMethod.GET, "/os-hypervisors/" + hostname + "/servers", null,
	    	        String.class);
	    	String servers = null;
	    	
	    	try{
		    	servers = novaClient.execute(request);
		    } catch(OpenStackResponseException e){
		      LOGGER.log(Level.SEVERE, e.getMessage());
		      return null;
		    }
	    	
	    	try{
		    	JSONParser parser = new JSONParser() ;			
				JSONObject object = (JSONObject) parser.parse(servers) ;
			    JSONArray json_servers = (JSONArray) object.get("hypervisors") ;
			    JSONObject obj = (JSONObject) json_servers.get(0) ;
			    JSONArray arr = (JSONArray) obj.get("servers") ;
				for (Object o : arr){
					JSONObject ob = (JSONObject) o ;
					if(ob.get("uuid").toString().matches(vm_id)){
						result = hostname ;
						return result ;
					}
				}
	    	}catch(ParseException pe){
	    		LOGGER.log(Level.WARNING,"Cannot parse result.");
	    	}
	    }
	    return result;
	}
	
	private ArrayList<String> getHypervisors(){
		Access access = getAccessWithTenantId();
	    
	    Nova novaClient = new Nova(NOVA_ENDPOINT.concat("/").concat(TENANT_ID));
	    novaClient.token(access.getToken().getId());

	    OpenStackRequest<String> request = new OpenStackRequest<String>(
	        novaClient, HttpMethod.GET, "/os-hypervisors", null,
	        String.class);
	    	    
	    String hypervisors = null;
	    try{
	    	hypervisors = novaClient.execute(request);
	    } catch(OpenStackResponseException e){
	      LOGGER.log(Level.SEVERE, e.getMessage());
	      return null;
	    }
	    ArrayList<String> hypervisors_list = new ArrayList<String>() ;
	    try{
		    JSONParser parser = new JSONParser() ;	
			JSONObject object = (JSONObject) parser.parse(hypervisors) ;
		    JSONArray json_hypervisors = (JSONArray) object.get("hypervisors") ;
		    for (Object o : json_hypervisors){
		    	JSONObject obj = (JSONObject) o ;
		    	hypervisors_list.add(obj.get("hypervisor_hostname").toString()) ;
		    }
	    }catch(ParseException pe){
	    	LOGGER.log(Level.WARNING,"Cannot parse result.");
    	}
	    
	    return hypervisors_list;
	}
	
	public static class InsufficientOpenstackPreferences extends RuntimeException {
	    
	    private static final long serialVersionUID = 6511540487288262809L;

	    public InsufficientOpenstackPreferences(String preferenceName) {
	      super("Please set the preference: "+preferenceName);
	    }
	  }
	
}
