package org.fiteagle.adapters.openstack.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.fiteagle.adapters.openstack.OpenstackAdapter;
import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;

public class OpenstackParser {

  private static Logger LOGGER = Logger.getLogger(OpenstackClient.class.toString());
  
	private static ObjectMapper mapper = new ObjectMapper();

	private final Property PROPERTY_ID;
	private final Property PROPERTY_IMAGE_ID;
  private final Property PROPERTY_IMAGES;
  private final Property PROPERTY_IMAGE;
  private final Property PROPERTY_KEYPAIRNAME;
  private final Property PROPERTY_FLAVOR;
  
	
	private OpenstackAdapter adapter;
  
  private static HashMap<OpenstackAdapter, OpenstackParser> instances = new HashMap<OpenstackAdapter, OpenstackParser>();
  
  public OpenstackParser(OpenstackAdapter adapter) {
    this.adapter = adapter;
    PROPERTY_IMAGES = adapter.getAdapterDescriptionModel().getProperty(adapter.getAdapterManagedResource().getNameSpace()+"images");
    PROPERTY_IMAGE = adapter.getAdapterDescriptionModel().getProperty(adapter.getAdapterManagedResource().getNameSpace()+"image");
    PROPERTY_ID = adapter.getAdapterDescriptionModel().getProperty(adapter.getAdapterManagedResource().getNameSpace()+"id");
    PROPERTY_IMAGE_ID = adapter.getAdapterDescriptionModel().getProperty(adapter.getAdapterManagedResource().getNameSpace()+"imageid");
    PROPERTY_KEYPAIRNAME = adapter.getAdapterDescriptionModel().getProperty(adapter.getAdapterManagedResource().getNameSpace()+"keypairname");
    PROPERTY_FLAVOR = adapter.getAdapterDescriptionModel().getProperty(adapter.getAdapterManagedResource().getNameSpace()+"flavor");
  }

  public static synchronized OpenstackParser getInstance(OpenstackAdapter adapter) {
    if (instances.get(adapter) == null) {
      instances.put(adapter, new OpenstackParser(adapter));
    }
    return instances.get(adapter);
  }
	
	static Images parseToImages(String imagesString) {
	  Images images = null;
		try {
      images = mapper.readValue(imagesString, Images.class);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return images;
	}

	public static FloatingIp parseToFloatingIp(String floatingIpString) {
	  FloatingIp ip = null;
		try {
      ip = mapper.readValue(floatingIpString, FloatingIp.class);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
		return ip;
	}

	public static Flavors parseToFlavors(String flavorsString) {
	  Flavors flavors = null;
		try {
      flavors = mapper.readValue(flavorsString, Flavors.class);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return flavors;
	}
	
	public static Server parseToServer(String serverString) {
    Server server = null;
    try {
      server = mapper.readValue(serverString, Server.class);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return server;
  }
	
	public static Servers parseToServers(String serversString) {
    Servers servers = null;
    try {
      servers = mapper.readValue(serversString, Servers.class);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return servers;
  }
	
  private Resource getImage(Image image) {
    Resource imageResource = null;
    StmtIterator imagesIterator = adapter.getAdapterDescriptionModel().listStatements(null, PROPERTY_IMAGE_ID, adapter.getAdapterDescriptionModel().createLiteral(image.getId()));
    if (imagesIterator.hasNext()) {
      imageResource = imagesIterator.next().getSubject();
    }
    return imageResource;
  }
	
	public Model parseToModel(Server server){
	  //TODO: better check whether it's already an URI
	  if(!server.getName().startsWith("http://")){
	    server.setName(adapter.getAdapterInstance().getNameSpace()+server.getName());
	  }
	  Resource resource = ModelFactory.createDefaultModel().createResource(server.getName());
	  resource.addProperty(RDF.type, adapter.getAdapterManagedResource());
	  resource.addProperty(RDFS.label, resource.getLocalName());
    for(Property p : OpenstackAdapter.resourceInstanceProperties){
      switch(p.getLocalName()){
        case "id": 
          if(server.getId() != null){
            resource.addLiteral(p, server.getId());
          }
          break;
        case "status": 
          if(server.getStatus() != null){
            resource.addLiteral(p, server.getStatus());
          }
          break;
        case "created": 
          if(server.getCreated() != null){
            resource.addLiteral(p, server.getCreated());
          }
          break;
        case "image": 
          if(server.getImage() != null && server.getImage().getId() != null){
              Resource image = getImage(server.getImage());
              resource.addProperty(p, image);
          }
          break;
        case "keypairname": 
          if(server.getKeyName() != null){
            resource.addLiteral(p, server.getKeyName());
          }
          break;
        case "flavor": 
          if(server.getFlavor() != null && server.getFlavor().getId() != null){
            resource.addLiteral(p, server.getFlavor().getId());
          }
          break;
      }
    }
    return resource.getModel();
	}
	
	public Model parseToModel(String serverString){
	  Server server = parseToServer(serverString);
	  return parseToModel(server);	
	}
	
	private Resource parseToImagesResource(Images images){
	  Model adapterModel = adapter.getAdapterDescriptionModel();
    Resource imagesResource = adapterModel.createResource(adapter.getAdapterInstance().getURI()+"_images");
    int i = 1;
    for(Image image : images.getList()){
      Resource imageResource = adapterModel.createResource(adapter.getAdapterInstance().getNameSpace()+image.getName().replace(" ", "_").replace(".","-"));
      imageResource.addProperty(RDF.type, adapter.getImageResource());
      imageResource.addProperty(PROPERTY_IMAGE_ID, adapterModel.createLiteral(image.getId()));
      imageResource.addProperty(RDFS.label, adapterModel.createLiteral(image.getName()));
      imagesResource.addProperty(RDF.li(i), imageResource);
      i++;
    }
    
    return imagesResource;
  }
	
	public void addToAdapterInstanceDescription(Servers servers){
    for(Server server : servers.getList()){
      Model resourceModel = parseToModel(server);
      adapter.getAdapterDescriptionModel().add(resourceModel);
    }
  }
	
	public void addToAdapterInstanceDescription(Images images){
	  adapter.getAdapterInstance().addProperty(PROPERTY_IMAGES, parseToImagesResource(images));
	}
	
	public String getResourcePropertyID(String instanceURI){
	  return adapter.getAdapterDescriptionModel().getResource(instanceURI).getProperty(PROPERTY_ID).getLiteral().getValue().toString();
	}

	private String getStringPropertyValue(String instanceURI, Model model, Property property){
	  Resource imagesResource = model.getResource(instanceURI);
	  return imagesResource.getProperty(property).getLiteral().getValue().toString();  
	}
	
	private RDFNode getResourcePropertyValue(String instanceURI, Model model, Property property){
    Resource imagesResource = model.getResource(instanceURI);
    return imagesResource.getProperty(property).getObject();
  }
	
  public ServerForCreate parseToServerForCreate(String instanceURI, Model newInstanceModel) {
    String imageResourceURI = getResourcePropertyValue(instanceURI, newInstanceModel, PROPERTY_IMAGE).toString();
    String imageRef = adapter.getAdapterDescriptionModel().getResource(imageResourceURI).getProperty(PROPERTY_IMAGE_ID).getLiteral().getValue().toString();
    String keyName = getStringPropertyValue(instanceURI, newInstanceModel, PROPERTY_KEYPAIRNAME);
    String flavorRef = getStringPropertyValue(instanceURI, newInstanceModel, PROPERTY_FLAVOR);
    
    ServerForCreate serverForCreate = new ServerForCreate(instanceURI, flavorRef, imageRef, keyName);
    
    return serverForCreate;
  }

  protected Property getPROPERTY_ID() {
    return PROPERTY_ID;
  }
  
  protected Property getPROPERTY_IMAGE_ID() {
    return PROPERTY_IMAGE_ID;
  }

  protected Property getPROPERTY_IMAGES() {
    return PROPERTY_IMAGES;
  }

  public Property getPROPERTY_IMAGE() {
    return PROPERTY_IMAGE;
  }
  
  public Property getPROPERTY_KEYPAIRNAME() {
    return PROPERTY_KEYPAIRNAME;
  }
  
  public Property getPROPERTY_FLAVOR() {
    return PROPERTY_FLAVOR;
  }
}
