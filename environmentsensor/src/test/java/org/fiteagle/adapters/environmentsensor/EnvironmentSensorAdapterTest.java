package org.fiteagle.adapters.environmentsensor;

import info.openmultinet.ontology.Parser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.riot.Lang;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import junit.framework.Assert;

public class EnvironmentSensorAdapterTest {

    private static final String URN_MOTOR_1 = "urn:environmentsensor-1";

    @Test
    public void testUpdateEnvironmentSensor() throws InstanceNotFoundException {
	final Model defaultModel = ModelFactory.createDefaultModel();
	final Property rpmProp = defaultModel.createProperty("http://open-multinet.info/ontology/resource/environmentsensor#rpm");
	final InputStream tbox = EnvironmentSensorAdapterTest.class.getResourceAsStream("/ontologies/environmentsensor.ttl");
	final InputStream cfg = EnvironmentSensorAdapterTest.class.getResourceAsStream("/updateEnvironmentSensor.ttl");

	final Model adapterTBox = defaultModel.read(tbox, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
	final Resource adapterABox = defaultModel.createResource("FS20-1");

	final EnvironmentSensorAdapter adapter = new EnvironmentSensorAdapter(adapterTBox, adapterABox);
	Model model = ModelFactory.createDefaultModel();
	System.out.println(Parser.toString(model));
	System.out.println("****************************");
	Model blah = adapter.createInstance(URN_MOTOR_1, ModelFactory.createDefaultModel());
	
	System.out.println(Parser.toString(blah));
	
	Model environmentsensor = adapter.getInstance(URN_MOTOR_1);

	Assert.assertEquals(0, environmentsensor.getResource(URN_MOTOR_1).getProperty(rpmProp).getInt());

	final Model cfgModel = defaultModel.read(cfg, StandardCharsets.UTF_8.name(), Lang.TTL.getName());

	environmentsensor = adapter.updateInstance(URN_MOTOR_1, cfgModel);
	Assert.assertEquals(111, environmentsensor.getResource(URN_MOTOR_1).getProperty(rpmProp).getInt());
    }
}
