package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn;
import info.openmultinet.ontology.vocabulary.Omn_federation;
import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.Omn_service;

import java.util.HashMap;
import java.util.Map;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageBusOntologyModel;
import org.fiteagle.api.core.OntologyModelUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public final class SshServiceAdapter extends AbstractAdapter {

	private Model adapterModel;
	private Resource adapterInstance;
	private static Resource adapter;
	private SshService sshService;

	public static Map<String, AbstractAdapter> adapterInstances = new HashMap<String, AbstractAdapter>();

	protected HashMap<String, SshService> instanceList = new HashMap<String, SshService>();

	static {
		Model adapterModel = OntologyModelUtil.loadModel(
				"ontologies/sshservice.ttl", IMessageBus.SERIALIZATION_TURTLE);

		ResIterator adapterIterator = adapterModel.listSubjectsWithProperty(
				RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		if (adapterIterator.hasNext()) {
			adapter = adapterIterator.next();
		}

		createDefaultAdapterInstance(adapterModel);
	}

	private static void createDefaultAdapterInstance(Model adapterModel) {
		Resource adapterInstance = adapterModel
				.createResource(OntologyModelUtil.getResourceNamespace()
						+ "PhysicalNodeAdapter-1");
		adapterInstance.addProperty(RDF.type, adapter);
		adapterInstance.addProperty(RDFS.label, adapterInstance.getLocalName());
		adapterInstance.addProperty(RDFS.comment,
				"A SSH Adapter that can create and manage SSH-Accesses.");
		Resource testbed = adapterModel
				.createResource("http://federation.av.tu-berlin.de/about#AV_Smart_Communication_Testbed");
		adapterInstance.addProperty(Omn_federation.partOfFederation, testbed);

		StmtIterator resourceIterator = adapter
				.listProperties(Omn_lifecycle.implements_);
		if (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next().getObject()
					.asResource();

			adapterInstance.addProperty(Omn_lifecycle.parentTo, resource);
			ResIterator propertiesIterator = adapterModel
					.listSubjectsWithProperty(RDFS.domain, resource);
			while (propertiesIterator.hasNext()) {
				Property p = adapterModel.getProperty(propertiesIterator.next()
						.getURI());
			}
		}

		new SshServiceAdapter(adapterInstance, adapterModel, new SshService());
	}

	private SshServiceAdapter(Resource adapterInstance, Model adapterModel,
			SshService sshService) {
		createDefaultConfiguration(adapterInstance.getLocalName());

		Config config = new Config("PhysicalNodeAdapter-1");
		String password = "aA21!7&8*";
		config.setNewProperty("password", password);

		this.adapterInstance = adapterInstance;
		this.adapterModel = adapterModel;
		this.sshService = sshService;
		adapterInstances.put(adapterInstance.getURI(), this);

	}

//	public Model testModel() {
//		Model model = ModelFactory.createDefaultModel();
//		Resource resource = ModelFactory.createDefaultModel().createResource(
//				"TEST-Resource");
//		model.add(
//				resource,
//				model.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-Username>"),
//				"testuseralaa");
//		model.add(
//				resource,
//				model.createProperty("<http://open-multinet.info/ontology/resource/ssh#SSH-PubKey>"),
//				"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDa6JqlE8UOyTagtS91f2Z5DtamUgyMnRZdyliZDXoL6O3jdoVPnernYvrzaRlW1YBPiuPxmv/S7Q7fvXL8CY3ntxGpOHER6EZIUOdHVp/Nu3BFhjJ40Zk/y5geQeJy6NXMqzATkmGGGV9QlGzirC5z2aUHY1UQhWmsE+3zUbw0P6Ic5tH0TcO/zDLY9L5MQwjx5537Q7mskeNaiTLjDZ2jD5wFEQAfNmYJydyyNzwvTNovEgkk2R8usaxH2qtBmmvkkrWdzgOQYsaCGEeHCmSP3FNKyxzymdQnPQetu/BpyBT3YU7zE04HA44Uua4+AjbhDBofPjK89uI1gxQ7a5rh alaa.alloush@air");
//
//		return model;
//	}

	public SshServiceAdapter(String adapterInstanceName) {
		createDefaultConfiguration(adapterInstanceName);
		// TODO Auto-generated constructor stub
	}

	protected Model parseToModel(SshService sshService) {
		Resource resource = ModelFactory.createDefaultModel().createResource(
				sshService.getInstanceName());
		resource.addProperty(RDF.type, getAdapterManagedResources().get(0));
		resource.addProperty(RDF.type, Omn.Resource);
		resource.addProperty(RDFS.label, resource.getLocalName());
		resource.addProperty(Omn_lifecycle.hasState, Omn_lifecycle.Ready);

		return resource.getModel();
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
	public void updateAdapterDescription() throws ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Model updateInstance(String instanceURI, Model configureModel)
			throws InvalidRequestException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model createInstance(String instanceURI, Model newInstanceModel)
			throws ProcessingException, InvalidRequestException {
		String pubKey = "";
		String userName = "";
		Model result = ModelFactory.createDefaultModel();

		ResIterator resIteratorKey = newInstanceModel.listSubjectsWithProperty(Omn_lifecycle.implementedBy);
		if (!resIteratorKey.hasNext())
			throw new InvalidRequestException("statements are missing ");
		while (resIteratorKey.hasNext()) {

			Resource resource = resIteratorKey.nextResource();
			if (!resource.hasProperty(Omn_service.publickey))
				throw new InvalidRequestException("public key is missing ");
			else {
				pubKey = resource
						.getProperty(Omn_service.publickey)
						.getLiteral().getString();
			}
			if (!resource.hasProperty(Omn_service.username))
				throw new InvalidRequestException("user name is missing ");
			else {
				userName = resource
						.getProperty(Omn_service.username)
						.getLiteral().getString();
			}
		}
		sshService.addSshAccess(userName, pubKey);

		return result;
	}

	@Override
	public void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException,
			ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Model getInstance(String instanceURI)
			throws InstanceNotFoundException {
		SshService sshService = instanceList.get(instanceURI);
		if (sshService == null) {
			throw new InstanceNotFoundException("Instance " + instanceURI
					+ " not found");
		}
		return parseToModel(sshService);
	}

	@Override
	public Model getAllInstances() throws InstanceNotFoundException {
		Model model = ModelFactory.createDefaultModel();
		for (String uri : instanceList.keySet()) {
			model.add(getInstance(uri));
		}
		return model;
	}

}
