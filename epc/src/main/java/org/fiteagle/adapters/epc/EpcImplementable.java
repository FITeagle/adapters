package org.fiteagle.adapters.epc;

import javax.enterprise.concurrent.ManagedThreadFactory;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class EpcImplementable {

	private final transient EpcAdapter owningAdapter;
	private final String instanceName;

	@SuppressWarnings("PMD.DoNotUseThreads")
	private transient Thread thread;
	private final static String THREAD_FACTORY = "java:jboss/ee/concurrency/factory/default";
	private transient ManagedThreadFactory threadFactory;

	public EpcImplementable(final EpcAdapter owningAdapter,
			final String instanceName) {

		this.owningAdapter = owningAdapter;
		this.instanceName = instanceName;
	}

	public String getInstanceName() {
		return this.instanceName;
	}

	public void updateProperty(final Statement configureStatement) {

	}

	public void updateInstance(Resource epcResource) {

	}

	public void parseToModel(Resource resource) {

	}

}
