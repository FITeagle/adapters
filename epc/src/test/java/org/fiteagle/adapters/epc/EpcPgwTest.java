package org.fiteagle.adapters.epc;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.MissingRspecElementException;
import info.openmultinet.ontology.translators.geni.RequestConverter;
import info.openmultinet.ontology.vocabulary.Epc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.jena.riot.Lang;
import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;
import org.fiteagle.adapters.epc.model.UserEquipment;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class EpcPgwTest {

	@Test
	public void testCreateMultipleNodes() throws JAXBException,
			InvalidModelException, MissingRspecElementException,
			ProcessingException, InvalidRequestException,
			InstanceNotFoundException, IOException {

		EpcAdapter epcAdapter = createAdapterInstance();

		final InputStream rspec2 = EpcPgwTest.class
				.getResourceAsStream("/pgw.xml");
		String theString = IOUtils.toString(rspec2, "UTF-8");
		System.out.println(theString);

		final InputStream rspec = EpcPgwTest.class
				.getResourceAsStream("/pgw.xml");
		final Model model = RequestConverter.getModel(rspec);

		String modelString = MessageUtil.serializeModel(model,
				IMessageBus.SERIALIZATION_TURTLE);
		System.out.println("********** input model*************");
		System.out.println(modelString);

		epcAdapter.createInstances(model);
		Model allInstances = epcAdapter.getAllInstances();
		String allInstancesString = MessageUtil.serializeModel(
				epcAdapter.getAllInstances(), IMessageBus.SERIALIZATION_TURTLE);
		System.out
				.println("********** instances created in adapter*************");
		System.out.println(allInstancesString);

		String statementString3 = "<http://open-multinet.info/example#epc1> <"
				+ RDF.type.toString()
				+ "> <http://open-multinet.info/ontology/resource/epc#EvolvedPacketCore>";
		final Statement statement3 = getStatement(statementString3);
		Assert.assertTrue(allInstances.contains(statement3));

	}

	private Statement getStatement(final String string)
			throws UnsupportedEncodingException {
		final ByteArrayInputStream bais = new ByteArrayInputStream(
				string.getBytes(StandardCharsets.UTF_8));
		final Model model = ModelFactory.createDefaultModel();
		model.read(bais, StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		return model.listStatements().next();
	}

	private EpcAdapter createAdapterInstance() {

		final Model defaultModel = ModelFactory.createDefaultModel();
		final InputStream tbox = EpcPgwTest.class
				.getResourceAsStream("/ontologies/epc.ttl");
		final Model adapterTBox = defaultModel.read(tbox,
				StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		final Resource adapterABox = defaultModel
				.createResource("http://open-multinet.info/ontology/resource/epc#EpcAdapter-1");
		adapterABox.addProperty(
				defaultModel.createProperty(Epc.getURI(), "pgwIp"), "pgwIp1");
		adapterABox.addProperty(
				defaultModel.createProperty(Epc.getURI(), "pgwStart"), "pgwStart1");
		adapterABox.addProperty(
				defaultModel.createProperty(Epc.getURI(), "pgwStop"), "pgwStop1");
		System.out.println("***********adapter a box");
		System.out.println(MessageUtil.serializeModel(adapterABox.getModel(),
				IMessageBus.SERIALIZATION_TURTLE));
		final EpcAdapter adapter = new EpcAdapter(adapterTBox, adapterABox);

		return adapter;
	}
}
