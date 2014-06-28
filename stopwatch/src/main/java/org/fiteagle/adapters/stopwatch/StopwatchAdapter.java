package org.fiteagle.adapters.stopwatch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

public final class StopwatchAdapter implements AbstractAdapter, IStopwatchAdapter {
    
    
    private static StopwatchAdapter stopwatchAdapterSingleton;
    
   
    public static synchronized StopwatchAdapter getInstance()
    { 
      if ( stopwatchAdapterSingleton == null ) 
          stopwatchAdapterSingleton = new StopwatchAdapter();
      return stopwatchAdapterSingleton; 
    } 

    public static final String PARAM_TURTLE = "TURTLE";
    public static final String PARAM_RDFXML = "RDF/XML";
    public static final String PARAM_NTRIPLE = "N-TRIPLE";

    HashMap<Integer, Stopwatch> stopwatchList = new HashMap<Integer, Stopwatch>();

    private Model modelGeneral;
    private Resource stopwatchResource;
    private Resource stopwatchAdapter;
    private Property stopwatchPropertyRPM;
    private Property stopwatchPropertyMaxRPM;
    private Property stopwatchPropertyThrottle;
    private Property stopwatchPropertyManufacturer;

    private List<Property> stopwatchControlProperties = new LinkedList<Property>();

    // private List<IAdapterListener> listeners = new LinkedList<IAdapterListener>();

    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();

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

    private StopwatchAdapter() {
        modelGeneral = ModelFactory.createDefaultModel();

        modelGeneral.setNsPrefix("", "http://fiteagle.org/ontology/adapter/stopwatch#");
        modelGeneral.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelGeneral.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelGeneral.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelGeneral.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelGeneral.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // Property instantiatesProperty = new PropertyImpl("fiteagle:instantiates");

        stopwatchResource = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/stopwatch#StopwatchResource");
        stopwatchResource.addProperty(RDF.type, OWL.Class);
        stopwatchResource.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Resource"));

        stopwatchAdapter = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/stopwatch#StopwatchAdapter");
        stopwatchAdapter.addProperty(RDF.type, OWL.Class);
        stopwatchAdapter.addProperty(RDFS.subClassOf, modelGeneral.createResource("http://fiteagle.org/ontology#Adapter"));
        stopwatchAdapter.addProperty(modelGeneral.createProperty("http://fiteagle.org/ontology#instantiates"), stopwatchResource);

        // create the property
        stopwatchPropertyRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/stopwatch#rpm");
        stopwatchPropertyRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyRPM.addProperty(RDFS.domain, stopwatchResource);
        stopwatchPropertyRPM.addProperty(RDFS.range, XSD.integer);
        stopwatchControlProperties.add(stopwatchPropertyRPM);

        stopwatchPropertyMaxRPM = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/stopwatch#maxRpm");
        stopwatchPropertyMaxRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyMaxRPM.addProperty(RDFS.domain, stopwatchResource);
        stopwatchPropertyMaxRPM.addProperty(RDFS.range, XSD.integer);
        stopwatchControlProperties.add(stopwatchPropertyMaxRPM);

        stopwatchPropertyThrottle = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/stopwatch#throttle");
        stopwatchPropertyThrottle.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyThrottle.addProperty(RDFS.domain, stopwatchResource);
        stopwatchPropertyThrottle.addProperty(RDFS.range, XSD.integer);
        stopwatchControlProperties.add(stopwatchPropertyThrottle);

        stopwatchPropertyManufacturer = modelGeneral.createProperty("http://fiteagle.org/ontology/adapter/stopwatch#manufacturer");
        stopwatchPropertyManufacturer.addProperty(RDF.type, OWL.DatatypeProperty);
        stopwatchPropertyManufacturer.addProperty(RDFS.domain, stopwatchResource);
        stopwatchPropertyManufacturer.addProperty(RDFS.range, XSD.xstring);

        Resource individualStopwatchAdapter1 = modelGeneral.createResource("http://fiteagle.org/ontology/adapter/stopwatch#individualStopwatchAdapter1");
        individualStopwatchAdapter1.addProperty(RDF.type, stopwatchAdapter);
        individualStopwatchAdapter1.addProperty(RDFS.label, modelGeneral.createLiteral("Stopwatch Adapter 1", "en"));
        individualStopwatchAdapter1.addProperty(RDFS.comment, modelGeneral.createLiteral("A Stopwatch Adapter 1", "en"));

    }

    public String getAdapterDescription(String serializationFormat) {

        StringWriter writer = new StringWriter();

        modelGeneral.write(writer, serializationFormat);

        return writer.toString();
    }

    public boolean createInstance(int stopwatchInstanceID) {

        Stopwatch newStopwatch = new Stopwatch(this);

        if (stopwatchList.containsKey(stopwatchInstanceID)) {
            return false;
        }

        stopwatchList.put(stopwatchInstanceID, newStopwatch);
        
        notifyListeners(newStopwatch, "new instance (ID: " + stopwatchInstanceID + ")", "null", "" + stopwatchInstanceID);

        // System.out.println("created new instance");
        // for (IAdapterListener client : this.listeners) {
        // System.out.println("Sending message to adapter listener...");
        // client.onAdapterMessage("created new instance");
        // }

        return true;

    }

    public boolean terminateInstance(int stopwatchInstanceID) {

        if (stopwatchList.containsKey(stopwatchInstanceID)) {
            notifyListeners(stopwatchList.get(stopwatchInstanceID), "terminated instance (ID: " + stopwatchInstanceID + ")", "" + stopwatchInstanceID, "null");
            stopwatchList.remove(stopwatchInstanceID);
            return true;
        }

        return false;
    }

    public String monitorInstance(int stopwatchInstanceID, String serializationFormat) {

        Model modelInstances = ModelFactory.createDefaultModel();

        modelInstances.setNsPrefix("", "http://fiteagle.org/ontology/adapter/stopwatch#");
        modelInstances.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelInstances.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelInstances.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelInstances.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelInstances.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        if (stopwatchList.containsKey(stopwatchInstanceID)) {
            Stopwatch currentStopwatch = stopwatchList.get(stopwatchInstanceID);

            Resource stopwatchInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/stopwatch#m" + stopwatchInstanceID);
            stopwatchInstance.addProperty(RDF.type, stopwatchResource);
            stopwatchInstance.addProperty(RDFS.label, "" + stopwatchInstanceID);
            stopwatchInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Stopwatch in the garage " + stopwatchInstanceID, "en"));
            stopwatchInstance.addLiteral(stopwatchPropertyRPM, currentStopwatch.getRpm());
            stopwatchInstance.addLiteral(stopwatchPropertyMaxRPM, currentStopwatch.getMaxRpm());
            stopwatchInstance.addLiteral(stopwatchPropertyThrottle, currentStopwatch.getThrottle());
            stopwatchInstance.addLiteral(stopwatchPropertyManufacturer, "Fraunhofer FOKUS");
        }

        StringWriter writer = new StringWriter();

        modelInstances.write(writer, serializationFormat);

        return writer.toString();

    }

    public String getAllInstances(String serializationFormat) {

        Model modelInstances = ModelFactory.createDefaultModel();

        modelInstances.setNsPrefix("", "http://fiteagle.org/ontology/adapter/stopwatch#");
        modelInstances.setNsPrefix("fiteagle", "http://fiteagle.org/ontology#");
        modelInstances.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        modelInstances.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        modelInstances.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        modelInstances.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        for (Integer key : stopwatchList.keySet()) {
            Stopwatch currentStopwatch = stopwatchList.get(key);

            Resource stopwatchInstance = modelInstances.createResource("http://fiteagle.org/ontology/adapter/stopwatch#m" + key);
            stopwatchInstance.addProperty(RDF.type, stopwatchResource);
            stopwatchInstance.addProperty(RDFS.label, "" + key);
            stopwatchInstance.addProperty(RDFS.comment, modelGeneral.createLiteral("Stopwatch in the garage " + key, "en"));
            stopwatchInstance.addLiteral(stopwatchPropertyRPM, currentStopwatch.getRpm());
            stopwatchInstance.addLiteral(stopwatchPropertyMaxRPM, currentStopwatch.getMaxRpm());
            stopwatchInstance.addLiteral(stopwatchPropertyThrottle, currentStopwatch.getThrottle());
            stopwatchInstance.addLiteral(stopwatchPropertyManufacturer, "Fraunhofer FOKUS");
        }

        StringWriter writer = new StringWriter();

        modelInstances.write(writer, serializationFormat);

        return writer.toString();
    }

    public String controlInstance(InputStream in, String serializationFormat) {

        // create an empty model
        Model model2 = ModelFactory.createDefaultModel();

        // read the RDF/XML file
        model2.read(in, null, serializationFormat);

        StringWriter sw = new StringWriter();

        StmtIterator iter = model2.listStatements(new SimpleSelector(null, RDF.type, stopwatchResource));
        while (iter.hasNext()) {
            Resource currentResource = iter.nextStatement().getSubject();
            // sw.write(currentResource.getProperty(RDFS.label).getObject().toString());
            int key = Integer.parseInt(currentResource.getProperty(RDFS.label).getObject().toString());
            if (stopwatchList.containsKey(key)) {
                Stopwatch currentStopwatch = stopwatchList.get(key);

                for (Property currentProperty : stopwatchControlProperties) {
                    StmtIterator iter2 = currentResource.listProperties(currentProperty);

                    while (iter2.hasNext()) {
                        int value = (int) iter2.nextStatement().getObject().asLiteral().getLong();

                        if (currentProperty == stopwatchPropertyRPM) {
                            currentStopwatch.setRpm(value);
                        } else if (currentProperty == stopwatchPropertyMaxRPM) {
                            currentStopwatch.setMaxRpm(value);
                        } else if (currentProperty == stopwatchPropertyThrottle) {
                            currentStopwatch.setThrottle(value);
                        }

                        sw.write("Changed stopwatch instance " + key + " property " + currentProperty.toString() + " to value " + value + "\n");
                    }
                }

            }
        }

        return sw.toString();

    }

    // @Override
    // public void registerForEvents(IAdapterListener listener) {
    // this.listeners.add(listener);
    // }
}
