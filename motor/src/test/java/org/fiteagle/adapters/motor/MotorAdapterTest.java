package org.fiteagle.adapters.motor;

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

public class MotorAdapterTest {

    private static final String URN_MOTOR_1 = "urn:motor-1";

    @Test
    public void testUpdateMotor() throws InstanceNotFoundException {
	final Model defaultModel = ModelFactory.createDefaultModel();
	final Property rpmProp = defaultModel.createProperty("http://open-multinet.info/ontology/resource/motor#rpm");
	final InputStream tbox = MotorAdapterTest.class.getResourceAsStream("/ontologies/motor.ttl");
	final InputStream cfg = MotorAdapterTest.class.getResourceAsStream("/updateMotor.ttl");

	final Model adapterTBox = defaultModel.read(tbox, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
	final Resource adapterABox = defaultModel.createResource("MotorGarage-1");

	final MotorAdapter adapter = new MotorAdapter(adapterTBox, adapterABox);
	adapter.createInstance(URN_MOTOR_1, ModelFactory.createDefaultModel());
	Model motor = adapter.getInstance(URN_MOTOR_1);

	Assert.assertEquals(0, motor.getResource(URN_MOTOR_1).getProperty(rpmProp).getInt());

	final Model cfgModel = defaultModel.read(cfg, StandardCharsets.UTF_8.name(), Lang.TTL.getName());

	motor = adapter.updateInstance(URN_MOTOR_1, cfgModel);
	Assert.assertEquals(111, motor.getResource(URN_MOTOR_1).getProperty(rpmProp).getInt());
    }
}