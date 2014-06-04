package org.fiteagle.adapters.motor;


import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.core.Application;

import org.fiteagle.adapters.motor.dm.MotorREST;

public class ApplicationConfiguration extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	public ApplicationConfiguration(){

	     singletons.add(new MotorREST());

	}
	
	@Override
	public Set<Class<?>> getClasses() {
	     return empty;
	}
	@Override
	public Set<Object> getSingletons() {
	     return singletons;
	}
}
