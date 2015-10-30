package org.fiteagle.adapters.epc;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class UserEquipment {

	// private String manufacturer;
	// private int rpm;
	private boolean lteSupport;
	// private int maxRpm;
	// private int throttle;
	private final transient EpcAdapter owningAdapter;
	private final String instanceName;
	// private transient boolean isDynamicEpc;
	@SuppressWarnings("PMD.DoNotUseThreads")
	private transient Thread thread;
	private final static String THREAD_FACTORY = "java:jboss/ee/concurrency/factory/default";
	private transient ManagedThreadFactory threadFactory;

	private static final Logger LOGGER = Logger.getLogger(UserEquipment.class.toString());

	public UserEquipment(final EpcAdapter owningAdapter, final String instanceName) {
		// this.isDynamicEpc = false;
		// this.manufacturer = "Fraunhofer FOKUS";
		// this.rpm = 0;
		// this.maxRpm = 3000;
		// this.throttle = 0;
		this.lteSupport = false;

		this.owningAdapter = owningAdapter;
		this.instanceName = instanceName;
	}

	// private void setIsDynamic(final boolean state) {
	// this.isDynamicEpc = state;
	//
	// if (this.isDynamicEpc && !this.threadIsRunning()) {
	// this.startThread();
	// } else if (!this.isDynamicEpc && this.threadIsRunning()) {
	// this.terminate();
	// }
	// }
	//
	// private void setRpmWithNotify(final int rpm) {
	// this.rpm = rpm;
	// final Model resourceModel = this.owningAdapter.parseToModel(this);
	// this.owningAdapter.notifyListeners(resourceModel, null,
	// IMessageBus.TYPE_INFORM, null);
	// }
	//
	// public String getManufacturer() {
	// return this.manufacturer;
	// }
	//
	// private void setManufacturer(final String manufacturer) {
	// this.manufacturer = manufacturer;
	// }

	// public int getRpm() {
	// return this.rpm;
	// }
	//
	// private void setRpm(final int rpm) {
	// LOGGER.info("Setting RPM: " + rpm);
	// this.rpm = rpm;
	// }

	public boolean isLteSupport() {
		return this.lteSupport;
	}

	private void setLteSupport(final boolean lteSupport) {
		LOGGER.info("Setting LTE Suport: " + lteSupport);
		this.lteSupport = lteSupport;
	}

	// public int getMaxRpm() {
	// return this.maxRpm;
	// }
	//
	// private void setMaxRpm(final int maxRpm) {
	// this.maxRpm = maxRpm;
	// }
	//
	// public int getThrottle() {
	// return this.throttle;
	// }
	//
	// public void setThrottle(final int throttle) {
	// this.throttle = throttle;
	// }

	public String getInstanceName() {
		return this.instanceName;
	}

	// public boolean isDynamic() {
	// return this.isDynamicEpc;
	// }

	@SuppressWarnings({ "PMD.GuardLogStatementJavaUtil", "PMD.LongVariable" })
	public void updateProperty(final Statement configureStatement) {
		if (configureStatement.getSubject().getURI().equals(this.instanceName)) {
			final String predicate = configureStatement.getPredicate()
					.getLocalName();
			switch (predicate) {
			case "lteSupport":
				this.setLteSupport(configureStatement.getObject().asLiteral().getBoolean());
				break;
			// case "rpm":
			// this.setRpm(configureStatement.getObject().asLiteral().getInt());
			// break;
			// case "maxRpm":
			// this.setMaxRpm(configureStatement.getObject().asLiteral()
			// .getInt());
			// break;
			// case "throttle":
			// this.setRpm(configureStatement.getObject().asLiteral().getInt());
			// break;
			// case "manufacturer":
			// this.setManufacturer(configureStatement.getObject().asLiteral()
			// .getString());
			// break;
			// case "isDynamic":
			// this.setIsDynamic(configureStatement.getObject().asLiteral()
			// .getBoolean());
			// break;
			default:
				LOGGER.warning("Unknown predicate: " + predicate);
				break;
			}
		} else {
			LOGGER.warning("Unknown URI: "
					+ configureStatement.getSubject().getURI());
			LOGGER.warning("Expected URI: " + this.instanceName);
		}
	}

	// @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
	// private void startThread() {
	// final RPMCreator creator = new RPMCreator();
	// if (this.threadFactory == null) {
	// Context context;
	// try {
	// context = new InitialContext();
	// this.threadFactory = (ManagedThreadFactory) context
	// .lookup(Epc.THREAD_FACTORY);
	// } catch (final NamingException e) {
	// Epc.LOGGER.log(Level.SEVERE,
	// "Could not create managed thread factory: "
	// + Epc.THREAD_FACTORY);
	// }
	// }
	// this.thread = this.threadFactory.newThread(creator);
	// this.thread.start();
	// }
	//
	// public void terminate() {
	// if (this.thread != null) {
	// this.thread.interrupt();
	// }
	// }
	//
	// private boolean threadIsRunning() {
	// return this.thread != null && this.thread.isAlive();
	// }

	// @SuppressWarnings("PMD.DoNotUseThreads")
	// public class RPMCreator implements Runnable {
	//
	// private static final int SLEEP_TIME = 5000;
	//
	// private transient final Random rpmGenerator;
	//
	// public RPMCreator() {
	// super();
	// rpmGenerator = new Random();
	// }
	//
	// @Override
	// public void run() {
	// while (!Thread.currentThread().isInterrupted()) {
	// try {
	// Thread.sleep(RPMCreator.SLEEP_TIME);
	// } catch (final InterruptedException e) {
	// return;
	// }
	// Epc.this.setRpmWithNotify(this.rpmGenerator
	// .nextInt(Epc.this.maxRpm));
	// }
	// }
	// }

}
