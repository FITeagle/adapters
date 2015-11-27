package org.fiteagle.adapters.epc;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.exceptions.MissingRspecElementException;
import info.openmultinet.ontology.translators.geni.RequestConverter;

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
import org.fiteagle.adapters.epc.model.EvolvedPacketCore;
import org.fiteagle.adapters.epc.model.UserEquipment;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class UpdateInstanceTest {

	@Test
	public void testCreateMultipleNodes() throws JAXBException,
			InvalidModelException, MissingRspecElementException,
			ProcessingException, InvalidRequestException,
			InstanceNotFoundException, IOException {

		String instanceURI = "http://localhost/resource/EpcAdapter-1/5c94f8e6-1887-432b-83a2-36cba5a6ee89";
		EpcAdapter epcAdapter = createAdapterInstance();
		final EvolvedPacketCore epc = new EvolvedPacketCore(epcAdapter,
				instanceURI);

		// get the update model, carry out the update and check that lteSupport
		// has the new value (true)
		final Model cfgModel = ModelFactory.createDefaultModel();
		final InputStream cfg = EpcAdapterTest.class
				.getResourceAsStream("/config.ttl");
		cfgModel.read(cfg, StandardCharsets.UTF_8.name(), Lang.TTL.getName());

		epcAdapter.addInstance(instanceURI, epc);
		System.out.println("#######creteinstance modelCreate: "
				+ MessageUtil.serializeModel(cfgModel,
						IMessageBus.SERIALIZATION_TURTLE));
		epcAdapter.updateInstance(instanceURI, cfgModel);

		EpcGeneric instance = epcAdapter.getInstanceByName(instanceURI);
		EvolvedPacketCore epcInstance = (EvolvedPacketCore) instance;
		
		
		System.out.println(epcInstance.getMmeAddress() + "###########*********");
		Model blah = epcAdapter.parseToModel(epc);

		System.out.println("***********parse to model");
		System.out.println(MessageUtil.serializeModel(blah,
				IMessageBus.SERIALIZATION_TURTLE));
	}

	private EpcAdapter createAdapterInstance() {

		final Model defaultModel = ModelFactory.createDefaultModel();
		final InputStream tbox = UpdateInstanceTest.class
				.getResourceAsStream("/ontologies/epc.ttl");
		final Model adapterTBox = defaultModel.read(tbox,
				StandardCharsets.UTF_8.name(), Lang.TTL.getName());
		final Resource adapterABox = defaultModel
				.createResource("http://www.test.com/EpcAdapter-1");
		// System.out.println("***********adapter a box");
		// System.out.println(MessageUtil.serializeModel(adapterABox.getModel(),
		// IMessageBus.SERIALIZATION_TURTLE));
		final EpcAdapter adapter = new EpcAdapter(adapterTBox, adapterABox);

		return adapter;
	}
}
