package org.fiteagle.abstractAdapter;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;
import info.openmultinet.ontology.vocabulary.OpenBaton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.fiteagle.abstractAdapter.dm.AdapterEventListener;
import org.fiteagle.api.core.Config;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.vocabulary.RDF;

import org.fiteagle.api.core.MessageBusOntologyModel;

public abstract class AbstractAdapter {

	private final Logger LOGGER = Logger.getLogger(AbstractAdapter.class
			.getName());

	private List<AdapterEventListener> listeners = new ArrayList<AdapterEventListener>();

	// Defines the adapters TBox
	protected Model adapterTBox;
	// Defines the adapters ABox
	protected Resource adapterABox;
	protected String uuid;

	public AbstractAdapter() {

	}

	public void updateConfig(String adapterName, String configInput)
			throws ProcessingException, IOException {

		LOGGER.log(Level.WARNING, "configInput: " + configInput);

		Config config = new Config(adapterName);
		config.deletePropertiesFile();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			Properties property = gson.fromJson(configInput, Properties.class);
			config.writeProperties(property);
			refreshConfig();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Could not read the JSON serialized Config-File from REST-Interface");
			e.printStackTrace();
		}

	}

	/**
	 * Creates a default properties file
	 *
	 * @param adapterInstanceName
	 */
	public void createDefaultConfiguration(String adapterInstanceName) {
		Config config = new Config(adapterInstanceName);
		config.createPropertiesFile();
	}

	/**
	 *
	 * Checks if the adapter is the recipent of the given message model
	 * 
	 * @param messageModel
	 *            TODO FIX!
	 * @return
	 */
	public boolean isRecipient(Model messageModel) {

		NodeIterator nodeIterator = messageModel
				.listObjectsOfProperty(Omn_lifecycle.implementedBy);
		while (nodeIterator.hasNext()) {
			Resource node = nodeIterator.nextNode().asResource();
			if (node.getURI().equals(this.adapterABox.getURI())) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 *
	 * @param model
	 * @return The currently active instances of the adapter
	 * @throws ProcessingException
	 * @throws InvalidRequestException
	 * @throws InstanceNotFoundException
	 */
	public Model getInstances(Model model) throws ProcessingException,
			InvalidRequestException, InstanceNotFoundException {
		Model instancesModel = ModelFactory.createDefaultModel();
		for (Resource resource : getAdapterManagedResources()) {
			ResIterator resourceInstanceIterator = model
					.listSubjectsWithProperty(RDF.type, resource);
			while (resourceInstanceIterator.hasNext()) {
				String instanceURI = resourceInstanceIterator.next().getURI();
				try {
					Model createdInstance = getInstance(instanceURI);
					instancesModel.add(createdInstance);
				} catch (InstanceNotFoundException e) {
					LOGGER.log(Level.WARNING, "Could not find adapterABox: "
							+ instanceURI);
				}
			}
		}
		if (instancesModel.isEmpty()) {
			throw new InstanceNotFoundException(
					"None of the requested instances could be found");
		}
		return instancesModel;
	}

	/**
	 * Creates a new adapterABox of a resource adapter
	 *
	 * @param model
	 *            Representation of the new adapter adapterABox
	 * @return the newly created adapter adapterABox
	 * @throws ProcessingException
	 * @throws InvalidRequestException
	 */
	public Model createInstances(Model model) throws ProcessingException,
			InvalidRequestException {
		Model createdInstancesModel = ModelFactory.createDefaultModel();

		if (getAdapterManagedResources().isEmpty())
			LOGGER.log(Level.WARNING,
					"To create an instance, I've to manage some :/");
		for (Resource resource : getAdapterManagedResources()) {
			LOGGER.log(Level.INFO, "Creating instance for type: " + resource);
			ResIterator resourceInstanceIterator = model
					.listSubjectsWithProperty(RDF.type, resource);
			while (resourceInstanceIterator.hasNext()) {
				String instanceURI = resourceInstanceIterator.next().getURI();
				LOGGER.log(Level.INFO, "Creating adapterABox: " + instanceURI);
				Model createdInstance = createInstance(instanceURI, model);
				createdInstancesModel.add(createdInstance);
			}
		}

		if (createdInstancesModel.isEmpty()) {
			LOGGER.log(Level.WARNING,
					"Could not find any new instances to create...");
			throw new ProcessingException(Response.Status.CONFLICT.name());
		}
		if(getAdapterABox().hasProperty(RDF.type, OpenBaton.OpenBatonAdapter)){
			startNSR(createdInstancesModel);
		}

		return createdInstancesModel;
	}

	/**
	 * Deletes an adapter adapterABox defined by the model
	 * 
	 * @param model
	 * @return
	 * @throws InvalidRequestException
	 * @throws ProcessingException
	 */
	public Model deleteInstances(Model model) throws InvalidRequestException,
			ProcessingException {
		Model deletedInstancesModel = ModelFactory.createDefaultModel();
		for (Resource resource : getAdapterManagedResources()) {
			ResIterator resourceInstanceIterator = model
					.listSubjectsWithProperty(RDF.type, resource);
			while (resourceInstanceIterator.hasNext()) {
				String instanceURI = resourceInstanceIterator.next().getURI();
				LOGGER.log(Level.INFO, "Deleting adapterABox: " + instanceURI);
				try {
					deleteInstance(instanceURI);
				} catch (InstanceNotFoundException e) {
					LOGGER.log(Level.INFO, "Instance: " + instanceURI
							+ " not found");
					throw new ProcessingException(e);
				}
				Resource deletedInstance = deletedInstancesModel
						.createResource(instanceURI);
				deletedInstance.addProperty(Omn_lifecycle.hasState,
						Omn_lifecycle.Removing);
			}
		}
		return deletedInstancesModel;
	}

	/**
	 *
	 * @param model
	 * @return
	 * @throws InvalidRequestException
	 * @throws ProcessingException
	 * @throws InstanceNotFoundException
	 */
	public Model updateInstances(Model model) throws InvalidRequestException,
			ProcessingException, InstanceNotFoundException {

		LOGGER.log(Level.INFO, "abstract adapter updateInstances");
		LOGGER.log(
				Level.INFO,
				"abstract adapter updateInstances input model"
						+ MessageUtil.serializeModel(model,
								IMessageBus.SERIALIZATION_TURTLE));

		Model updatedInstancesModel = ModelFactory.createDefaultModel();
		for (Resource resource : getAdapterManagedResources()) {
			LOGGER.log(Level.INFO,
					"abstract adapter updateInstances resource: " + resource);

			ResIterator resourceInstanceIterator = model
					.listSubjectsWithProperty(RDF.type, resource);
			while (resourceInstanceIterator.hasNext()) {
				Resource resourceInstance = resourceInstanceIterator.next();
				LOGGER.log(Level.INFO, "Updating adapterABox: "
						+ resourceInstance);

				StmtIterator propertiesIterator = model.listStatements(
						resourceInstance, null, (RDFNode) null);
				Model updateModel = ModelFactory.createDefaultModel();
				while (propertiesIterator.hasNext()) {
					// updateModel.add(propertiesIterator.next());
					// }

					// get the first level of properties fanning out from the
					// resource
					// StmtIterator properties = resource.listProperties();
					// while (properties.hasNext()) {
					Statement property = propertiesIterator.next();
					updateModel.add(property);

					// get the second level of properties fanning out from the
					// resource
					if (property.getObject().isResource()) {
						Resource child = property.getObject().asResource();
						StmtIterator childProperties = child.listProperties();
						while (childProperties.hasNext()) {
							Statement childProperty = childProperties.next();
							updateModel.add(childProperty);

							// get the third level of properties fanning out
							// from
							// the resource
							if (childProperty.getObject().isResource()) {
								Resource grandchild = childProperty.getObject()
										.asResource();
								StmtIterator grandchildProperties = grandchild
										.listProperties();
								while (grandchildProperties.hasNext()) {
									Statement grandchildProperty = grandchildProperties
											.next();
									updateModel.add(grandchildProperty);
								}
							}
						}
					}
				}

				LOGGER.log(
						Level.INFO,
						"abstract adapter updateInstances updatedModel"
								+ MessageUtil.serializeModel(updateModel,
										IMessageBus.SERIALIZATION_TURTLE));

				Model updatedModel = updateInstance(resourceInstance.getURI(),
						updateModel);
				updatedInstancesModel.add(updatedModel);
			}
		}
		if (updatedInstancesModel.isEmpty()) {
			LOGGER.log(Level.INFO, "Could not find any instances to update");
			throw new InstanceNotFoundException(
					"Could not find any instances to update");
		}

		return updatedInstancesModel;
	}

	/**
	 *
	 * @return
	 */
	public List<Resource> getAdapterManagedResources() {
		List<Resource> managedResources = new ArrayList<>();
		StmtIterator iter = this.adapterABox.getModel().listStatements(null,
				Omn_lifecycle.canImplement, (RDFNode) null);
		while (iter.hasNext()) {
			managedResources.add(iter.next().getResource());
		}
		return managedResources;
	}

	/**
	 *
	 * @param eventRDF
	 * @param requestID
	 * @param methodType
	 * @param methodTarget
	 */
	public void notifyListeners(Model eventRDF, String requestID,
			String methodType, String methodTarget) {
		for (AdapterEventListener listener : listeners) {
			listener.publishModelUpdate(eventRDF, requestID, methodType,
					methodTarget);
		}
	}

	/**
	 *
	 * @param newListener
	 */
	public void addListener(AdapterEventListener newListener) {
		listeners.add(newListener);
	}

	public abstract Resource getAdapterABox();
	
	public void startNSR(Model createdInstancesModel){};

	public abstract Model getAdapterDescriptionModel();

	public abstract void updateAdapterDescription() throws ProcessingException;

	public abstract Model updateInstance(String instanceURI,
			Model configureModel) throws InvalidRequestException,
			ProcessingException;

	public abstract Model createInstance(String instanceURI,
			Model newInstanceModel) throws ProcessingException,
			InvalidRequestException;

	public abstract void deleteInstance(String instanceURI)
			throws InstanceNotFoundException, InvalidRequestException,
			ProcessingException;

	public abstract Model getInstance(String instanceURI)
			throws InstanceNotFoundException, ProcessingException,
			InvalidRequestException;

	public abstract Model getAllInstances() throws InstanceNotFoundException,
			ProcessingException;

	public abstract void refreshConfig() throws ProcessingException;

	public String getId() {
		return this.uuid;
	}

	protected void setId(String id) {
		this.uuid = id;
	}

	public abstract void shutdown();

	public abstract void configure(Config configuration);

	public static class InstanceNotFoundException extends Exception {

		private static final long serialVersionUID = 2310151290668732710L;

		public InstanceNotFoundException(String message) {
			super(message);
		}
	}

	public static class InvalidRequestException extends Exception {

		private static final long serialVersionUID = -217391164873287337L;

		public InvalidRequestException(String message) {
			super(message);
		}

		public InvalidRequestException(Throwable cause) {
			super(cause);
		}
	}

	public static class ProcessingException extends Exception {

		private static final long serialVersionUID = 7943720534259771304L;

		public ProcessingException(String message) {
			super(message);
		}

		public ProcessingException(Throwable cause) {
			super(cause);
		}

		public ProcessingException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	protected Resource getAdapterClass() {
		ResIterator resIterator = adapterTBox.listResourcesWithProperty(
				RDFS.subClassOf, MessageBusOntologyModel.classAdapter);
		Resource resourceAdapterClass = null;
		while (resIterator.hasNext()) {
			resourceAdapterClass = resIterator.nextResource();
			break;
		}

		return resourceAdapterClass;
	}

}
