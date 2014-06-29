package org.fiteagle.adapters.mightyrobot;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.fiteagle.adapters.AbstractAdapter;

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

public final class MightyRobotAdapter extends AbstractAdapter{

	private String adapterSpecificPrefix = "http://fiteagle.org/ontology/adapter/mightyrobot#";
    private static MightyRobotAdapter mightyRobotAdapterSingleton; 
    
   
    public static synchronized MightyRobotAdapter getInstance() 
    { 
      if ( mightyRobotAdapterSingleton == null ) 
    	  mightyRobotAdapterSingleton = new MightyRobotAdapter(); 
      return mightyRobotAdapterSingleton; 
    } 


    private Resource motorResource;
    private Resource MightyRobotAdapter;
    private Property motorPropertyRPM;
    private Property motorPropertyMaxRPM;
    private Property motorPropertyThrottle;
    private Property motorPropertyManufacturer;

    private List<Property> motorControlProperties = new LinkedList<Property>();

    private MightyRobotAdapter() {
        modelGeneral = ModelFactory.createDefaultModel();

        modelGeneral.setNsPrefix("", "http://fiteagle.org/ontology/adapter/mightyrobot#");
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // Property instantiatesProperty = new PropertyImpl("fiteagle:instantiates");

        motorResource = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/mightyrobot#MightyRobotResource");
        motorResource.addProperty(RDF.type, OWL.Class);
        motorResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

        MightyRobotAdapter = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/mightyrobot#MightyRobotAdapter");
        MightyRobotAdapter.addProperty(RDF.type, OWL.Class);
        MightyRobotAdapter.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        MightyRobotAdapter.addProperty(modelGeneral.createProperty("http://fiteagle.org/ontology#instantiates"), motorResource);

        // create the property
        motorPropertyRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/mightyrobot#rpm");
        motorPropertyRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyRPM.addProperty(RDFS.domain, motorResource);
        motorPropertyRPM.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyRPM);

        motorPropertyMaxRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/mightyrobot#maxRpm");
        motorPropertyMaxRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyMaxRPM.addProperty(RDFS.domain, motorResource);
        motorPropertyMaxRPM.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyMaxRPM);

        motorPropertyThrottle = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/mightyrobot#throttle");
        motorPropertyThrottle.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyThrottle.addProperty(RDFS.domain, motorResource);
        motorPropertyThrottle.addProperty(RDFS.range, XSD.integer);
        motorControlProperties.add(motorPropertyThrottle);

        motorPropertyManufacturer = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/mightyrobot#manufacturer");
        motorPropertyManufacturer.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyManufacturer.addProperty(RDFS.domain, motorResource);
        motorPropertyManufacturer.addProperty(RDFS.range, XSD.xstring);

        Resource individualMightyRobotAdapter1 = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/mightyrobot#individualMightyRobotAdapter1");
        individualMightyRobotAdapter1.addProperty(RDF.type, MightyRobotAdapter);
        individualMightyRobotAdapter1.addProperty(RDFS.label, modelGeneral.createLiteral("MightyRobot Adapter 1", "en"));
        individualMightyRobotAdapter1.addProperty(RDFS.comment, modelGeneral.createLiteral("A MightyRobot Adapter 1", "en"));

    }
    
    @Override 
    public Object handleCreateInstance(){
    	return new MightyRobot(this);
    }
    
    @Override
    public Model handleMonitorInstance(int instanceID, Model modelInstances){
    	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(instanceID);

        Resource mightyRobotInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/mightyrobot#m" + instanceID);
        mightyRobotInstance.addProperty(RDF.type, motorResource);
        mightyRobotInstance.addProperty(RDFS.label, "" + instanceID);
        mightyRobotInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("MightyRobot in the garage " + instanceID, "en"));
        mightyRobotInstance.addLiteral(motorPropertyRPM, currentMightyRobot.getRpm());
        mightyRobotInstance.addLiteral(motorPropertyMaxRPM, currentMightyRobot.getMaxRpm());
        mightyRobotInstance.addLiteral(motorPropertyThrottle, currentMightyRobot.getThrottle());
        mightyRobotInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");
        
        return modelInstances;
    }

	@Override
	public Model handleGetAllInstances(Model modelInstances) {
		for (Integer key : instanceList.keySet()) {
        	
        	MightyRobot currentMightyRobot= (MightyRobot) instanceList.get(key);

            Resource mightyRobotInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/mightyrobot#m" + key);
            mightyRobotInstance.addProperty(RDF.type, motorResource);
            mightyRobotInstance.addProperty(RDFS.label, "" + key);
            mightyRobotInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("MightyRobot in the garage " + key, "en"));
            mightyRobotInstance.addLiteral(motorPropertyRPM, currentMightyRobot.getRpm());
            mightyRobotInstance.addLiteral(motorPropertyMaxRPM, currentMightyRobot.getMaxRpm());
            mightyRobotInstance.addLiteral(motorPropertyThrottle, currentMightyRobot.getThrottle());
            mightyRobotInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");
        }
		return modelInstances;
	}

	@Override
	public String handleControlInstance(Model model2) {
        StringWriter sw = new StringWriter();

        StmtIterator iter = model2.listStatements(new SimpleSelector(null, RDF.type, motorResource));
        while (iter.hasNext()) {
            Resource currentResource = iter.nextStatement().getSubject();
            // sw.write(currentResource.getProperty(RDFS.label).getObject().toString());
            int key = Integer.parseInt(currentResource.getProperty(RDFS.label).getObject().toString());
            if (instanceList.containsKey(key)) {
            	MightyRobot currentMightyRobot = (MightyRobot) instanceList.get(key);

                for (Property currentProperty : motorControlProperties) {
                    StmtIterator iter2 = currentResource.listProperties(currentProperty);

                    while (iter2.hasNext()) {
                        int value = (int) iter2.nextStatement().getObject().asLiteral().getLong();

                        if (currentProperty == motorPropertyRPM) {
                        	currentMightyRobot.setRpm(value);
                        } else if (currentProperty == motorPropertyMaxRPM) {
                        	currentMightyRobot.setMaxRpm(value);
                        } else if (currentProperty == motorPropertyThrottle) {
                        	currentMightyRobot.setThrottle(value);
                        }

                        sw.write("Changed mightyrobot instance " + key + " property " + currentProperty.toString() + " to value " + value + "\n");
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
