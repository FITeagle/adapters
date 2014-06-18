package org.fiteagle.adapters.motor;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;


public class MotorAdapter implements IMotorAdapter {
       
    HashMap<Integer, Motor> motorList = new HashMap<Integer, Motor>(); 
    
    private Model modelGeneral;
    private Resource motorResource;
    private Resource motorAdapter;
    private Property motorPropertyRPM;
    private Property motorPropertyMaxRPM;
    private Property motorPropertyThrottle;
    private Property motorPropertyManufacturer;
    
    private List<Property> motorControlProperties = new LinkedList<Property>();
    
    public MotorAdapter() {
        modelGeneral = ModelFactory.createDefaultModel();       
        
        modelGeneral.setNsPrefix("" , "http://fiteagle.org/ontology/adapter/motor#");
        modelGeneral.setNsPrefix( "fiteagle", "http://fiteagle.org/ontology#" );
        modelGeneral.setNsPrefix( "owl", "http://www.w3.org/2002/07/owl#" );
        modelGeneral.setNsPrefix( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" );
        modelGeneral.setNsPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
        modelGeneral.setNsPrefix( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" );
    
       // Property instantiatesProperty = new PropertyImpl("fiteagle:instantiates");

        motorResource = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#MotorResource");
        motorResource.addProperty(RDF.type, OWL.Class);
        motorResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));
                
        motorAdapter = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#MotorAdapter");
        motorAdapter.addProperty(RDF.type, OWL.Class);
        motorAdapter.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        motorAdapter.addProperty(modelGeneral.createProperty("http://fiteagle.org/ontology#instantiates"), motorResource); 
        
        // create the property
        motorPropertyRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#rpm");
        motorPropertyRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyRPM.addProperty(RDFS.domain, motorResource);
        motorPropertyRPM.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyRPM);
        
        motorPropertyMaxRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#maxRpm");
        motorPropertyMaxRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyMaxRPM.addProperty(RDFS.domain, motorResource);
        motorPropertyMaxRPM.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyMaxRPM);
        
        motorPropertyThrottle = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#throttle");
        motorPropertyThrottle.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyThrottle.addProperty(RDFS.domain, motorResource);
        motorPropertyThrottle.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyThrottle);
        
        motorPropertyManufacturer = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/motor#manufacturer");
        motorPropertyManufacturer.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyManufacturer.addProperty(RDFS.domain, motorResource);
        motorPropertyManufacturer.addProperty(RDFS.range, XSD.xstring);
        
        Resource individualMotorAdapter1 = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/motor#individualMotorAdapter1");
        individualMotorAdapter1.addProperty(RDF.type, motorAdapter);
        individualMotorAdapter1.addProperty(RDFS.label, modelGeneral.createLiteral("Motor Adapter 1", "en"));
        individualMotorAdapter1.addProperty(RDFS.comment, modelGeneral.createLiteral("A Motor Adapter 1", "en"));        
        
    }
   
    
    public String getAdapterDescription(String serializationFormat){        
        
        StringWriter writer = new StringWriter();
        
        modelGeneral.write(writer, serializationFormat);
        
        return writer.toString();
    }
    
    public boolean createMotorInstance(int motorInstanceID){     
                
        Motor newMotor = new Motor();
        
        if(motorList.containsKey(motorInstanceID)){
            return false;
        }
        
        motorList.put(motorInstanceID, newMotor);
        return true;
        
    }
    
    public boolean terminateMotorInstance(int motorInstanceID){
                
        if(motorList.containsKey(motorInstanceID)){
            motorList.remove(motorInstanceID);
            return true;
        }
        
        return false;   
    }
    
    public String monitorMotorInstance(int motorInstanceID, String serializationFormat){
        
        Model modelInstances = ModelFactory.createDefaultModel();       
        
        modelInstances.setNsPrefix("" , "http://fiteagle.org/ontology/adapter/motor#");
        modelInstances.setNsPrefix( "fiteagle", "http://fiteagle.org/ontology#" );
        modelInstances.setNsPrefix( "owl", "http://www.w3.org/2002/07/owl#" );
        modelInstances.setNsPrefix( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" );
        modelInstances.setNsPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
        modelInstances.setNsPrefix( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" );
        
        
        if(motorList.containsKey(motorInstanceID)){
            Motor currentMotor = motorList.get(motorInstanceID);
            
            Resource motorInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/motor#m" + motorInstanceID);
            motorInstance.addProperty(RDF.type, motorResource);
            motorInstance.addProperty(RDFS.label, "" + motorInstanceID);
            motorInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Motor in the garage " + motorInstanceID, "en"));
            motorInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
            motorInstance.addLiteral(motorPropertyMaxRPM, currentMotor.getMaxRpm());
            motorInstance.addLiteral(motorPropertyThrottle, currentMotor.getThrottle());
            motorInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");               
        }                             
        
        StringWriter writer = new StringWriter();
        
        modelInstances.write(writer, serializationFormat);
        
        return writer.toString();   
          
    }
    
    public String getAllMotorInstances(String serializationFormat){
        
        Model modelInstances = ModelFactory.createDefaultModel();       
        
        modelInstances.setNsPrefix("" , "http://fiteagle.org/ontology/adapter/motor#");
        modelInstances.setNsPrefix( "fiteagle", "http://fiteagle.org/ontology#" );
        modelInstances.setNsPrefix( "owl", "http://www.w3.org/2002/07/owl#" );
        modelInstances.setNsPrefix( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" );
        modelInstances.setNsPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
        modelInstances.setNsPrefix( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" );
                
        
        for (Integer key : motorList.keySet()) {
            Motor currentMotor = motorList.get(key);
            
            Resource motorInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/motor#m" + key);
            motorInstance.addProperty(RDF.type, motorResource);
            motorInstance.addProperty(RDFS.label, "" + key);
            motorInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Motor in the garage " + key, "en"));
            motorInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
            motorInstance.addLiteral(motorPropertyMaxRPM, currentMotor.getMaxRpm());
            motorInstance.addLiteral(motorPropertyThrottle, currentMotor.getThrottle());
            motorInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");            
        }       
        
        StringWriter writer = new StringWriter();
        
        modelInstances.write(writer, serializationFormat);
        
        return writer.toString();   
    }
    
    
    public String controlMotorInstance(InputStream in, String serializationFormat){
        
     // create an empty model
        Model model2 = ModelFactory.createDefaultModel();
        
       // read the RDF/XML file
       model2.read(in, null, serializationFormat);

       StringWriter sw = new StringWriter();

       StmtIterator iter = model2.listStatements(new SimpleSelector(null, RDF.type, motorResource));
       while (iter.hasNext()) {
           Resource currentResource = iter.nextStatement().getSubject();
           //sw.write(currentResource.getProperty(RDFS.label).getObject().toString());
           int key = Integer.parseInt(currentResource.getProperty(RDFS.label).getObject().toString());
           if(motorList.containsKey(key)){
               Motor currentMotor = motorList.get(key);
               
               for (Property currentProperty: motorControlProperties) {
                   StmtIterator iter2 = currentResource.listProperties(currentProperty);
                   
                   while (iter2.hasNext()) {
                       int value = (int) iter2.nextStatement().getObject().asLiteral().getLong();
                       
                       if(currentProperty == motorPropertyRPM){
                       currentMotor.setRpm(value);
                       } else if (currentProperty == motorPropertyMaxRPM){
                           currentMotor.setMaxRpm(value);                           
                       } else if(currentProperty == motorPropertyThrottle){
                           currentMotor.setThrottle(value);
                       }
                       
                       sw.write("Changed motor instance " + key + " property " + currentProperty.toString() + " to value " + value + "\n");
                   }                
               }
               
               
           }
       }
       
       
       return sw.toString();
         

    }


	@Override
	public void registerForEvents(IAdapterListener adapterDM) {
		// TODO store list of DM's
		adapterDM.onAdapterMessage("event from adapter");
	}
    
   

}
