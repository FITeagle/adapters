package org.fiteagle.adapters.epc;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.jena.riot.Lang;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import junit.framework.Assert;

public class EpcTest {

	@Test
	public void testEpc() throws UnsupportedEncodingException {

		final String instanceName = "http://foo";
		final EpcAdapter owningAdapter = null;
		final Epc epc = new Epc(owningAdapter, instanceName);
		// epc.setThrottle(10);
		// Assert.assertEquals(10, epc.getThrottle());
		Assert.assertEquals(instanceName, epc.getInstanceName());
		// Assert.assertFalse(epc.getManufacturer().isEmpty());

		// Assert.assertFalse(epc.getRpm() == 90);
		// @SuppressWarnings("PMD.CloseResource")
		// final Statement statement = getStatement("<" + instanceName +
		// "> <rpm> 90");
		// epc.updateProperty(statement);
		// Assert.assertTrue(epc.getRpm() == 90);
	}

	// private Statement getStatement(final String string)
	// throws UnsupportedEncodingException {
	// final ByteArrayInputStream bais = new ByteArrayInputStream(
	// string.getBytes(StandardCharsets.UTF_8));
	// final Model model = ModelFactory.createDefaultModel();
	// model.read(bais, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
	// return model.listStatements().next();
	// }

}
