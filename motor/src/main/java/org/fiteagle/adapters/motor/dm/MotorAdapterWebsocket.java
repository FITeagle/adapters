package org.fiteagle.adapters.motor.dm;

import java.util.Iterator;

import javax.websocket.server.ServerEndpoint;

import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.abstractAdapter.dm.AbstractAdapterWebsocket;
import org.fiteagle.adapters.motor.MotorAdapter;

@ServerEndpoint("/websocket")
public class MotorAdapterWebsocket extends AbstractAdapterWebsocket {

private static MotorAdapter adapter;
	
    @Override
    public AbstractAdapter handleSetup() {
    	if(adapter == null){
    		Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
    		if(iterator.hasNext()){
    			adapter = MotorAdapter.getInstance(iterator.next());
    		}
    	}
    	return adapter;
    }
}
