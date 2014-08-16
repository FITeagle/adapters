package org.fiteagle.adapters.motor;

import java.util.HashMap;
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
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init(){
        instanceThreadList = new HashMap<String, Thread>(); 
        this.adapter = MotorAdapter.getInstance();
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

        private final int SLEEP = 5000;
        
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
                adapter.getInstance(instanceName).setRpm(randomRPMGenerator.nextInt(1000));
            }
            return;
        }
    }

}
