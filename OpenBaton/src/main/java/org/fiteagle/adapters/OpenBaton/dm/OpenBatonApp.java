package org.fiteagle.adapters.OpenBaton.dm;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Created by dne on 24.06.15.
 */
@ApplicationPath("/")
public class OpenBatonApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
	final Set<Class<?>> classes = new HashSet<Class<?>>();
	classes.add(OpenBatonAdapterREST.class);
	return classes;
    }
}
