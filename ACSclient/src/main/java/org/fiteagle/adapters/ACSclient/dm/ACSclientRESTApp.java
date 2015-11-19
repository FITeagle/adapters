package org.fiteagle.adapters.ACSclient.dm;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/")
public class ACSclientRESTApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
	final Set<Class<?>> classes = new HashSet<Class<?>>();
	classes.add(ACSClientAdapterREST.class);
	return classes;
    }
}