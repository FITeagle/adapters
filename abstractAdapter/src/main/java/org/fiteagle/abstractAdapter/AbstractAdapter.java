package org.fiteagle.abstractAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Abstract class defining the basics all the current adapters are following
 * Extend this class and implement the abstract methods to get this to work
 */
public abstract class AbstractAdapter {
	
    public static final String PARAM_TURTLE = "TURTLE";
    public static final String PARAM_RDFXML = "RDF/XML";
    public static final String PARAM_NTRIPLE = "N-TRIPLE";	
	
	private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
    protected HashMap<Integer, Object> instanceList = new HashMap<Integer, Object>();	
    
	protected Model modelGeneral = ModelFactory.createDefaultModel();

    public String getAdapterDescription(String serializationFormat){
        StringWriter writer = new StringWriter();

        modelGeneral.write(writer, serializationFormat);

        return writer.toString();
    }
    
    public boolean createInstance(int instanceID){

        if (instanceList.containsKey(instanceID)) {
            return false;
        }

        // handling done by adapter, handleCreateInstance has to be implemented by all subclasses!
    	Object newInstance = handleCreateInstance(instanceID);    

        instanceList.put(instanceID, newInstance);
        
        notifyListeners(newInstance, "provisioned:" + instanceID + "::0;;" + " (ID: " + instanceID + ")", "null", "" + instanceID);

        return true;

    }


    public boolean terminateInstance(int instanceID){

        if (instanceList.containsKey(instanceID)) {
            notifyListeners(instanceList.get(instanceID), "terminated:"+ instanceID + ";; " + " (ID: " + instanceID + ")", "" + instanceID, "null");
            instanceList.remove(instanceID);
            return true;
        }

        return false;
    }

    public String monitorInstance(int instanceID, String serializationFormat){
    	Model modelInstances = ModelFactory.createDefaultModel();

        modelInstances.setNsPrefix("", getAdapterSpecificPrefix());
        modelInstances.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelInstances.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelInstances.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelInstances.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelInstances.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");


        if (instanceList.containsKey(instanceID)) {
            // handling done by adapter, handleMonitorInstance has to be implemented by all subclasses!
        	modelInstances = handleMonitorInstance(instanceID, modelInstances);      	
        }

        StringWriter writer = new StringWriter();

        modelInstances.write(writer, serializationFormat);

        return writer.toString();

    }
    
    public String getAllInstances(String serializationFormat){
    	Model modelInstances = ModelFactory.createDefaultModel();

        modelInstances.setNsPrefix("", getAdapterSpecificPrefix());
        modelInstances.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelInstances.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelInstances.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelInstances.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelInstances.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // handling done by adapter, handleGetAllInstances has to be implemented by all subclasses!
        modelInstances = handleGetAllInstances(modelInstances);

        StringWriter writer = new StringWriter();

        modelInstances.write(writer, serializationFormat);

        return writer.toString();
    }

    public String controlInstance(InputStream in, String serializationFormat){

        // create an empty model
        Model model2 = ModelFactory.createDefaultModel();

        // read the RDF/XML file
        model2.read(in, null, serializationFormat);

        // handling done by adapter, handleControlInstance has to be implemented by all subclasses!
        return handleControlInstance(model2);

    }
    
    public void notifyListeners(Object object, String property, String oldValue, String newValue) {
        //System.err.println("sending event to " + listener.size() + " listeners");
        
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }
    
    
    

    public boolean addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
        return true;
    }   
    
    /**
     * Needs to return a new instance of the class this adapter is supposed to handle
     * @return Object - the newly created instance
     */
    public abstract Object handleCreateInstance(int instanceID);
    
    /**
     * Needs to return the base class for this adapter's instance objects as a String
     * @return String containing the name of the instance class
     */
    public abstract String getInstanceClassName();
    
    /**
     * Returns a String containing the prefix that should be applied for the case "" for this specific adapter
     * e.g. http://fiteagle.org/ontology/adapter/motor#
     * @return String containing the prefix to be applied
     */
    public abstract String getAdapterSpecificPrefix();
    
    /**
     * Handles the monitoring of resources specifically for this adapter
     */
    public abstract Model handleMonitorInstance(int instanceID, Model modelInstances);
    
    /**
     * Handles the getting of all instance for this adapter
     */
    public abstract Model handleGetAllInstances(Model modelInstances);   
    
    /**
     * Handles the controlling of a specific instance for this adapter
     */
    public abstract String handleControlInstance(Model model2);

}
