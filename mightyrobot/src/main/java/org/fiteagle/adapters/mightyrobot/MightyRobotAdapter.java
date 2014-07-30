package org.fiteagle.adapters.mightyrobot;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;



import org.fiteagle.abstractAdapter.AbstractAdapter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
//import com.hp.hpl.jena.rdf.model.Resource;
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
import org.fiteagle.adapters.mightyrobot.dm.MightyRobotAdapterBean;
import org.fiteagle.adapters.mightyrobot.dm.MightyRobotAdapterEJB;

import javax.ejb.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;

import javax.annotation.Resource;
import javax.naming.InitialContext;

@ApplicationScoped
public class MightyRobotAdapter extends AbstractAdapter{

	private String adapterSpecificPrefix = "http://fiteagle.org/ontology/adapter/mightyrobot#";
    private static MightyRobotAdapter mightyRobotAdapterSingleton; 
    
    private com.hp.hpl.jena.rdf.model.Resource instanceClassResource;
    private String instanceClassResourceString = "MightyRobotResource";
    private com.hp.hpl.jena.rdf.model.Resource adapterResource;
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
    
    public MightyRobotAdapter() {
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
        mightyRobotPropertyDancing = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "dancing"), XSD.xboolean);
        adapterControlProperties.add(mightyRobotPropertyDancing);

        mightyRobotPropertyExploded = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "exploded"), XSD.xboolean);
        adapterControlProperties.add(mightyRobotPropertyExploded);

        mightyRobotPropertyHeadRotation = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "headRotation"), XSD.integer);
        adapterControlProperties.add(mightyRobotPropertyHeadRotation);

        mightyRobotPropertyNickname = generateProperty(modelGeneral.createProperty(this.getAdapterSpecificPrefix() +  "nickname"), XSD.xstring);
        adapterControlProperties.add(mightyRobotPropertyNickname);

        com.hp.hpl.jena.rdf.model.Resource individualMightyRobotAdapter1 = modelGeneral.createResource(this.getAdapterSpecificPrefix() +  "individualMightyRobotAdapter1");
        individualMightyRobotAdapter1.addProperty(RDF.type, adapterResource);
        individualMightyRobotAdapter1.addProperty(RDFS.label, modelGeneral.createLiteral("MightyRobot Adapter 1", "en"));
        individualMightyRobotAdapter1.addProperty(RDFS.comment, modelGeneral.createLiteral("A MightyRobot Adapter 1", "en"));
    }
    
    private Property generateProperty(Property template, com.hp.hpl.jena.rdf.model.Resource XSDType){
    	template.addProperty(RDF.type, OWL.DatatypeProperty);
    	template.addProperty(RDFS.domain, instanceClassResource);
    	template.addProperty(RDFS.range, XSDType);
    	return template;
    }


   /* @Resource(mappedName = "topic/core")
    private Topic topic;*/
    @Inject
    private JMSContext context;
    @Inject
    private MightyRobotAdapterBean mightyRobotAdapterBean;
    
    private void sendSparqlMessage(String data, String type){
        try{
            final Message message= this.context.createMessage();
            message.setStringProperty("topic", "Fuseki");
            message.setStringProperty("type",
                    type);
            message.setStringProperty("data",
            		data);
            mightyRobotAdapterBean.sendMessage(message);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Object handleCreateInstance(int instanceID){
        MightyRobot temp = new MightyRobot(this, instanceID);
		String data = 
				"INSERT DATA { <"+ instanceClassResource + temp.getInstanceID() + ">" 
				+ " \n<" + getAdapterSpecificPrefix() + "InstanceID> \"" + temp.getInstanceID() + "\" ;"
				+ " \n<" + mightyRobotPropertyDancing + "> \"" + temp.getDancing() + "\" ;"
				+ " \n<" + mightyRobotPropertyExploded + "> \"" + temp.getExploded() + "\" ;"
				+ " \n<" + mightyRobotPropertyHeadRotation + "> \"" + temp.getHeadRotation() + "\" ;"
				+ " \n<" + mightyRobotPropertyNickname + "> \"" + temp.getNickname() + "\" ;"
                + " \n<" + getAdapterSpecificPrefix() + "owningAdapter> \"" + temp.getOwningAdapter() + "\" .}";        
        
        
        sendSparqlMessage(data, "update");
        
        return temp;
    }
   
/* 
Post the entry
INSERT DATA { 
	<http://fiteagle.org/ontology/adapter/mightyrobot#MightyRobotResource> 
		<http://fiteagle.org/ontology/adapter/mightyrobot#InstanceID> "1" ; 
		<http://fiteagle.org/ontology/adapter/mightyrobot#dancing> "false" ; 
		<http://fiteagle.org/ontology/adapter/mightyrobot#exploded> "false" ; 
		<http://fiteagle.org/ontology/adapter/mightyrobot#headRotation> "0" ; 
		<http://fiteagle.org/ontology/adapter/mightyrobot#nickname> "Mecha" ; 
		<http://fiteagle.org/ontology/adapter/mightyrobot#owningAdapter> "org.fiteagle.adapters.mightyrobot.MightyRobotAdapter@13ad3f4" .
} 

Get the entry
PREFIX mr: <http://fiteagle.org/ontology/adapter/mightyrobot#>
SELECT ?InstanceID ?dancing ?exploded ?headRotation ?nickname ?owningAdapter
WHERE
{
	?mightyrobot mr:InstanceID ?InstanceID .
	?mightyrobot mr:dancing ?dancing .
	?mightyrobot mr:exploded ?exploded .
	?mightyrobot mr:headRotation ?headRotation .
	?mightyrobot mr:nickname ?nickname .
	?mightyrobot mr:owningAdapter ?owningAdapter .
}

Change the nickname
PREFIX mr: <http://fiteagle.org/ontology/adapter/mightyrobot#>
DELETE { ?mightyrobot mr:nickname "Mecha" }
INSERT { ?mightyrobot mr:nickname "Toni" }
WHERE { ?mightyrobot mr:nickname "Mecha" } 

Delete the entry
DELETE
{
?mightyrobot ?property ?value 
}
WHERE
{
?mightyrobot ?property ?value ; <http://fiteagle.org/ontology/adapter/mightyrobot#InstanceID> "1"
}	

Delete the entry
DELETE
{
?mightyrobot ?property ?value 
}
WHERE
{
?mightyrobot ?property ?value ; <http://fiteagle.org/ontology/adapter/mightyrobot#InstanceID> "5"
}	
*/		
  
    @Override
    public void handleTerminateInstance(int instanceID){

    	String data = 
    			"DELETE { ?mightyrobot ?property ?value } \n" +
    					"WHERE { ?mightyrobot ?property ?value ; " +
    					"<http://fiteagle.org/ontology/adapter/mightyrobot#InstanceID> \"" +
    					instanceID + 
    					"\"}";
    	
    	sendSparqlMessage(data, "update");

    }
    
    @Override
    public Model handleMonitorInstance(int instanceID, Model modelInstances){
    	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceID);

        com.hp.hpl.jena.rdf.model.Resource mightyRobotInstance = modelInstances.createResource(this.getAdapterSpecificPrefix() +  "m" + instanceID);
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

		// db messaging should not happen here, it is not needed as the adapter keeps track of its resources without querying the db for their status
		// this is just here for testing
		String data = 
				"PREFIX mr: <http://fiteagle.org/ontology/adapter/mightyrobot#>\n" +
					"SELECT ?InstanceID ?dancing ?exploded ?headRotation ?nickname ?owningAdapter\n"+
					"WHERE\n"+
					"{\n"+
						"?mightyrobot mr:InstanceID ?InstanceID .\n"+
						"?mightyrobot mr:dancing ?dancing .\n"+
						"?mightyrobot mr:exploded ?exploded .\n"+
						"?mightyrobot mr:headRotation ?headRotation .\n"+
						"?mightyrobot mr:nickname ?nickname .\n"+
						"?mightyrobot mr:owningAdapter ?owningAdapter .\n"+
					"}\n";	
    	sendSparqlMessage(data, "query");
    	
		for (Integer key : instanceList.keySet()) {
        	
        	MightyRobot currentMightyRobot= (MightyRobot) instanceList.get(key);

            com.hp.hpl.jena.rdf.model.Resource mightyRobotInstance = modelInstances.createResource(this.getAdapterSpecificPrefix() +  "m" + key);
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
            com.hp.hpl.jena.rdf.model.Resource currentResource = iter.nextStatement().getSubject();
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
