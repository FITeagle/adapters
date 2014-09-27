package org.fiteagle.adapters.stopwatch;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedThreadFactory;

@Singleton(name = "StopWatchAdapterDynamic")
@Remote(IStopWatchAdapterDynamic.class)
public class StopWatchAdapterDynamic implements IStopWatchAdapterDynamic{
    
    // SIMULATE DYNAMICALLY CHANGING STOPWATCH
    private HashMap<String, Thread> instanceThreadList; 

    @Resource(lookup = "java:jboss/ee/concurrency/factory/default")
    private ManagedThreadFactory threadFactory;
    
    private StopWatchAdapter adapter;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init(){
        instanceThreadList = new HashMap<String, Thread>(); 
        this.adapter = StopWatchAdapter.getInstance();
    }

    public void startThread(String instanceName, int refreshInterval) {

            DynamicRPM dynamicRPM = new DynamicRPM(instanceName, refreshInterval);
            Thread newDynamicThread = threadFactory.newThread(dynamicRPM);
            newDynamicThread.start();
            
            instanceThreadList.put(instanceName, newDynamicThread);
    }

    public void endThread(String instanceName){
        instanceThreadList.get(instanceName).interrupt();
    }


    public class DynamicRPM implements Runnable {

        private final int refreshInterval;
        
        private final String instanceName;
        
        public DynamicRPM(String instanceName, int refreshInterval){
            this.instanceName = instanceName;
            this.refreshInterval = refreshInterval;
            adapter.getInstance(instanceName).setStartTime(System.currentTimeMillis());
        }

        @SuppressWarnings("static-access")
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.currentThread().sleep(refreshInterval);
                } catch (InterruptedException e) {
                    return;
                }
                Stopwatch instance = adapter.getInstance(instanceName);
                if(instance != null){
                    Stopwatch currentWatch = adapter.getInstance(instanceName);
                    long currentTime = System.currentTimeMillis() - currentWatch.getStartTime();
                    currentWatch.setCurrentTimeWithNotify(currentTime);

                } else {
                    Thread.currentThread().interrupt();
                    instanceThreadList.remove(instanceName);
                }
            }
        }
    }

}
