package org.fiteagle.adapters.epc;

import com.hp.hpl.jena.vocabulary.RDF;

import info.openmultinet.ontology.Parser;
import info.openmultinet.ontology.vocabulary.Epc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import junit.framework.Assert;

import org.apache.jena.riot.Lang;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class EpcAdapterTest {

	private static final String URN_USER_EQUIPMENT_1 = "urn:ue1";

	@Test
	public void testUpdateEpc() throws InstanceNotFoundException {

		// set up adapter instance
		final InputStream tbox = EpcAdapterTest.class
				.getResourceAsStream("/ontologies/epc.ttl");
		final Model defaultModel = ModelFactory.createDefaultModel();
		final Model adapterTBox = defaultModel.read(tbox,
				StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		final Resource adapterABox = defaultModel
				.createResource("http://www.test.com/EpcAdapter-1");
		final EpcAdapter adapter = new EpcAdapter(adapterTBox, adapterABox);

		// create model
		// <urn:ue1> a epc:UserEquipment .
		Model model = ModelFactory.createDefaultModel();
		Resource epcResource = model.createResource(URN_USER_EQUIPMENT_1);
		Statement statement = model.createStatement(epcResource, RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.UserEquipment);
		model.add(statement);

		// create an instance of user equipment and check that lteSupport has
		// the default value assigned at creation (false)
		adapter.createInstance(URN_USER_EQUIPMENT_1, model);
		Model epc = adapter.getInstance(URN_USER_EQUIPMENT_1);
		Resource epcDetails = epc.getResource(URN_USER_EQUIPMENT_1)
				.getProperty(Epc.hasUserEquipment).getObject().asResource();
		Assert.assertEquals(false, epcDetails.getProperty(Epc.lteSupport)
				.getObject().asLiteral().getBoolean());

		// get the update model, carry out the update and check that lteSupport
		// has the new value (true)
		final Model cfgModel = ModelFactory.createDefaultModel();
		final InputStream cfg = EpcAdapterTest.class
				.getResourceAsStream("/updateEpc.ttl");
		cfgModel.read(cfg, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		epc = adapter.updateInstance(URN_USER_EQUIPMENT_1, cfgModel);
		Resource epcDetailsNew = epc.getResource(URN_USER_EQUIPMENT_1)
				.getProperty(Epc.hasUserEquipment).getObject().asResource();
		Assert.assertEquals(true, epcDetailsNew.getProperty(Epc.lteSupport)
				.getObject().asLiteral().getBoolean());
	}
}