package org.fiteagle.adapters.motor.dm;

import java.util.Iterator;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fiteagle.abstractAdapter.dm.AbstractAdapterEJB;
import org.fiteagle.abstractAdapter.dm.IAbstractAdapterEJB;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton(name = "MotorAdapter")
@Remote(IAbstractAdapterEJB.class)
public class MotorAdapterEJB extends AbstractAdapterEJB {

    public MotorAdapterEJB() {
    	if (adapter == null){
  		  Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
  		  if(iterator.hasNext()){
  			  super.adapter = MotorAdapter.getInstance(iterator.next());
  		  }
  	  }
        //super.adapter = MotorAdapter.getInstance();
    }
}
