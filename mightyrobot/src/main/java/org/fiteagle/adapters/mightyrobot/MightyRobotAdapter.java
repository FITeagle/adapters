package org.fiteagle.adapters.mightyrobot;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.MessageBusOntologyModel;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

@ApplicationScoped
@Startup
public class MightyRobotAdapter extends AbstractAdapter{

	private String [] adapterSpecificPrefix = {"mightyrobot", "http://fiteagle.org/ontology/adapter/mightyrobot#"};
    private static MightyRobotAdapter mightyRobotAdapterSingleton;
    
    public static synchronized MightyRobotAdapter getInstance()
    { 
      if ( mightyRobotAdapterSingleton == null ) 
    	  mightyRobotAdapterSingleton = new MightyRobotAdapter();
      return mightyRobotAdapterSingleton;
    }    
    
    private Resource instanceClassResource;
    private String instanceClassResourceString = "MightyRobot";
    private String adapterResourceString = "MightyRobotAdapter";

    private Property mightyRobotPropertyDancing;
    private Property mightyRobotPropertyExploded;
    private Property mightyRobotPropertyHeadRotation;
    private Property mightyRobotPropertyNickname;    
    
    private List<Property> resourceControlProperties = new LinkedList<Property>();
    
    public MightyRobotAdapter() {
    	
    	adapterName = "RunningMightyRobotAdapter1";
    	
    	// Provide model with needed prefixes
        modelGeneral = ModelFactory.createDefaultModel();
        modelGeneral.setNsPrefix("", "http://fiteagleinternal#");
        modelGeneral.setNsPrefix(adapterSpecificPrefix[0], adapterSpecificPrefix[1]);
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // instantiate instance class resource
        instanceClassResource = modelGeneral.createResource(adapterSpecificPrefix[1] + instanceClassResourceString);
        instanceClassResource.addProperty(RDF.type, OWL.Class);
        instanceClassResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

        // instantiate adapter type resource
        adapterType = modelGeneral.createResource(adapterSpecificPrefix[1] + adapterResourceString);
        adapterType.addProperty(RDF.type, OWL.Class);
        adapterType.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        
        adapterType.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, instanceClassResource);
        adapterType.addProperty(RDFS.label, modelGeneral.createLiteral(adapterResourceString + "Type ", "en"));  
        
        instanceClassResource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapterType);
        instanceClassResource.addProperty(RDFS.label, modelGeneral.createLiteral(instanceClassResourceString + "Resource", "en"));        
        
        
        // create properties
        mightyRobotPropertyDancing = generateProperty(modelGeneral.createProperty(adapterSpecificPrefix[1] +  "dancing"), XSD.xboolean);
        resourceControlProperties.add(mightyRobotPropertyDancing);        

        mightyRobotPropertyExploded = generateProperty(modelGeneral.createProperty(adapterSpecificPrefix[1] +  "exploded"), XSD.xboolean);
        resourceControlProperties.add(mightyRobotPropertyExploded);      

        mightyRobotPropertyHeadRotation = generateProperty(modelGeneral.createProperty(adapterSpecificPrefix[1] +  "headRotation"), XSD.integer);
        resourceControlProperties.add(mightyRobotPropertyHeadRotation);        

        mightyRobotPropertyNickname = generateProperty(modelGeneral.createProperty(adapterSpecificPrefix[1] +  "nickname"), XSD.xstring);
       
        // instantiate adapter instance resource
        adapterInstance = modelGeneral.createResource("http://fiteagleinternal#" + adapterName);
        adapterInstance.addProperty(RDF.type, adapterType);
        adapterInstance.addProperty(RDFS.label, modelGeneral.createLiteral("A deployed Mighty Robot Adapter named: " + adapterName, "en"));
        adapterInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("A Mighty Robot Adapter that can handle multiple Robots and their shenanigans.", "en"));
        // Testbed Addendum
        adapterInstance.addProperty(modelGeneral.createProperty("http://fiteagleinternal#isAdapterIn"), modelGeneral.createResource("http://fiteagleinternal#FITEAGLE_Testbed"));
    }
    
    private Property generateProperty(Property template, Resource XSDType){
    	template.addProperty(RDF.type, OWL.DatatypeProperty);
    	template.addProperty(RDFS.domain, instanceClassResource);
    	template.addProperty(RDFS.range, XSDType);
    	return template;
    }   

    public void init(){

    }

    @Override
    public Object handleCreateInstance(String instanceName){
        MightyRobot temp = new MightyRobot(this, instanceName);
        return temp;
    }
    
    @Override
    public Model handleMonitorInstance(String instanceName, Model modelInstances){
    	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceName);

        Resource mightyRobotInstance = modelInstances.createResource("http://fiteagleinternal#" + instanceName);
        addPropertiesToResource(mightyRobotInstance, currentMightyRobot, instanceName);
        
        return modelInstances;
    } 

	@Override
	public Model handleGetAllInstances(Model modelInstances) {
    	
		for (String key : instanceList.keySet()) {
        	
        	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(key);
        	
            Resource mightyRobotInstance = modelInstances.createResource("http://fiteagleinternal#" + key);
            addPropertiesToResource(mightyRobotInstance, currentMightyRobot, key);        	
  
        }
		return modelInstances;
	}
	
	public void addPropertiesToResource(Resource mightyRobotInstance, MightyRobot currentMightyRobot, String instanceName) {
    	mightyRobotInstance.addProperty(RDF.type, instanceClassResource);
    	mightyRobotInstance.addProperty(RDFS.label, "MightyRobot: " + instanceName);
    	mightyRobotInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("MightyRobot in da house " + instanceName, "en"));
    	mightyRobotInstance.addLiteral(mightyRobotPropertyDancing, currentMightyRobot.getDancing());
    	mightyRobotInstance.addLiteral(mightyRobotPropertyExploded, currentMightyRobot.getExploded());
    	mightyRobotInstance.addLiteral(mightyRobotPropertyHeadRotation, currentMightyRobot.getHeadRotation());
    	mightyRobotInstance.addLiteral(mightyRobotPropertyNickname, currentMightyRobot.getNickname());
    }  	

    @Override
    public List<String> handleConfigureInstance(Statement configureStatement) {
        // TODO Auto-generated method stub

        Resource currentResource = configureStatement.getSubject();
        String instanceName = currentResource.getLocalName();

        List<String> updatedProperties = new LinkedList<String>();

        if (instanceList.containsKey(instanceName)) {
            MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceName);

            for (Property currentProperty : resourceControlProperties) {
                StmtIterator iter2 = currentResource.listProperties(currentProperty);

                while (iter2.hasNext()) {

                    if (currentProperty == mightyRobotPropertyDancing) {
                    	currentMightyRobot.setDancing(iter2.nextStatement().getObject().asLiteral().getBoolean());
                    	updatedProperties.add("dancing");
                    } else if (currentProperty == mightyRobotPropertyExploded) {
                    	currentMightyRobot.setExploded(iter2.nextStatement().getObject().asLiteral().getBoolean());
                    	updatedProperties.add("exploded");
                    } else if (currentProperty == mightyRobotPropertyHeadRotation) {
                    	currentMightyRobot.setHeadRotation((int) iter2.nextStatement().getObject().asLiteral().getLong());
                    	updatedProperties.add("headRotation");
                    } else if (currentProperty == mightyRobotPropertyNickname) {
                        currentMightyRobot.setNickname(iter2.nextStatement().getObject().asLiteral().getString());
                        updatedProperties.add("nickname");
                    }
                }
            }

        }

        return updatedProperties;
    }
    
    @Override
    public String[] getAdapterSpecificPrefix() {
        return adapterSpecificPrefix.clone();
    }

    @Override
    public Resource getAdapterManagedResource() {
        // TODO Auto-generated method stub
    	return instanceClassResource;
    }    

}
