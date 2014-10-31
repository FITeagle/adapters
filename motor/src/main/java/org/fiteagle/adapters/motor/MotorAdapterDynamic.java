package org.fiteagle.adapters.motor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedThreadFactory;

@Singleton(name = "MotorAdapterDynamic")
@Remote(IMotorAdapterDynamic.class)
public class MotorAdapterDynamic implements IMotorAdapterDynamic{
    
    // SIMULATE DYNAMICALLY CHANGING MOTOR

    private static final Random randomRPMGenerator = new Random();
    
    private HashMap<String, Thread> instanceThreadList; 

    @Resource(lookup = "java:jboss/ee/concurrency/factory/default")
    private ManagedThreadFactory threadFactory;
    
    private MotorAdapter adapter;
    
    @PostConstruct
    private void init(){
        instanceThreadList = new HashMap<String, Thread>(); 
        if (adapter == null){
  		  Iterator<String> iterator = MotorAdapter.motorAdapterInstances.keySet().iterator();
  		  if(iterator.hasNext()){
  			  this.adapter = MotorAdapter.getInstance(iterator.next());
  		  }
  	  }
        //this.adapter = MotorAdapter.getInstance();
    }

    public void startThread(String instanceName) {

            DynamicRPM dynamicRPM = new DynamicRPM(instanceName);
            Thread newDynamicThread = threadFactory.newThread(dynamicRPM);
            newDynamicThread.start();
            
            instanceThreadList.put(instanceName, newDynamicThread);
    }

    public void endThread(String instanceName){
        instanceThreadList.get(instanceName).interrupt();
    }


    public class DynamicRPM implements Runnable {

        private static final int SLEEP = 5000;
        
        private final String instanceName;
        
        public DynamicRPM(String instanceName){
            this.instanceName = instanceName;
                  
        }

        @SuppressWarnings("static-access")
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.currentThread().sleep(SLEEP);
                } catch (InterruptedException e) {
                    return;
                }
                
                Motor instance = adapter.getInstanceName(instanceName);
                if(instance != null){
                    adapter.getInstanceName(instanceName).setRpmWithNotify(randomRPMGenerator.nextInt(1000));

                } else {
                    Thread.currentThread().interrupt();
                    instanceThreadList.remove(instanceName);
                }
            }
            
        }
    }

}
