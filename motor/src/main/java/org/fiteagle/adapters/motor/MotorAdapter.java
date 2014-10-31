package org.fiteagle.adapters.motor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModels;

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

public final class MotorAdapter extends AbstractAdapter {

    //private static final String[] ADAPTER_SPECIFIC_PREFIX = {"motorgarage","http://open-multinet.info/ontology/resource/motorgarage#"};
	private static final String[] ADAPTER_SPECIFIC_PREFIX = new String[2];
	
	//private static final String[] ADAPTER_MANAGED_RESOURCE_PREFIX = {"motor","http://open-multinet.info/ontology/resource/motor#"};
	private static final String[] ADAPTER_MANAGED_RESOURCE_PREFIX = new String[2];
	
	//private final String[] ADAPTER_INSTANCE_PREFIX = {"av","http://federation.av.tu-berlin.de/about#"};
	private final String[] ADAPTER_INSTANCE_PREFIX = new String[2];

    private Model adapterModel;
    private Resource adapterInstance;
    private static Resource adapter;
    private static Resource resource;
    private String adapterName;
    
/*    private static MotorAdapter motorAdapterSingleton;

    public static synchronized MotorAdapter getInstance() {
        if (motorAdapterSingleton == null)
            motorAdapterSingleton = new MotorAdapter();
        return motorAdapterSingleton;
    }
*/
    public static HashMap<String,MotorAdapter> motorAdapterInstances = new HashMap<>();
    
    public static MotorAdapter getInstance(String URI){
      return motorAdapterInstances.get(URI);
    }
    
    private Property motorPropertyRPM;
    private Property motorPropertyMaxRPM;
    private Property motorPropertyThrottle;
    private Property motorPropertyManufacturer;
    private Property motorPropertyIsDynamic;

    private static List<Property> motorControlProperties = new LinkedList<Property>();
    
    static {
        Model adapterModel = OntologyModels.loadModel("ontologies/motor.ttl", IMessageBus.SERIALIZATION_TURTLE);
        
        StmtIterator adapterIterator = adapterModel.listStatements(null, RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
        if (adapterIterator.hasNext()) {
          adapter = adapterIterator.next().getSubject();
          ADAPTER_SPECIFIC_PREFIX[1] = adapter.getNameSpace();
          ADAPTER_SPECIFIC_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_SPECIFIC_PREFIX[1]);
        }
        
        StmtIterator resourceIterator = adapterModel.listStatements(adapter, MessageBusOntologyModel.propertyFiteagleImplements, (Resource) null);
        if (resourceIterator.hasNext()) {
          resource = resourceIterator.next().getObject().asResource();
          ADAPTER_MANAGED_RESOURCE_PREFIX[1] = resource.getNameSpace();
          ADAPTER_MANAGED_RESOURCE_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_MANAGED_RESOURCE_PREFIX[1]);
        }
        
        StmtIterator propertiesIterator = adapterModel.listStatements(null, RDFS.domain, resource);
        while (propertiesIterator.hasNext()) {
          Property p = adapterModel.getProperty(propertiesIterator.next().getSubject().getURI());
          motorControlProperties.add(p);
        }
        
        StmtIterator adapterInstanceIterator = adapterModel.listStatements(null, RDF.type, adapter);
        while (adapterInstanceIterator.hasNext()) {
          Resource adapterInstance = adapterInstanceIterator.next().getSubject();
          
          MotorAdapter motorAdapter = new MotorAdapter(adapterInstance, adapterModel);
          
          motorAdapterInstances.put(adapterInstance.getURI(), motorAdapter);
        }
      }

    private MotorAdapter(Resource adapterInstance, Model adapterModel) {
        
        this.adapterInstance = adapterInstance;
        this.adapterModel = adapterModel;
        
        ADAPTER_INSTANCE_PREFIX[1] = adapterInstance.getNameSpace();
        ADAPTER_INSTANCE_PREFIX[0] = adapterModel.getNsURIPrefix(ADAPTER_INSTANCE_PREFIX[1]);
/*        
        adapterName = "MotorGarage-1";
        
        adapterModel = ModelFactory.createDefaultModel();

        adapterModel.setNsPrefix("omn","http://open-multinet.info/ontology/omn#");
        adapterModel.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        adapterModel.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        adapterModel.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        adapterModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        adapterModel.setNsPrefix("av","http://federation.av.tu-berlin.de/about#");

        resource = adapterModel.createResource(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"Motor");
        resource.addProperty(RDF.type, OWL.Class);
        resource.addProperty(RDFS.subClassOf, adapterModel.createResource(MessageBusOntologyModel.classResource));


        adapter = adapterModel.createResource(ADAPTER_SPECIFIC_PREFIX[1]+"MotorGarage");
        adapter.addProperty(RDF.type, OWL.Class);
        adapter.addProperty(RDFS.subClassOf, adapterModel.createResource(MessageBusOntologyModel.classAdapter));

        adapter.addProperty(MessageBusOntologyModel.propertyFiteagleImplements, resource);
        adapter.addProperty(RDFS.label, adapterModel.createLiteral("MotorGarageAdapterType ", "en"));

        resource.addProperty(MessageBusOntologyModel.propertyFiteagleImplementedBy, adapter);
        resource.addProperty(RDFS.label, adapterModel.createLiteral("MotorResource", "en"));

        // create the properties
        motorPropertyRPM = adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"rpm");
        motorPropertyRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyRPM.addProperty(RDFS.domain, resource);
        motorPropertyRPM.addProperty(RDFS.range, XSD.integer);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: RPM", "en");
        motorControlProperties.add(motorPropertyRPM);

        motorPropertyMaxRPM = adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"maxRpm");
        motorPropertyMaxRPM.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyMaxRPM.addProperty(RDFS.domain, resource);
        motorPropertyMaxRPM.addProperty(RDFS.range, XSD.integer);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: Max. RPM", "en");
        motorControlProperties.add(motorPropertyMaxRPM);

        motorPropertyThrottle = adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"throttle");
        motorPropertyThrottle.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyThrottle.addProperty(RDFS.domain, resource);
        motorPropertyThrottle.addProperty(RDFS.range, XSD.integer);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: Throttle", "en");
        motorControlProperties.add(motorPropertyThrottle);



        motorPropertyIsDynamic = adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"isDynamic");
        motorPropertyIsDynamic.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyIsDynamic.addProperty(RDFS.domain, resource);
        motorPropertyIsDynamic.addProperty(RDFS.range, XSD.xboolean);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: isDynamic", "en");
        motorControlProperties.add(motorPropertyIsDynamic);

        motorPropertyManufacturer = adapterModel.createProperty(ADAPTER_MANAGED_RESOURCE_PREFIX[1]+"manufacturer");
        motorPropertyManufacturer.addProperty(RDF.type, OWL.DatatypeProperty);
        motorPropertyManufacturer.addProperty(RDFS.domain, resource);
        motorPropertyRPM.addProperty(RDFS.label, "Motor Property: Manufacturer", "en");
        motorPropertyManufacturer.addProperty(RDFS.range, XSD.xstring);
        motorControlProperties.add(motorPropertyManufacturer);

        adapterInstance = adapterModel.createResource(ADAPTER_INSTANCE_PREFIX[1] + adapterName);
        adapterInstance.addProperty(RDF.type, adapter);
        adapterInstance.addProperty(RDFS.label, adapterModel.createLiteral("A deployed motor garage adapter named: " + adapterName, "en"));
        adapterInstance.addProperty(RDFS.comment, adapterModel.createLiteral("A motor garage adapter that can simulate different dynamic motor resources.", "en"));
        //wgs coordinates
        adapterInstance.addProperty(adapterModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat"), "52.516377");
        adapterInstance.addProperty(adapterModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long"), "13.323732");
 */       
        // Testbed name with omn:partOfGroup
        //adapterInstance.addProperty(adapterModel.createProperty("http://open-multinet.info/ontology/omn#partOfGroup"),adapterModel.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed"));
        
        // adding resource property to the description.
/*        for(Property prob : motorControlProperties){
        	adapterInstance.addProperty(RDFS.member, prob);
        }*/
        
    }

    @Override
    public Object handleCreateInstance(String instanceName, Map<String, String> properties) {
        return new DynamicMotor(this, instanceName);
    }

    @Override
    public Model handleMonitorInstance(String instanceName, Model modelInstances) {
        Motor currentMotor = (Motor) instanceList.get(instanceName);

        Resource motorInstance = modelInstances.createResource(ADAPTER_INSTANCE_PREFIX[1] + instanceName);
        addPropertiesToResource(motorInstance, currentMotor, instanceName);

        return modelInstances;
    }

    @Override
    public Model handleGetAllInstances(Model modelInstances) {
        for (String key : instanceList.keySet()) {

            Motor currentMotor = (Motor) instanceList.get(key);

            Resource motorInstance = modelInstances.createResource(ADAPTER_INSTANCE_PREFIX[1] + key);
            addPropertiesToResource(motorInstance, currentMotor, key);
        }
        return modelInstances;
    }

    public void addPropertiesToResource(Resource motorInstance, Motor currentMotor, String instanceName) {
        motorInstance.addProperty(RDF.type, resource);
        motorInstance.addProperty(RDFS.label, "Motor: " + instanceName);
        motorInstance.addProperty(RDFS.comment, adapterModel.createLiteral("Motor in the garage " + instanceName, "en"));
        motorInstance.addLiteral(motorPropertyRPM, currentMotor.getRpm());
        motorInstance.addLiteral(motorPropertyMaxRPM, currentMotor.getMaxRpm());
        motorInstance.addLiteral(motorPropertyThrottle, currentMotor.getThrottle());
        motorInstance.addLiteral(motorPropertyManufacturer, "Fraunhofer FOKUS");
        motorInstance.addLiteral(motorPropertyIsDynamic, ((DynamicMotor) currentMotor).isDynamic());
    }

    @Override
    public List<String> configureInstance(Statement configureStatement) {

        Resource currentResource = configureStatement.getSubject();
        String instanceName = currentResource.getLocalName();

        List<String> updatedProperties = new LinkedList<String>();

        if (instanceList.containsKey(instanceName)) {
            Motor currentMotor = (Motor) instanceList.get(instanceName);

            for (Property currentProperty : motorControlProperties) {
                StmtIterator iter2 = currentResource.listProperties(currentProperty);

                while (iter2.hasNext()) {

                    if (currentProperty == motorPropertyRPM) {
                        currentMotor.setRpm((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == motorPropertyMaxRPM) {
                        currentMotor.setMaxRpm((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == motorPropertyThrottle) {
                        currentMotor.setThrottle((int) iter2.nextStatement().getObject().asLiteral().getLong(), updatedProperties);
                    } else if (currentProperty == motorPropertyIsDynamic) {
                        ((DynamicMotor) currentMotor).setIsDynamic(iter2.nextStatement().getObject().asLiteral().getBoolean(), updatedProperties);
                    }
                }
            }

        }

        return updatedProperties;
    }

    @Override
    public String[] getAdapterSpecificPrefix() {
        return ADAPTER_SPECIFIC_PREFIX.clone();
    }
    
    @Override
    public String[] getAdapterManagedResourcePrefix() {
      return ADAPTER_MANAGED_RESOURCE_PREFIX.clone();
    }

    @Override
    public String[] getAdapterInstancePrefix() {
      return ADAPTER_INSTANCE_PREFIX.clone();
    }

    public Motor getInstanceName(String instanceName) {
        return (Motor) instanceList.get(instanceName);
    }
    
    @Override
    public Resource getAdapterManagedResource() {
      return resource;
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
    
    @Override
    public void updateAdapterDescription() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void handleTerminateInstance(String instanceName) {
      // TODO Auto-generated method stub
      
    }
}
