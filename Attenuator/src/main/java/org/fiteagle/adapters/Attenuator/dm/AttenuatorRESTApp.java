package org.fiteagle.adapters.Attenuator.dm;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;


@ApplicationPath("/")
public class AttenuatorRESTApp extends Application{
  
  @Override
  public Set<Class<?>> getClasses() {
final Set<Class<?>> classes = new HashSet<Class<?>>();
classes.add(AttenuatorAdapterREST.class);
return classes;
  }
  
  
}
