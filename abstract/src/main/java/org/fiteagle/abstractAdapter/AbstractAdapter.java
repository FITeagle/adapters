package org.fiteagle.abstractAdapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.fiteagle.abstractAdapter.AdapterEventListener;

/**
 * Abstract class defining the basics all the current adapters are following Extend this class and implement the abstract methods to get this to work
 */
public abstract class AbstractAdapter {

    public static final String PARAM_TURTLE = "TURTLE";
    public static final String PARAM_RDFXML = "RDF/XML";
    public static final String PARAM_NTRIPLE = "N-TRIPLE";

    private List<AdapterEventListener> listener = new ArrayList<AdapterEventListener>();
    protected HashMap<String, Object> instanceList = new HashMap<String, Object>();

    protected Model modelGeneral = ModelFactory.createDefaultModel();

    public String getAdapterDescription(String serializationFormat) {
        StringWriter writer = new StringWriter();

        modelGeneral.write(writer, serializationFormat);

        return writer.toString();
    }

    public Model getAdapterDescriptionModel(String serializationFormat) {    
        Model newModel = ModelFactory.createDefaultModel();
        newModel.add(modelGeneral);
        newModel.setNsPrefixes(modelGeneral.getNsPrefixMap());
        return newModel;
    }

    public boolean createInstance(String instanceName) {

        if (instanceList.containsKey(instanceName)) {
            return false;
        }

        // handling done by adapter, handleCreateInstance has to be implemented by all subclasses!
        Object newInstance = handleCreateInstance(instanceName);

        instanceList.put(instanceName, newInstance);

        notifyListeners(createInformRDF(instanceName));

        return true;
    }

    public boolean terminateInstance(String instanceName) {

        if (instanceList.containsKey(instanceName)) {
            // TODO: release event message
            // notifyListeners(instanceList.get(instanceID), "terminated:"+ instanceID + ";; " + " (ID: " + instanceID + ")", "" + instanceID, "null");
            instanceList.remove(instanceName);
            return true;
        }

        return false;
    }

    public String monitorInstance(String instanceName, String serializationFormat) {
        Model modelInstances = getSingleInstanceModel(instanceName);
        StringWriter writer = new StringWriter();

        modelInstances.write(writer, serializationFormat);

        return writer.toString();
    }

    private Model getSingleInstanceModel(String instanceName) {
        Model modelInstances = ModelFactory.createDefaultModel();

        setModelPrefixes(modelInstances);

        if (instanceList.containsKey(instanceName)) {
            // handling done by adapter, handleMonitorInstance has to be implemented by all subclasses!
            modelInstances = handleMonitorInstance(instanceName, modelInstances);
        }

        return modelInstances;
    }

    public String getAllInstances(String serializationFormat) {

        StringWriter writer = new StringWriter();

        getAllInstancesModel(serializationFormat).write(writer, serializationFormat);

        return writer.toString();
    }
    
    public Model getAllInstancesModel(String serializationFormat) {
        Model modelInstances = ModelFactory.createDefaultModel();

        setModelPrefixes(modelInstances);

        // handling done by adapter, handleGetAllInstances has to be implemented by all subclasses!
        modelInstances = handleGetAllInstances(modelInstances);

        return modelInstances;
    }
    

    private void setModelPrefixes(Model model) {
        model.setNsPrefix("", "http://fiteagleinternal#");
        model.setNsPrefix(getAdapterSpecificPrefix()[0], getAdapterSpecificPrefix()[1]);
        model.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    }

    public String controlInstance(String controlInput, String serializationFormat) {

        // create an empty model
        Model model2 = ModelFactory.createDefaultModel();

        InputStream is = new ByteArrayInputStream(controlInput.getBytes());

        // read the RDF/XML file
        model2.read(is, null, serializationFormat);

        // handling done by adapter, handleControlInstance has to be implemented by all subclasses!
        return handleControlInstance(model2);
    }
    
    public String controlInstance(Model controlModel) {
        // handling done by adapter, handleControlInstance has to be implemented by all subclasses!
        return handleControlInstance(controlModel);
    }

    public void notifyListeners(Model eventRDF) {
        // System.err.println("sending event to " + listener.size() + " listeners");

        for (AdapterEventListener name : listener) {
            name.rdfChange(eventRDF);
        }
    }

    public Model createInformRDF(String instanceName) {
        return getSingleInstanceModel(instanceName);
    }

    public boolean addChangeListener(AdapterEventListener newListener) {
        listener.add(newListener);
        return true;
    }

    /**
     * Needs to return a new instance of the class this adapter is supposed to handle
     * 
     * @return Object - the newly created instance
     */
    public abstract Object handleCreateInstance(String instanceName);

    /**
     * Needs to return the base class for this adapter's instance objects as a String
     * 
     * @return String containing the name of the instance class
     */
    public abstract String getInstanceClassName();

    /**
     * Returns a String containing the prefix that should be applied for the case "" for this specific adapter e.g. http://fiteagle.org/ontology/adapter/motor#
     * 
     * @return String containing the prefix to be applied
     */
    public abstract String[] getAdapterSpecificPrefix();

    /**
     * Handles the monitoring of resources specifically for this adapter
     */
    public abstract Model handleMonitorInstance(String instanceName, Model modelInstances);

    /**
     * Handles the getting of all instance for this adapter
     */
    public abstract Model handleGetAllInstances(Model modelInstances);

    /**
     * Handles the controlling of a specific instance for this adapter
     */
    public abstract String handleControlInstance(Model model2);

}
