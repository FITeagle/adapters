package org.fiteagle.adapters.environmentsensor.dm;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Created by dne on 24.06.15.
 */
@ApplicationPath("/environmentsensor")
public class EnvironmentSensorApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
	final Set<Class<?>> classes = new HashSet<Class<?>>();
	classes.add(EnvironmentSensorAdapterREST.class);
	return classes;
    }
}
