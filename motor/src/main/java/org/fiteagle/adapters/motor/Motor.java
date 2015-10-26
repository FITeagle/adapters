package org.fiteagle.adapters.motor;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.fiteagle.api.core.IMessageBus;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

public class Motor {

    private String manufacturer;
    private int rpm;
    private int maxRpm;
    private int throttle;
    private final transient MotorAdapter owningAdapter;
    private final String instanceName;
    private transient boolean isDynamicMotor;
    @SuppressWarnings("PMD.DoNotUseThreads")
    private transient Thread thread;
    private final static String THREAD_FACTORY = "java:jboss/ee/concurrency/factory/default";
    private transient ManagedThreadFactory threadFactory;

    private static final Logger LOGGER = Logger.getLogger(Motor.class.toString());

    public Motor(final MotorAdapter owningAdapter, final String instanceName) {
	this.isDynamicMotor = false;
	this.manufacturer = "Fraunhofer FOKUS";
	this.rpm = 0;
	this.maxRpm = 3000;
	this.throttle = 0;

	this.owningAdapter = owningAdapter;
	this.instanceName = instanceName;
    }

    private void setIsDynamic(final boolean state) {
	this.isDynamicMotor = state;

	if (this.isDynamicMotor && !this.threadIsRunning()) {
	    this.startThread();
	} else if (!this.isDynamicMotor && this.threadIsRunning()) {
	    this.terminate();
	}
    }

    private void setRpmWithNotify(final int rpm) {
	this.rpm = rpm;
	final Model resourceModel = this.owningAdapter.parseToModel(this);
	this.owningAdapter.notifyListeners(resourceModel, null, IMessageBus.TYPE_INFORM, null);
    }

    public String getManufacturer() {
	return this.manufacturer;
    }

    private void setManufacturer(final String manufacturer) {
	this.manufacturer = manufacturer;
    }

    public int getRpm() {
	return this.rpm;
    }

    private void setRpm(final int rpm) {
	this.rpm = rpm;
    }

    public int getMaxRpm() {
	return this.maxRpm;
    }

    private void setMaxRpm(final int maxRpm) {
	this.maxRpm = maxRpm;
    }

    public int getThrottle() {
	return this.throttle;
    }

    public void setThrottle(final int throttle) {
	this.throttle = throttle;
    }

    public String getInstanceName() {
	return this.instanceName;
    }

    public boolean isDynamic() {
	return this.isDynamicMotor;
    }

    @SuppressWarnings({ "PMD.GuardLogStatementJavaUtil", "PMD.LongVariable" })
    public void updateProperty(final Statement configureStatement) {
	if (configureStatement.getSubject().getURI().equals(this.instanceName)) {
	    final String predicate = configureStatement.getPredicate().getLocalName();
	    switch (predicate) {
	    case "rpm":
		this.setRpm(configureStatement.getInt());
		break;
	    case "maxRpm":
		this.setMaxRpm(configureStatement.getInt());
		break;
	    case "throttle":
		this.setRpm(configureStatement.getInt());
		break;
	    case "manufacturer":
		this.setManufacturer(configureStatement.getString());
		break;
	    case "isDynamic":
		this.setIsDynamic(configureStatement.getBoolean());
		break;
	    default:
		LOGGER.warning("Unknown predicate: " + predicate);
		break;
	    }
	} else {
	    LOGGER.warning("Unknown URI: " + configureStatement.getSubject().getURI());
	    LOGGER.warning("Expected URI: " + this.instanceName);
	}
    }


    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    private void startThread() {
	final RPMCreator creator = new RPMCreator();
	if (this.threadFactory == null) {
	    Context context;
	    try {
		context = new InitialContext();
		this.threadFactory = (ManagedThreadFactory) context.lookup(Motor.THREAD_FACTORY);
	    } catch (final NamingException e) {
		Motor.LOGGER.log(Level.SEVERE, "Could not create managed thread factory: " + Motor.THREAD_FACTORY);
	    }
	}
	this.thread = this.threadFactory.newThread(creator);
	this.thread.start();
    }

    public void terminate() {
	if (this.thread != null) {
	    this.thread.interrupt();
	}
    }

    private boolean threadIsRunning() {
	return this.thread != null && this.thread.isAlive();
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    public class RPMCreator implements Runnable {

	private static final int SLEEP_TIME = 5000;

	private transient final Random rpmGenerator;

	public RPMCreator() {
	    super();
	    rpmGenerator = new Random();
	}
	
	@Override
	public void run() {
	    while (!Thread.currentThread().isInterrupted()) {
		try {
		    Thread.sleep(RPMCreator.SLEEP_TIME);
		} catch (final InterruptedException e) {
		    return;
		}
		Motor.this.setRpmWithNotify(this.rpmGenerator.nextInt(Motor.this.maxRpm));
	    }
	}
    }

}
