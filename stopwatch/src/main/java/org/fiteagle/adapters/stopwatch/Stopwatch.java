package org.fiteagle.adapters.stopwatch;

import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hp.hpl.jena.rdf.model.Model;


public class Stopwatch {
    

    private long startTime;
    private long currentTime;
    private int refreshInterval;
    private boolean isRunning;
    
    protected StopWatchAdapter owningAdapter;   
    private String instanceName;
    
    public Stopwatch(){
        super();
        
    }
    
    public Stopwatch(StopWatchAdapter owningAdapter, String instanceName) {
        super();
        this.currentTime = 0;
        this.startTime = 0;
        this.refreshInterval = 1000;
        this.isRunning = false;     
        this.isDynamicThreadRunning = false;
        
        this.owningAdapter = owningAdapter;
        this.instanceName = instanceName;
    }
    
    
    public String toString() {        
        return "Stop Watch";
    }
    
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval, List<String> updatedProperties) {
        this.refreshInterval = refreshInterval;
        updatedProperties.add("refreshInterval");
    }
    
    public void setCurrentTimeWithNotify(long currentTime) {
        this.currentTime = currentTime;
        
        List<String> updatedProperties = new LinkedList<String>();
        updatedProperties.add("currentTime");
        Model changedInstanceValues = owningAdapter.createInformConfigureRDF(instanceName,updatedProperties); 
        owningAdapter.setModelPrefixes(changedInstanceValues);   
        owningAdapter.notifyListeners(changedInstanceValues, "");

    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime, List<String> updatedProperties) {
        this.currentTime = currentTime;
        updatedProperties.add("currentTime");
    }


    public String getInstanceName() {
        return instanceName;
    }


    // SIMULATE DYNAMICALLY CHANGING MOTOR

    private boolean isDynamicThreadRunning;

    public void setIsRunning(boolean state, List<String> updatedProperties) {
        this.isRunning = state;

        try {
            if (this.isRunning && !isDynamicThreadRunning) {
                startStopwatch();

            } else if (!this.isRunning && isDynamicThreadRunning) {
                stopStopwatch();
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        updatedProperties.add("isRunning");
    }
    


    private void startStopwatch() throws NamingException {
        IStopWatchAdapterDynamic dynamicThread;
        dynamicThread = (IStopWatchAdapterDynamic) new InitialContext().lookup("java:module/StopWatchAdapterDynamic");

        dynamicThread.startThread(getInstanceName(), refreshInterval);

        isDynamicThreadRunning = true;
    }

    private void stopStopwatch() throws NamingException {
        IStopWatchAdapterDynamic dynamicThread = (IStopWatchAdapterDynamic) new InitialContext().lookup("java:module/StopWatchAdapterDynamic");
        dynamicThread.endThread(getInstanceName());

        isDynamicThreadRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

}
