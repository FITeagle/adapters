package org.fiteagle.adapters.motor.dm;


import java.util.Iterator;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterMDBSender;
import org.fiteagle.adapters.motor.MotorAdapter;

@Singleton
@Startup
public class MotorAdapterMDBSender extends AbstractAdapterMDBSender {

	private static MotorAdapter adapter;
  @Override
  protected AbstractAdapter getAdapter() {
	  if (adapter == null){
		  Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
		  if(iterator.hasNext()){
			  adapter = MotorAdapter.getInstance(iterator.next());
		  }
	  }
	  return adapter;
  }
}
