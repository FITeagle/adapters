package org.fiteagle.adapters.stopwatch;

public interface IStopWatchAdapterDynamic {
    
    public void startThread(String instanceName, int refreshInterval);
    public void endThread(String instanceName);

}
