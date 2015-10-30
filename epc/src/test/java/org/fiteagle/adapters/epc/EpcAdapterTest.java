package org.fiteagle.adapters.epc;

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

public class EpcAdapterTest {

	private static final String URN_EPC_1 = "urn:ue1";

	@Test
	public void testUpdateEpc() throws InstanceNotFoundException {
		final Model defaultModel = ModelFactory.createDefaultModel();
		// final Property rpmProp =
		// defaultModel.createProperty("http://open-multinet.info/ontology/resource/epc#rpm");
		final Property lteSupportProp = Epc.lteSupport;
		final InputStream tbox = EpcAdapterTest.class
				.getResourceAsStream("/ontologies/epc-adapter.ttl");
		final InputStream cfg = EpcAdapterTest.class
				.getResourceAsStream("/updateEpc.ttl");

		final Model adapterTBox = defaultModel.read(tbox,
				StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		final Resource adapterABox = defaultModel
				.createResource("http://www.test.com/EpcAdapter-1");

		final EpcAdapter adapter = new EpcAdapter(adapterTBox, adapterABox);
		Model instance = adapter.createInstance(URN_EPC_1,
				ModelFactory.createDefaultModel());
		System.out.println(Parser.toString(instance));

		Model epc = adapter.getInstance(URN_EPC_1);
		System.out.println(Parser.toString(epc));
		Assert.assertEquals(false,
				epc.getResource(URN_EPC_1).getProperty(lteSupportProp)
						.getObject().asLiteral().getBoolean());

		final Model cfgModel = defaultModel.read(cfg,
				StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		System.out.println(Parser.toString(cfgModel));
		epc = adapter.updateInstance(URN_EPC_1, cfgModel);

		Assert.assertEquals(true,
				epc.getResource(URN_EPC_1).getProperty(lteSupportProp)
						.getObject().asLiteral().getBoolean());
	}
}