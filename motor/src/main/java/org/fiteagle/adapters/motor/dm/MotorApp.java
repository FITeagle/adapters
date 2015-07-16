package org.fiteagle.adapters.motor.dm;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dne on 24.06.15.
 */
@ApplicationPath("/motor")
public class MotorApp extends Application{
    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(MotorAdapterREST.class);
        return s;
    }
}
