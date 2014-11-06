package org.fiteagle.adapters.openstack.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fiteagle.abstractAdapter.AdapterResource;
import org.fiteagle.adapters.openstack.Image;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;

public class OpenstackParser {

	private static JsonFactory factory = new JsonFactory();
	private static ObjectMapper mapper = new ObjectMapper(factory);

	private static Images parseToImages(String imagesString) {
	  Images images = null;
		try {
      images = mapper.readValue(imagesString, Images.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return images;
	}

	public static FloatingIp parseToFloatingIp(String floatingIpString) throws JsonParseException,
			JsonMappingException, IOException {

		return mapper.readValue(floatingIpString, FloatingIp.class);
	}

	public static Flavors parseToFlavors(String flavorsString)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(flavorsString, Flavors.class);
	}
	
	public static AdapterResource parseToOpenstackVM(Server server, OpenstackAdapter adapter){
	  Map<Property, Object> properties = new HashMap<Property, Object>();
    
    for(Property p : OpenstackAdapter.resourceInstanceProperties){
      switch(p.getLocalName()){
        case "id": 
          if(server.getId() != null){
            properties.put(p, server.getId());
          }
          break;
        case "status": 
          if(server.getStatus() != null){
            properties.put(p, server.getStatus());
          }
          break;
        case "created": 
          if(server.getCreated() != null){
            properties.put(p, server.getCreated());
          }
          break;
        case "image": 
          if(server.getImage() != null && server.getImage().getId() != null){
              Resource image = adapter.getImage(server.getImage().getId());
              properties.put(OpenstackAdapter.PROPERTY_IMAGE, image);
          }
          break;
        case "keypairname": 
          if(server.getKeyName() != null){
            properties.put(p, server.getKeyName());
          }
          break;
      }
    }
    return new AdapterResource(server.getName(), properties);
	}
	
	public static AdapterResource parseToOpenstackVM(String serverString, OpenstackAdapter adapter){
	  Server server = parseToServer(serverString);
	  return parseToOpenstackVM(server, adapter);	
	}
	
	private static Server parseToServer(String serverString) {
		Server server = null;
    try {
      server = mapper.readValue(serverString, Server.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
		return server;
	}
	
	public static Set<AdapterResource> parseToOpenstackVMSet(String serversString, OpenstackAdapter adapter){
	  Set<AdapterResource> openstackVMs = new HashSet<>();
	  Servers servers = parseToServers(serversString);
	  for(Server server : servers.getList()){
	    openstackVMs.add(parseToOpenstackVM(server, adapter));
	  }
	  return openstackVMs;
	}
	
	public static Set<Image> parseToImageSet(String imagesString){
    Set<Image> OpenstackImages = new HashSet<>();
    org.fiteagle.adapters.openstack.client.model.Images images = parseToImages(imagesString);
    for(org.fiteagle.adapters.openstack.client.model.Image image : images){
      OpenstackImages.add(parseToImage(image));
    }
    return OpenstackImages;
  }
	
	public static Image parseToImage(org.fiteagle.adapters.openstack.client.model.Image image){
    String name = image.getName();
    String id = image.getId();
    return new Image(name, id);
  }
	
	private static Servers parseToServers(String serversString) {
	  Servers servers = null;
		try {
      servers = mapper.readValue(serversString, Servers.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
		return servers;
	}
}
