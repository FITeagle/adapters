package org.fiteagle.adapters.mightyrobot;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public final class MightyRobotAdapter extends AbstractAdapter{

	private String adapterSpecificPrefix = "http://fiteagle.org/ontology/adapter/mightyrobot#";
    private static MightyRobotAdapter mightyRobotAdapterSingleton; 
    
    private Resource instanceClassResource;
    private String instanceClassResourceString = "MightyRobotResource";
    private Resource adapterResource;
    private String adapterResourceString = "MightyRobotAdapter";

    private Property mightyRobotPropertyDancing;
    private Property mightyRobotPropertyExploded;
    private Property mightyRobotPropertyHeadRotation;
    private Property mightyRobotPropertyNickname;    
    
    private List<Property> adapterControlProperties = new LinkedList<Property>();
   
    public static synchronized MightyRobotAdapter getInstance() 
    { 
      if ( mightyRobotAdapterSingleton == null ) 
    	  mightyRobotAdapterSingleton = new MightyRobotAdapter(); 
      return mightyRobotAdapterSingleton; 
    }     
    
    private MightyRobotAdapter() {
    		 
        modelGeneral = ModelFactory.createDefaultModel();

        modelGeneral.setNsPrefix("", this.getAdapterSpecificPrefix());
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // Property instantiatesProperty = new PropertyImpl("fiteagle:instantiates");

        instanceClassResource = modelGeneral.createResource(this.getAdapterSpecificPrefix() +  instanceClassResourceString);
        instanceClassResource.addProperty(RDF.type, OWL.Class);
        instanceClassResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

        adapterResource = modelGeneral.createResource(this.getAdapterSpecificPrefix() +  adapterResourceString);
        adapterResource.addProperty(RDF.type, OWL.Class);
        adapterResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        adapterResource.addProperty(modelGeneral.createProperty("http://fiteagle.org/ontology#instantiates"), instanceClassResource);

        // create the properties
        /*
        mightyRobotPropertyDancing = modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "dancing");
        mightyRobotPropertyDancing.addProperty(RDF.type, OWL.DatatypeProperty);
        mightyRobotPropertyDancing.addProperty(RDFS.domain, instanceClassResource);
        mightyRobotPropertyDancing.addProperty(RDFS.range, XSD.xboolean);
        adapterControlProperties.add(mightyRobotPropertyDancing);        

        mightyRobotPropertyExploded = modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "exploded");
        mightyRobotPropertyExploded.addProperty(RDF.type, OWL.DatatypeProperty);
        mightyRobotPropertyExploded.addProperty(RDFS.domain, instanceClassResource);
        mightyRobotPropertyExploded.addProperty(RDFS.range, XSD.xboolean);
        adapterControlProperties.add(mightyRobotPropertyExploded);        

        mightyRobotPropertyHeadRotation = modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "headRotation");
        mightyRobotPropertyHeadRotation.addProperty(RDF.type, OWL.DatatypeProperty);
        mightyRobotPropertyHeadRotation.addProperty(RDFS.domain, instanceClassResource);
        mightyRobotPropertyHeadRotation.addProperty(RDFS.range, XSD.integer);
        adapterControlProperties.add(mightyRobotPropertyHeadRotation);         
 
        mightyRobotPropertyNickname = modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "nickname");
        mightyRobotPropertyNickname.addProperty(RDF.type, OWL.DatatypeProperty);
        mightyRobotPropertyNickname.addProperty(RDFS.domain, instanceClassResource);
        mightyRobotPropertyNickname.addProperty(RDFS.range, XSD.xstring);
        adapterControlProperties.add(mightyRobotPropertyNickname); 
        */          

        mightyRobotPropertyDancing = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "dancing"), XSD.xboolean);
        adapterControlProperties.add(mightyRobotPropertyDancing);

        mightyRobotPropertyExploded = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "exploded"), XSD.xboolean);
        adapterControlProperties.add(mightyRobotPropertyExploded);

        mightyRobotPropertyHeadRotation = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "headRotation"), XSD.integer);
        adapterControlProperties.add(mightyRobotPropertyHeadRotation);

        mightyRobotPropertyNickname = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "nickname"), XSD.xstring);
        adapterControlProperties.add(mightyRobotPropertyNickname); 

        Resource individualMightyRobotAdapter1 = modelGeneral.createResource(this.getAdapterSpecificPrefix() +  "individualMightyRobotAdapter1");
        individualMightyRobotAdapter1.addProperty(RDF.type, adapterResource);
        individualMightyRobotAdapter1.addProperty(RDFS.label, modelGeneral.createLiteral("MightyRobot Adapter 1", "en"));
        individualMightyRobotAdapter1.addProperty(RDFS.comment, modelGeneral.createLiteral("A MightyRobot Adapter 1", "en"));

    }
    
    private Property generateProperty(Property template, Resource XSDType){
    	template.addProperty(RDF.type, OWL.DatatypeProperty);
    	template.addProperty(RDFS.domain, instanceClassResource);
    	template.addProperty(RDFS.range, XSDType);
    	return template;
    } 
    
    @Override 
    public Object handleCreateInstance(int instanceID){
    	MightyRobot temp = new MightyRobot(this, instanceID);
    			postSparqlEntry(temp);
    	return temp;
    }
   
    private boolean postSparqlEntry(MightyRobot currentRobot){
/* Get the entry
PREFIX el: <http://purl.org/dc/elements/1.1/>
SELECT ?InstanceID ?Dancing ?Exploded ?HeadRotation ?Nickname ?owningAdapter
WHERE
{
?MightyRobot el:InstanceID ?InstanceID .
?MightyRobot el:Dancing ?Dancing .
?MightyRobot el:Exploded ?Exploded .
?MightyRobot el:HeadRotation ?HeadRotation . 
?MightyRobot el:Nickname ?Nickname .
?MightyRobot el:owningAdapter ?owningAdapter .
}		 
 */			
		String postString = 
				"PREFIX dc: <http://purl.org/dc/elements/1.1/> INSERT DATA { <http://example.org/MR/0.1>"
				+ " dc:InstanceID \"" + currentRobot.getInstanceID() + "\" ;"
				+ " dc:Nickname \"" + currentRobot.getNickname() + "\" ;"
				+ " dc:Dancing \"" + currentRobot.getDancing() + "\" ;"
				+ " dc:Exploded \"" + currentRobot.getExploded() + "\" ;"
				+ " dc:HeadRotation \"" + currentRobot.getHeadRotation() + "\" ;"				
                + " dc:owningAdapter \"" + currentRobot.getOwningAdapter() + "\" .}";
 
		String updateURL = "http://localhost:3030/ds/update";
		System.out.println("Posting " + postString + "\nto" + updateURL);
		UpdateRequest updateRequest = UpdateFactory.create(postString);
		UpdateProcessRemote uPR = (UpdateProcessRemote) 
				UpdateExecutionFactory.createRemote(updateRequest, updateURL);
		uPR.execute();
		return true;
    }
    
    @Override
    public void handleTerminateInstance(int instanceID){
/*
PREFIX dc: <http://purl.org/dc/elements/1.1/>
DELETE DATA
{
  <http://example.org/book/book19> dc:title "A new book" ;
                         dc:creator "A.N.Other" .
}	
 */
    	//if (true) return; 
    	String postString = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
						"DELETE DATA {\n" +
						"<http://example.org/MR/0.1>" + 
						"dc:InstanceID \"" + instanceID + "\" . \n" +
								"}";


		String updateURL = "http://localhost:3030/ds/update";
		System.out.println("Posting " + postString + "\nto" + updateURL);
		UpdateRequest updateRequest = UpdateFactory.create(postString);
		UpdateProcessRemote uPR = (UpdateProcessRemote) 
				UpdateExecutionFactory.createRemote(updateRequest, updateURL);
		uPR.execute();
    }
    
    @Override
    public Model handleMonitorInstance(int instanceID, Model modelInstances){
    	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceID);

        Resource mightyRobotInstance = modelInstances.createResource(this.getAdapterSpecificPrefix() +  "m" + instanceID);
        mightyRobotInstance.addProperty(RDF.type, instanceClassResource);
        mightyRobotInstance.addProperty(RDFS.label, "" + instanceID);
        mightyRobotInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("MightyRobot in da house " + instanceID, "en"));

        mightyRobotInstance.addLiteral(mightyRobotPropertyDancing, currentMightyRobot.getDancing());
        mightyRobotInstance.addLiteral(mightyRobotPropertyExploded, currentMightyRobot.getExploded());
        mightyRobotInstance.addLiteral(mightyRobotPropertyHeadRotation, currentMightyRobot.getHeadRotation());
        mightyRobotInstance.addLiteral(mightyRobotPropertyNickname, currentMightyRobot.getNickname());       

        return modelInstances;
    }

	@Override
	public Model handleGetAllInstances(Model modelInstances) {
		for (Integer key : instanceList.keySet()) {
        	
        	MightyRobot currentMightyRobot= (MightyRobot) instanceList.get(key);

            Resource mightyRobotInstance = modelInstances.createResource(this.getAdapterSpecificPrefix() +  "m" + key);
            mightyRobotInstance.addProperty(RDF.type, instanceClassResource);
            mightyRobotInstance.addProperty(RDFS.label, "" + key);
            mightyRobotInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("MightyRobot in da house " + key, "en"));

            mightyRobotInstance.addLiteral(mightyRobotPropertyDancing, currentMightyRobot.getDancing());
            mightyRobotInstance.addLiteral(mightyRobotPropertyExploded, currentMightyRobot.getExploded());
            mightyRobotInstance.addLiteral(mightyRobotPropertyHeadRotation, currentMightyRobot.getHeadRotation());
            mightyRobotInstance.addLiteral(mightyRobotPropertyNickname, currentMightyRobot.getNickname());  
        }
		return modelInstances;
	}

	@Override
	public String handleControlInstance(Model model2) {
        StringWriter sw = new StringWriter();

        StmtIterator iter = model2.listStatements(new SimpleSelector(null, RDF.type, instanceClassResource));
        while (iter.hasNext()) {
            Resource currentResource = iter.nextStatement().getSubject();
            // sw.write(currentResource.getProperty(RDFS.label).getObject().toString());
            int key = Integer.parseInt(currentResource.getProperty(RDFS.label).getObject().toString());
            if (instanceList.containsKey(key)) {
            	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(key);

                for (Property currentProperty : adapterControlProperties) {
                    StmtIterator iter2 = currentResource.listProperties(currentProperty);

                    while (iter2.hasNext()) {

                    	String newValue = "";
                        if (currentProperty == mightyRobotPropertyDancing) {
                        	
                        	boolean value = iter2.nextStatement().getObject().asLiteral().getBoolean();
                        	currentMightyRobot.setDancing(value);
                        	newValue = value + "";
                        	
                        } else if (currentProperty == mightyRobotPropertyExploded) {
                        	
                        	boolean value = iter2.nextStatement().getObject().asLiteral().getBoolean();
                        	currentMightyRobot.setExploded(value);
                        	newValue = value + "";
                        	 
                        } else if (currentProperty == mightyRobotPropertyHeadRotation) {
                        	
                        	int value = (int) iter2.nextStatement().getObject().asLiteral().getLong();
                        	currentMightyRobot.setHeadRotation(value);
                        	newValue = value + "";
                        	
                        } else if (currentProperty == mightyRobotPropertyNickname) {

                        	String value = iter2.nextStatement().getObject().asLiteral().getString();
                        	currentMightyRobot.setNickname(value);
                        	newValue = value + "";
                        	
                        }

                        sw.write("Changed mightyrobot instance " + key + " property " + currentProperty.toString() + " to value " + newValue + "\n");
                    }
                }

            }
        }

        return sw.toString(); 
	}
	
	@Override
	public String getInstanceClassName() {
		return MightyRobot.class.getName();
	}

	@Override
	public String getAdapterSpecificPrefix() {
		return adapterSpecificPrefix;
	}

}
