package org.fiteagle.adapters.motor;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.jena.riot.Lang;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import junit.framework.Assert;

public class MotorTest {

    @Test
    public void testMotor() throws UnsupportedEncodingException {

	final String instanceName = "http://foo";
	final MotorAdapter owningAdapter = null;
	final Motor motor = new Motor(owningAdapter, instanceName);
	motor.setThrottle(10);
	Assert.assertEquals(10, motor.getThrottle());
	Assert.assertEquals(instanceName, motor.getInstanceName());
	Assert.assertFalse(motor.getManufacturer().isEmpty());
	
	Assert.assertFalse(motor.getRpm() == 90);
	@SuppressWarnings("PMD.CloseResource")
	final Statement statement = getStatement("<" + instanceName + "> <rpm> 90");
	motor.updateProperty(statement);
	Assert.assertTrue(motor.getRpm() == 90);
    }

    private Statement getStatement(final String string) throws UnsupportedEncodingException {
	final ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
	final Model model = ModelFactory.createDefaultModel();
	model.read(bais, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
	return model.listStatements().next();
    }

}
