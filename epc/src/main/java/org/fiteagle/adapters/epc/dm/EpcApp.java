package org.fiteagle.adapters.epc.dm;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Created by dne on 24.06.15.
 */
@ApplicationPath("/epc")
public class EpcApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
	final Set<Class<?>> classes = new HashSet<Class<?>>();
	classes.add(EpcAdapterREST.class);
	return classes;
    }
}
