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
    
    private HashMap<Integer, Thread> instanceThreadList; 

    @Resource(lookup = "java:jboss/ee/concurrency/factory/default")
    private ManagedThreadFactory threadFactory;
    
    private MotorAdapter adapter;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init(){
        instanceThreadList = new HashMap<Integer, Thread>(); 
        this.adapter = MotorAdapter.getInstance();
    }

    public void startThread(int instanceID) {

            DynamicRPM dynamicRPM = new DynamicRPM(instanceID);
            Thread newDynamicThread = threadFactory.newThread(dynamicRPM);
            newDynamicThread.start();
            
            instanceThreadList.put(instanceID, newDynamicThread);
    }

    public void endThread(int instanceID){
        instanceThreadList.get(instanceID).interrupt();
    }


    public class DynamicRPM implements Runnable {

        private final int SLEEP = 5000;
        
        private final int instanceID;
        
        public DynamicRPM(int instanceID){
            this.instanceID = instanceID;
                  
        }

        @SuppressWarnings("static-access")
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.currentThread().sleep(SLEEP);
                } catch (InterruptedException e) {
                   // e.printStackTrace();
                    return;

                }
                adapter.getInstance(instanceID).setRpm(randomRPMGenerator.nextInt(5000));
            }
            return;
        }
    }

}
