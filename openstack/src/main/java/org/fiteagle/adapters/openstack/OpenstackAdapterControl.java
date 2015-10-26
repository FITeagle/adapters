package org.fiteagle.adapters.openstack;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.AdapterControl;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapter;
import org.fiteagle.adapters.openstack.dm.OpenstackAdapterMDBSender;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Created by dne on 30.06.15.
 */

@Singleton
@Startup
public class OpenstackAdapterControl extends AdapterControl {

    @Inject
    protected OpenstackAdapterMDBSender mdbSender;
    
    @javax.annotation.Resource
	private ManagedExecutorService executorService;
    
    public static Map <String,Map> instancesDefaultFlavours = new HashMap<>();
    


    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    @PostConstruct
    public void initialize(){
        LOGGER.log(Level.INFO, "Starting OpenStackAdapter");

        this.adapterModel = ModelFactory.createDefaultModel();


        
        try {
        	init();
			final WatchService watcher = FileSystems.getDefault().newWatchService();
			final Path filePath = this.adapterInstancesConfig.getFilePath();
			
			try {
			    final WatchKey key = filePath.getParent().register(watcher,
			                           ENTRY_CREATE,
			                           ENTRY_DELETE,
			                           ENTRY_MODIFY);
			    
			    
			    executorService.submit(new Runnable(){
			    	Path tmpPath = filePath.getFileName();
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						while(true){
							WatchKey tmpKey = null;
							try {
								tmpKey = watcher.take();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							for (WatchEvent<?> event : tmpKey.pollEvents()) {
								WatchEvent.Kind<?> kind = event.kind();
								Path eventPath = (Path) event.context();
								 if (eventPath.endsWith(tmpPath)) {
									 
										LOGGER.log(Level.SEVERE, "Property File changed. Will refresh Adapter now");
										init();
						            }
								 key.reset();
							}
							
						} 
						
						
						
					}
			    	
			    });
			    
			    
			} catch (IOException x) {
			    x.printStackTrace();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void init(){
        this.adapterInstancesConfig= readConfig("OpenStackAdapter");

        createAdapterInstances();


        publishInstances();
    }
    
    @Override
    public AbstractAdapter createAdapterInstance(Model tbox, Resource abox) {
        OpenstackAdapter adapter =  new OpenstackAdapter(tbox,abox);
        adapter.setListener(mdbSender);
        return  adapter;
    }

    @Override
    protected void parseConfig() {

        String jsonProperties = this.adapterInstancesConfig.readJsonProperties();
        if(!jsonProperties.isEmpty()){
            JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(jsonProperties.getBytes()));

            JsonObject jsonObject = jsonReader.readObject();

            JsonArray adapterInstances = jsonObject.getJsonArray(IAbstractAdapter.ADAPTER_INSTANCES);

            for (int i = 0; i < adapterInstances.size(); i++) {
                JsonObject adapterInstanceObject = adapterInstances.getJsonObject(i);
                String adapterInstance = adapterInstanceObject.getString(IAbstractAdapter.COMPONENT_ID);
                
                if(!adapterInstance.isEmpty()){
                Model model = ModelFactory.createDefaultModel();
                Resource resource = model.createResource(adapterInstance);
                //parse possible additional values from config
                OpenstackAdapter adapter = (OpenstackAdapter)createAdapterInstance(adapterModel, resource);

                String floating_ip_pool  = adapterInstanceObject.getString(IOpenStack.FLOATING_IP_POOL_NAME);
                adapter.setFloatingPool(floating_ip_pool);

                String keystone_auth_URL  = adapterInstanceObject.getString(IOpenStack.KEYSTONE_AUTH_URL);
                adapter.setKeystone_auth_URL(keystone_auth_URL);

                String net_name  = adapterInstanceObject.getString(IOpenStack.NET_NAME);
                adapter.setNet_name(net_name);

                String nova_endpoint  = adapterInstanceObject.getString(IOpenStack.NOVA_ENDPOINT);
                adapter.setNova_endpoint(nova_endpoint);

                String keystone_password  = adapterInstanceObject.getString(IOpenStack.KEYSTONE_PASSWORD);
                adapter.setKeystone_password(keystone_password);

                String keystone_endpoint  = adapterInstanceObject.getString(IOpenStack.KEYSTONE_ENDPOINT);
                adapter.setKeystone_endpoint(keystone_endpoint);

                String glance_endpoint  = adapterInstanceObject.getString(IOpenStack.GLANCE_ENDPOINT);
                adapter.setGlance_endpoint(glance_endpoint);

                String net_endpoint  = adapterInstanceObject.getString(IOpenStack.NET_ENDPOINT);
                adapter.setNet_endpoint(net_endpoint);

                String tenant_name  = adapterInstanceObject.getString(IOpenStack.TENANT_NAME);
                adapter.setTenant_name(tenant_name);

                String keystone_username  = adapterInstanceObject.getString(IOpenStack.KEYSTONE_USERNAME);
                adapter.setKeystone_username(keystone_username);

                String default_flavor_id  = adapterInstanceObject.getString(IOpenStack.DEFAULT_FLAVOR_ID);
                adapter.setDefault_flavor_id(default_flavor_id);

                String default_image_id  = adapterInstanceObject.getString(IOpenStack.DEFAULT_IMAGE_ID);
                adapter.setDefault_image_id(default_image_id);
                
                String default_region  = adapterInstanceObject.getString(IOpenStack.DEFAULT_REGION);
                adapter.setDefault_region(default_region);
   
                try{
                    

                	JsonArray adapterInstancesFlavours = adapterInstanceObject.getJsonArray("defaultFlavours");
                    Map<String,ArrayList<String>> defaultFlavours = new HashMap<>();
                    
                        for(String s : adapterInstancesFlavours.getJsonObject(0).keySet()){
                       	JsonArray tmp = adapterInstancesFlavours.getJsonObject(0).getJsonArray(s);
                        ArrayList<String> tmpArray = new ArrayList<String>();
                       	tmpArray.add(tmp.getJsonObject(0).getString("diskImage"));
                       	tmpArray.add(tmp.getJsonObject(0).getString("flavourName"));
                       	defaultFlavours.put(s, tmpArray);
                        }

                        
                   instancesDefaultFlavours.put(adapter.getId(), defaultFlavours);
                   
                }catch(Exception e){
                    LOGGER.log(Level.SEVERE, "Could not find default Flavours in the Config-File for Instance" + adapterInstance);
                    e.printStackTrace();
                }
                
                
                
                adapter.initFlavors();
                this.adapterInstances.put(adapter.getId(),adapter);
                LOGGER.log(Level.SEVERE, this.adapterInstances.toString());
                

            }
            }
            

            

  
        }

        
        
            
            
    }
    
    @Override
    protected void addAdapterProperties(Map<String, String> adapterInstnaceMap){
      
      adapterInstnaceMap.put(IOpenStack.FLOATING_IP_POOL_NAME, "");
      adapterInstnaceMap.put(IOpenStack.KEYSTONE_AUTH_URL, "");
      adapterInstnaceMap.put(IOpenStack.NET_NAME, "");
      adapterInstnaceMap.put(IOpenStack.NOVA_ENDPOINT, "");
      adapterInstnaceMap.put(IOpenStack.KEYSTONE_PASSWORD, "");
      adapterInstnaceMap.put(IOpenStack.KEYSTONE_ENDPOINT, "");
      adapterInstnaceMap.put(IOpenStack.GLANCE_ENDPOINT, "");
      adapterInstnaceMap.put(IOpenStack.NET_ENDPOINT, "");
      adapterInstnaceMap.put(IOpenStack.TENANT_NAME, "");
      adapterInstnaceMap.put(IOpenStack.KEYSTONE_USERNAME, "");
      adapterInstnaceMap.put(IOpenStack.DEFAULT_FLAVOR_ID, "");
      adapterInstnaceMap.put(IOpenStack.DEFAULT_IMAGE_ID, "");
      
    }

    
    @PreDestroy
    public void preDestroy(){
    for (String adapter : this.adapterInstances.keySet()){
    	OpenstackAdapter open = (OpenstackAdapter) this.adapterInstances.get(adapter);
    	try {
			open.openstackClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
    }
    	
    }
    
}
