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
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;

public class OpenstackParser {

	private static JsonFactory factory = new JsonFactory();
	private static ObjectMapper mapper = new ObjectMapper(factory);

	private final Property PROPERTY_ID;
  private final Property PROPERTY_IMAGES;
  private final Property PROPERTY_IMAGE;
  
	
	private OpenstackAdapter adapter;
  
  private static HashMap<OpenstackAdapter, OpenstackParser> instances = new HashMap<OpenstackAdapter, OpenstackParser>();
  
  public OpenstackParser(OpenstackAdapter adapter, Property PROPERTY_ID, Property PROPERTY_IMAGES, Property PROPERTY_IMAGE) {
    this.adapter = adapter;
    this.PROPERTY_ID = PROPERTY_ID;
    this.PROPERTY_IMAGES = PROPERTY_IMAGES;
    this.PROPERTY_IMAGE = PROPERTY_IMAGE;    
  }

  public static synchronized OpenstackParser getInstance(OpenstackAdapter adapter, Property PROPERTY_ID, Property PROPERTY_IMAGES, Property PROPERTY_IMAGE) {
    if (instances.get(adapter) == null) {
      instances.put(adapter, new OpenstackParser(adapter, PROPERTY_ID, PROPERTY_IMAGES, PROPERTY_IMAGE));
    }
    return instances.get(adapter);
  }
	
	static Images parseToImages(String imagesString) {
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
	
  public Resource getImage(String id) {
    Resource image;
    StmtIterator imagesIterator = adapter.getAdapterDescriptionModel().listStatements(null, RDF.type,
        adapter.getAdapterDescriptionModel().createProperty("http://open-multinet.info/ontology/resource/openstackvm#Image"));
    if (imagesIterator.hasNext()) {
      image = imagesIterator.next().getSubject();
      if (image.getPropertyResourceValue(PROPERTY_ID).asLiteral().getValue() == id) {
        return image;
      }
    }
    return null;
  }
	
	public AdapterResource parseToAdapterResource(Server server){
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
              Resource image = getImage(server.getImage().getId());
              properties.put(PROPERTY_IMAGE, image);
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
	
	public AdapterResource parseToAdapterResource(String serverString){
	  Server server = parseToServer(serverString);
	  return parseToAdapterResource(server);	
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
	
	public Set<AdapterResource> parseToAdapterResourceSet(Servers servers){
	  Set<AdapterResource> openstackVMs = new HashSet<>();
	  for(Server server : servers.getList()){
	    openstackVMs.add(parseToAdapterResource(server));
	  }
	  return openstackVMs;
	}
	
	private Resource parseToImagesResource(Images images){
	  Model adapterModel = adapter.getAdapterDescriptionModel();
    Resource imagesResource = adapterModel.createResource(adapter.getAdapterInstance().getURI()+"_images");
    int i = 1;
    for(Image image : images.getList()){
      Resource imageResource = adapterModel.createResource(adapter.getAdapterInstancePrefix()[1]+image.getName().replace(" ", "_"));
      imageResource.addProperty(RDF.type, adapterModel.createProperty("http://open-multinet.info/ontology/resource/openstackvm#Image"));
      imageResource.addProperty(PROPERTY_ID, adapterModel.createLiteral(image.getId()));
      imageResource.addProperty(RDFS.label, adapterModel.createLiteral(image.getName()));
      imagesResource.addProperty(RDF.li(i), imageResource);
      i++;
    }
    
    return imagesResource;
  }
	
	public void addPropertiesToResource(Resource openstackInstance, AdapterResource openstackVM, String instanceName) {
    openstackInstance.addProperty(RDF.type, adapter.getAdapterManagedResource());
    openstackInstance.addProperty(RDFS.label, instanceName);
    
    for(Property p : OpenstackAdapter.resourceInstanceProperties){
      if(openstackVM.getProperty(p) != null){
        openstackInstance.addLiteral(p, openstackVM.getProperty(p));
      }
    }
  }
	
	public void addToAdapterInstanceDescription(Servers servers){
	  Set<AdapterResource> openstackVMs = parseToAdapterResourceSet(servers);
      for(AdapterResource vm : openstackVMs){
        String instanceName = vm.getName();
        
        Model createdResourceInstanceModel = ModelFactory.createDefaultModel();
        
        Resource serverInstance = createdResourceInstanceModel.createResource(adapter.getAdapterInstancePrefix()[1]+instanceName);
        addPropertiesToResource(serverInstance, vm, instanceName);

        adapter.createInstance(instanceName, createdResourceInstanceModel);
      }
  }
	
	public void addToAdapterInstanceDescription(Images images){
	  adapter.getAdapterInstance().addProperty(PROPERTY_IMAGES, parseToImagesResource(images));
	}
	
	public String getAdapterResourceID(AdapterResource resource){
	  return (String) resource.getProperty(PROPERTY_ID);
	}
	
	static Servers parseToServers(String serversString) {
	  Servers servers = null;
		try {
      servers = mapper.readValue(serversString, Servers.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
		return servers;
	}

  public ServerForCreate parseToServerForCreate(String instanceName, Map<String, String> properties) {
    String imageResourceURI = OpenstackAdapter.getProperty(PROPERTY_IMAGE.getURI(), properties);
    Statement imageIdStatement = adapter.getAdapterDescriptionModel().getRequiredProperty(adapter.getAdapterDescriptionModel().getResource(imageResourceURI), PROPERTY_ID);
    String imageID = imageIdStatement.getObject().asLiteral().getValue().toString();
    String keypairName = OpenstackAdapter.getProperty(adapter.getAdapterManagedResourcePrefix()[1]+"keypairname", properties);
    String flavorId_small = "2"; 
    
    ServerForCreate serverForCreate = new ServerForCreate();
    serverForCreate.setName(instanceName);
    serverForCreate.setFlavorRef(flavorId_small);
    serverForCreate.setImageRef(imageID);
    serverForCreate.setKeyName(keypairName);
    
    return serverForCreate;
  }
}
