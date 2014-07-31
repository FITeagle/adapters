package org.fiteagle.adapters.motor.dm;

public interface IMotorAdapterMDBSender {
    
    public void registerAdapter();
    
    public void unregisterAdapter();

    public void sendMessage(String message);

}
