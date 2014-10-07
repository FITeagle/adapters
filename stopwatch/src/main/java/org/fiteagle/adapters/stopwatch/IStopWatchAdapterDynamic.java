package org.fiteagle.adapters.stopwatch;

public interface IStopWatchAdapterDynamic {
    
    void startThread(String instanceName, int refreshInterval);
    void endThread(String instanceName);

}
