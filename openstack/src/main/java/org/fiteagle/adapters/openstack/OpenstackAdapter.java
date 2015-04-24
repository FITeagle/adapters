package org.fiteagle.adapters.openstack;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.woorea.openstack.nova.model.*;
import info.openmultinet.ontology.vocabulary.*;

import java.util.*;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.openstack.client.IOpenstackClient;
import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.OpenstackParser;
import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;
import org.fiteagle.adapters.openstack.client.model.Servers;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OpenstackAdapter extends AbstractAdapter {

  private IOpenstackClient openstackClient;
  private OpenstackParser openstackParser;

  private static Resource adapter;
  public static List<Property> resourceInstanceProperties = new ArrayList<Property>();
  
  private Model adapterModel;
  private Resource adapterInstance;
  
  public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();
  
  public static OpenstackAdapter getTestInstance(IOpenstackClient openstackClient){
    OpenstackAdapter instance = (OpenstackAdapter) adapterInstances.values().iterator().next();
    OpenstackAdapter testInstance = new OpenstackAdapter(instance.getAdapterInstance(), openstackClient);
    testInstance.updateAdapterDescription();
    return testInstance;
  }
  
  static {
    Model adapterModel = OntologyModelUtil.loadModel("ontologies/openstack.ttl", IMessageBus.SERIALIZATION_TURTLE);
    
    ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
    if (adapterIterator.hasNext()) {
      adapter = adapterIterator.next();
    }
    
    createDefaultAdapterInstance(adapterModel);
  }
  
  private static void createDefaultAdapterInstance(Model adapterModel){
 //   Resource adapterInstance = adapterModel.createResource(OntologyModelUtil.getResourceNamespace()+"Openstack-1");

    adapter = adapterModel.createResource(Omn_domain_pc.VMServer);
    adapter.addProperty(RDFS.subClassOf,MessageBusOntologyModel.classAdapter);
    adapter.addProperty(Omn_lifecycle.implements_,Omn_domain_pc.VM);

    Resource adapterInstance = ModelFactory.createDefaultModel().createResource(OntologyModelUtil.getResourceNamespace()+"VMServer-1");
    adapterInstance.addProperty(RDF.type, Omn_domain_pc.VMServer);
    adapterInstance.addProperty(RDF.type, OWL2.NamedIndividual);
    adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
    adapterInstance.addProperty(RDFS.comment, "An openstack vm server that can handle different VMs.");
    adapterInstance.addProperty(Geo.lat,"52.5258083");
    adapterInstance.addProperty(Geo.long_,"13.3172764");
    adapterInstance.addProperty(Omn_resource.isExclusive,"false");

//    Resource testbed = adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
//    adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);
//
//    StmtIterator resourceIterator = adapter.listProperties(Omn_lifecycle.implements_);
//    if (resourceIterator.hasNext()) {
//      Resource resource = resourceIterator.next().getObject().asResource();
//
//      adapterInstance.addProperty(Omn_lifecycle.canImplement, resource);
//      ResIterator propertiesIterator = adapterModel.listSubjectsWithProperty(RDFS.domain, resource);
//      while (propertiesIterator.hasNext()) {
//        Property p = adapterModel.getProperty(propertiesIterator.next().getURI());
//        resourceInstanceProperties.add(p);
//      }
//    }
    
    new OpenstackAdapter(adapterInstance, new OpenstackClient());
  }
  
  private OpenstackAdapter(Resource adapterInstance, IOpenstackClient openstackClient){
    //super(adapterInstance.getLocalName());
    
    this.adapterInstance = adapterInstance;
    this.adapterModel = adapterInstance.getModel();
    
    this.openstackClient = openstackClient;
  //  this.openstackParser = OpenstackParser.getInstance(this);

    List<Resource> diskimages = new ArrayList<>();
    Images images = openstackClient.listImages();
    for(Image image : images.getList()){
      Resource diskImage = adapterInstance.getModel().createResource(OntologyModelUtil.getResourceNamespace() + "diskImage/" +image.getId() );
      diskImage.addProperty(RDF.type, Omn_domain_pc.DiskImage);
      diskImage.addProperty(Omn_domain_pc.hasDiskimageLabel,image.getName());
      diskImage.addProperty(Omn_domain_pc.hasDiskimageURI, image.getId());
      diskimages.add(diskImage);

    }
    Flavors flavors = openstackClient.listFlavors();
    for(Flavor flavor: flavors.getList()){
      Resource vmResource = adapterInstance.getModel().createResource(OntologyModelUtil.getResourceNamespace() + flavor.getName());
      vmResource.addProperty(RDFS.subClassOf,Omn_domain_pc.VM);
      vmResource.addProperty(Omn_domain_pc.hasCPU, flavor.getVcpus());
      vmResource.addProperty(Omn_lifecycle.hasID,flavor.getId());
      for(Resource r: diskimages){
        vmResource.addProperty(Omn_domain_pc.hasDiskImage, r);
      }
      adapterInstance.addProperty(Omn_lifecycle.canImplement, vmResource);

    }
    adapterInstance.addProperty(Omn_lifecycle.canImplement, Omn_domain_pc.VM);
    adapterInstances.put(adapterInstance.getURI(), this);
  }
  
  @Override
  public Model createInstance(String instanceURI, Model newInstanceModel) {

    Resource requestedVM = newInstanceModel.getResource(instanceURI);

    String typeURI = getRequestedTypeURI(requestedVM);
    String flavorId = getFlavorId(typeURI);
    String diskImageURI = getDiskImageId(requestedVM);
    String username = getUsername(requestedVM);
    String publicKey = getPublicKey(requestedVM);

    ServerForCreate serverForCreate = new ServerForCreate(instanceURI,flavorId,diskImageURI,null);

    if(username != null && publicKey != null) {
      String userdata_not_encoded = "#cloud-config\n" +
              "users:\n" +
              "  - name: " + username + "\n" +
              "    sudo: ALL=(ALL) NOPASSWD:ALL\n" +
              "    shell: /bin/bash\n" +
              "    ssh-authorized-keys:\n" +
              "      - " + publicKey + "\n";

      String userdata_encoded = Base64.getEncoder().encodeToString(userdata_not_encoded.getBytes());

      serverForCreate.setUserData(userdata_encoded);
    }

    CreateVM createVM = new CreateVM(serverForCreate, this);
    Thread createVMThread = new Thread(createVM);
    createVMThread.start();

     Property property = newInstanceModel.createProperty(Omn_lifecycle.hasState.getNameSpace(), Omn_lifecycle.hasState.getLocalName());
     property.addProperty(RDF.type, OWL.FunctionalProperty);
     requestedVM.addProperty(property, Omn_lifecycle.Uncompleted);
    return newInstanceModel;
  }

  private String addKeypairId(String username, String publicKey ) {
    String keyPairId = null;

    if (username != null && publicKey != null) {
        keyPairId = username + UUID.randomUUID();
        openstackClient.addKeyPair(keyPairId, publicKey);
    }


    return keyPairId;
  }

  private String getPublicKey(Resource requestedVM) {
    Statement publicKeyStatement = requestedVM.getProperty(Omn_service.publickey);
    return publicKeyStatement.getObject().toString();
  }

  private String getUsername(Resource requestedVM) {
    Statement usernameStatement = requestedVM.getProperty(Omn_service.username);


    if(usernameStatement != null) {


      String userName = usernameStatement.getObject().toString();
      return userName;
    }
    return null;
  }


  private String getFlavorId(String typeURI) {
    String flavorId = null;

    Resource requestedFlavor = this.adapterModel.getResource(typeURI);
    return requestedFlavor.getProperty(Omn_lifecycle.hasID).getObject().asLiteral().getString();
  }

  private String getDiskImageId(Resource requestedVM) {
    String diskImageURI = null;
    Statement requestedDiskImage = requestedVM.getProperty(Omn_domain_pc.hasDiskImage);
    if(requestedDiskImage != null){
      Resource diskImage = this.adapterModel.getResource(requestedDiskImage.getObject().asResource().getURI());


       diskImageURI = diskImage.getProperty(Omn_domain_pc.hasDiskimageURI).getString();
    }
    return diskImageURI;
  }

  private String getRequestedTypeURI(Resource requestedVM) {
    StmtIterator stmtIterator = requestedVM.listProperties(RDF.type);
    String requestedType = null;
    while (stmtIterator.hasNext()){
      Statement statement = stmtIterator.nextStatement();
      RDFNode one = statement.getObject();
      boolean isNode =  one.equals(Omn_resource.Node);
      if(!isNode){
        requestedType = statement.getObject().asResource().getURI();
        break;
      }
    }
    return requestedType;
  }

  @Override
  public void deleteInstance(String instanceURI) throws InstanceNotFoundException {
    Model model = getInstance(instanceURI);
    ResIterator iter = model.listSubjectsWithProperty(RDF.type, getAdapterManagedResources().get(0));
    if (iter.hasNext()) {
      Resource instance = iter.next();
      String id = instance.getRequiredProperty(openstackParser.getPROPERTY_ID()).getLiteral().getString();
      openstackClient.deleteServer(id);
      return;
    }
    throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
  }
  
  @Override
  public void updateAdapterDescription(){
    Images images = openstackClient.listImages();
    if(images != null){
     // openstackParser.addToAdapterInstanceDescription(images);
    }
  }
  
  @Override
  public Model updateInstance(String instanceURI, Model configureModel) {
    // TODO Auto-generated method stub
    return null;
  }

  public Resource getImageResource(){
    return adapterModel.getResource(getAdapterManagedResources().get(0).getNameSpace()+"OpenstackImage");
  }

  @Override
  public Resource getAdapterInstance() {
    return adapterInstance;
  }

  @Override
  public Resource getAdapterType() {
    return adapter;
  }

  @Override
  public Model getAdapterDescriptionModel() {
    return adapterModel;
  }
  
  public OpenstackParser getOpenstackParser(){
    return openstackParser;
  }

  @Override
  public Model getInstance(String instanceURI) throws InstanceNotFoundException {
    Servers servers = openstackClient.listServers();
    for(Server server : servers.getList()){
      if(server.getName().equals(instanceURI)){
        return openstackParser.parseToModel(server);
      }
    }
    throw new InstanceNotFoundException("Instance "+instanceURI+" not found");
  }

  @Override
  public Model getAllInstances() throws InstanceNotFoundException {
    Model model = ModelFactory.createDefaultModel();
    Servers servers = openstackClient.listServers();
    for(Server server : servers.getList()){
      model.add(getInstance(server.getName()));
    }
    return model;
  }

  private class CreateVM implements Runnable {

    private final ServerForCreate serverForCreate;
    private final OpenstackAdapter parent;

    public CreateVM(ServerForCreate serverForCreate, OpenstackAdapter parent){
      this.parent =  parent;
      this.serverForCreate = serverForCreate;
    }


    @Override
    public void run() {
      Server server = openstackClient.createServer(serverForCreate);
      FloatingIps floatingIps = openstackClient.listFreeFloatingIps();
      FloatingIp floatingIp = null;
      if(floatingIps != null){
        Iterator<FloatingIp> floatingIpIterator = floatingIps.iterator();
        while(floatingIpIterator.hasNext()) {

          FloatingIp tempfloatingIp = floatingIpIterator.next();
          if (tempfloatingIp.getInstanceId() == null) {
            floatingIp = tempfloatingIp;
          }
        }
      }
      try {
      if(floatingIp == null) {

          floatingIp = openstackClient.addFloatingIp();

        }
        openstackClient.allocateFloatingIpForServer(server.getId(),floatingIp.getIp());
      }catch (Exception e){
        throw  new RuntimeException();
      }

      Model model = parseToModel(server);
      parent.notifyListeners(model, UUID.randomUUID().toString(),IMessageBus.TYPE_INFORM, IMessageBus.TARGET_ORCHESTRATOR);



    }


    private Model parseToModel(Server server){
      //TODO: better check whether it's already an URI

      Resource resource = ModelFactory.createDefaultModel().createResource(server.getName());
      Server.Addresses addresses = server.getAddresses();
      boolean hasFloatingIP = false;
      //for(Server.Addresses.Address address:addresses.getAddresses())


      return resource.getModel();
    }

  }



}
