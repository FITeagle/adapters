package org.fiteagle.adapters.motor;

public interface IMotorAdapter {
    public String getAdapterDescription(String serializationFormat);
	public void registerForEvents(IAdapterListener adapterDM);
	public boolean createMotorInstance(int instanceNumber);
}