package org.fiteagle.adapters.environmentsensor;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.jena.riot.Lang;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import junit.framework.Assert;

public class EnvironmentSensorTest {

    @Test
    public void testEnvironmentSensor() throws UnsupportedEncodingException {

	final String instanceName = "http://foo";
	final EnvironmentSensorAdapter owningAdapter = null;
	final EnvironmentSensor environmentsensor = new EnvironmentSensor(owningAdapter, instanceName);
	environmentsensor.setThrottle(10);
	Assert.assertEquals(10, environmentsensor.getThrottle());
	Assert.assertEquals(instanceName, environmentsensor.getInstanceName());
	Assert.assertFalse(environmentsensor.getManufacturer().isEmpty());
	
	Assert.assertFalse(environmentsensor.getRpm() == 90);
	@SuppressWarnings("PMD.CloseResource")
	final Statement statement = getStatement("<" + instanceName + "> <rpm> 90");
	environmentsensor.updateProperty(statement);
	Assert.assertTrue(environmentsensor.getRpm() == 90);
    }

    private Statement getStatement(final String string) throws UnsupportedEncodingException {
	final ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
	final Model model = ModelFactory.createDefaultModel();
	model.read(bais, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
	return model.listStatements().next();
    }

}
